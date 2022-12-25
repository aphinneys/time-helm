package com.example.timehelm.logic

import android.content.Context
import android.widget.Toast
import com.example.timehelm.state.State
import com.google.protobuf.Timestamp
import java.lang.Integer.max
import java.lang.Long.min
import java.time.LocalDate

typealias Toaster = (String) -> Unit

fun useToast(context: Context, duration: Int): Toaster {
  return { Toast.makeText(context, it, duration).show() }
}

fun fmtTime(hours: Int, minutes: Int): String {
  return "%02d:%02d".format(hours, minutes)
}

fun Int.value() = max(this, 0)

fun now(): Timestamp {
  return Timestamp.getDefaultInstance().toBuilder()
    .setSeconds(System.currentTimeMillis() / 1000).build()
}

fun Timestamp.add(other: Timestamp): Timestamp {
  return this.toBuilder()
    .setSeconds(this.seconds + other.seconds)
    .setNanos(this.nanos + other.nanos).build()
}

fun Timestamp.diff(other: Timestamp): Timestamp {
  return this.toBuilder()
    .setSeconds(this.seconds - other.seconds)
    .setNanos(this.nanos - other.nanos).build()
}

fun State.Builder.elapsedTime(now: Timestamp): Timestamp {
  return this.build().elapsedTime(now)
}

fun State.elapsedTime(now: Timestamp): Timestamp {
  if (this.isTracking) {
    return this.timeWorked.add(now.diff(this.startTime))
  }
  return this.timeWorked
}

// Don't show more than 100 hours
fun State.elapsedHours(now: Timestamp): Int {
  return min(100, this.elapsedTime(now).seconds / 3600).toInt()
}

fun State.elapsedMins(now: Timestamp): Int {
  return ((this.elapsedTime(now).seconds / 60) % 60).toInt()
}

fun State.firstOpen(): Boolean {
  return false
}

fun State.onFirstOpen() {

}

