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

fun isSettingsInitialized(s: Settings): Boolean {
  return s.isInitialized && s.dailyHoursMax >= 0 && s.dailyHoursMin >= 0 && s.dailyHoursMax >= 0 && s.difficultyAvg >= 0 && s.difficultyVariance >= 0
}
