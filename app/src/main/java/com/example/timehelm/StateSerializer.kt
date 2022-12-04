package com.example.timehelm
import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object TimeStateSerializer : Serializer<TimeState> {
    override val defaultValue: TimeState = TimeState.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): TimeState {
        try {
            return TimeState.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: TimeState,
        output: OutputStream) = t.writeTo(output)
}

val Context.stateDataStore: DataStore<TimeState> by dataStore(
    fileName = "settings.pb",
    serializer = TimeStateSerializer
)

