package com.example.timehelm.state

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream

// TIME STATE

object StateSerializer : Serializer<State> {
  override val defaultValue: State = State.getDefaultInstance()

  override suspend fun readFrom(input: InputStream): State {
    try {
      return State.parseFrom(input)
    } catch (exception: InvalidProtocolBufferException) {
      throw CorruptionException("Cannot read proto.", exception)
    }
  }

  override suspend fun writeTo(
    t: State,
    output: OutputStream
  ) = t.writeTo(output)
}

val Context.stateDataStore: DataStore<State> by dataStore(
  fileName = "state.pb",
  serializer = StateSerializer
)

typealias StateUpdate = ((State.Builder) -> State.Builder) -> Unit

fun useUpdateState(
  scope: CoroutineScope,
  context: Context
): StateUpdate {
  return { update ->
    scope.launch {
      context.stateDataStore.updateData { currentState ->
        currentState.toBuilder().also { update(it) }.build()
      }
    }
  }
}

// SETTINGS

object SettingsSerializer : Serializer<Settings> {
  override val defaultValue: Settings = Settings.getDefaultInstance()

  override suspend fun readFrom(input: InputStream): Settings {
    try {
      return Settings.parseFrom(input)
    } catch (exception: InvalidProtocolBufferException) {
      throw CorruptionException("Cannot read proto.", exception)
    }
  }

  override suspend fun writeTo(
    t: Settings,
    output: OutputStream
  ) = t.writeTo(output)
}

val Context.settingsDataStore: DataStore<Settings> by dataStore(
  fileName = "settings.pb",
  serializer = SettingsSerializer
)

typealias SettingsUpdate = ((Settings.Builder) -> Settings.Builder) -> Unit

fun useUpdateSettings(
  scope: CoroutineScope,
  context: Context
): SettingsUpdate {
  return { update ->
    scope.launch {
      context.settingsDataStore.updateData { currentSettings ->
        currentSettings.toBuilder().also { update(it) }.build()
      }
    }
  }
}

fun Settings.isFullyInitialized(): Boolean {
  return isInitialized && dailyHoursMax >= 0 && dailyHoursMin >= 0 && dailyHoursMax >= 0 && difficultyAvg >= 0 && difficultyVariance >= 0
}

// POKEMON STATE

object PokemonSerializer : Serializer<PokemonState> {
  override val defaultValue: PokemonState = PokemonState.getDefaultInstance()

  override suspend fun readFrom(input: InputStream): PokemonState {
    try {
      return PokemonState.parseFrom(input)
    } catch (exception: InvalidProtocolBufferException) {
      throw CorruptionException("Cannot read proto.", exception)
    }
  }

  override suspend fun writeTo(
    t: PokemonState,
    output: OutputStream
  ) = t.writeTo(output)
}

val Context.pokemonDataStore: DataStore<PokemonState> by dataStore(
  fileName = "pokemon.pb",
  serializer = PokemonSerializer
)

typealias PokemonUpdate = ((PokemonState.Builder) -> PokemonState.Builder) -> Unit

fun useUpdatePokemon(
  scope: CoroutineScope,
  context: Context
): PokemonUpdate {
  return { update ->
    scope.launch {
      context.pokemonDataStore.updateData { currentPokemon ->
        currentPokemon.toBuilder().also { update(it) }.build()
      }
    }
  }
}
