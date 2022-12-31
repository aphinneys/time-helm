package com.example.timehelm.state

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import com.google.protobuf.MessageLite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream


@Suppress("UNCHECKED_CAST")
fun <T : MessageLite, B : MessageLite.Builder> updateHook(
  scope: CoroutineScope,
  context: Context,
  accessState: (Context) -> DataStore<T>,
): (((B) -> B) -> Unit) {
  return { update ->
    scope.launch {
      accessState(context).updateData { currentState ->
        currentState.toBuilder().also { update(it as B) }.build() as T
      }
    }
  }
}

open class Serialize<T : MessageLite>(default: () -> T, val parseFrom: (InputStream) -> T) :
  Serializer<T> {
  override val defaultValue: T = default()
  override suspend fun readFrom(input: InputStream): T {
    try {
      return parseFrom(input)
    } catch (exception: InvalidProtocolBufferException) {
      throw CorruptionException("Cannot read proto.", exception)
    }
  }

  override suspend fun writeTo(
    t: T,
    output: OutputStream
  ) = t.writeTo(output)
}

// TIME STATE

object StateSerializer : Serialize<State>({ State.getDefaultInstance() }, { State.parseFrom(it) })
val Context.stateDataStore: DataStore<State> by dataStore("state.pb", StateSerializer)
typealias StateUpdate = ((State.Builder) -> State.Builder) -> Unit
fun useUpdateState(scope: CoroutineScope, context: Context): StateUpdate =
  updateHook(scope, context) { it.stateDataStore }

// SETTINGS

object SettingsSerializer : Serialize<Settings>({Settings.getDefaultInstance()}, {Settings.parseFrom(it)})
val Context.settingsDataStore: DataStore<Settings> by dataStore("settings.pb", SettingsSerializer)
typealias SettingsUpdate = ((Settings.Builder) -> Settings.Builder) -> Unit
fun useUpdateSettings(scope: CoroutineScope, context: Context): SettingsUpdate =
  updateHook(scope, context) { it.settingsDataStore }

fun Settings.isFullyInitialized(): Boolean {
  return isInitialized && dailyHoursMax >= 0 && dailyHoursMin >= 0 && dailyHoursMax >= 0 && difficultyAvg >= 0 && difficultyVariance >= 0
}

// POKEMON STATE

object PokemonSerializer : Serialize<PokemonState>( {PokemonState.getDefaultInstance()}, {PokemonState.parseFrom(it)})
val Context.pokemonDataStore: DataStore<PokemonState> by dataStore("pokemon.pb", PokemonSerializer)
typealias PokemonUpdate = ((PokemonState.Builder) -> PokemonState.Builder) -> Unit
fun useUpdatePokemon(scope: CoroutineScope, context: Context): PokemonUpdate =
  updateHook(scope, context) {it.pokemonDataStore}