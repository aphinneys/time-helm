package com.example.timehelm.logic

import android.content.Context
import android.widget.Toast
import com.example.timehelm.state.State
import com.example.timehelm.state.StateOrBuilder
import com.google.protobuf.Timestamp
import java.lang.Integer.max
import java.lang.Long.min
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

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

val Timestamp.hours: Long
  get() = seconds / 3600

val Timestamp.minutes: Long
  get() = seconds / 60

fun Timestamp.toLocalDate(): LocalDate {
  return Instant.ofEpochSecond(seconds).atZone(ZoneId.systemDefault()).toLocalDate();
}

fun Timestamp.add(other: Timestamp): Timestamp {
  return toBuilder()
    .setSeconds(seconds + other.seconds)
    .setNanos(nanos + other.nanos).build()
}

fun Timestamp.diff(other: Timestamp): Timestamp {
  return toBuilder()
    .setSeconds(seconds - other.seconds)
    .setNanos(nanos - other.nanos).build()
}

fun StateOrBuilder.elapsedTime(now: Timestamp): Timestamp {
  if (isTracking) {
    return timeWorked.add(now.diff(startTime))
  }
  return timeWorked
}

// Don't show more than 100 hours
fun State.elapsedHours(now: Timestamp): Int {
  return min(100, elapsedTime(now).seconds / 3600).toInt()
}

fun State.elapsedMins(now: Timestamp): Int {
  return ((elapsedTime(now).seconds / 60) % 60).toInt()
}
