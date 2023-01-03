package com.example.timehelm.logic

import com.example.timehelm.state.Settings
import com.example.timehelm.state.State
import com.google.protobuf.Timestamp
import java.time.LocalDate


fun Timestamp.realDay(startOfDay: Int): LocalDate {
  return toBuilder().setSeconds(seconds - (startOfDay.toLong() * 3600)).build().toLocalDate()
}

fun State.firstOpen(startOfDay: Int): Boolean {
  return lastDayStreak != Timestamp.getDefaultInstance()
          && (lastDayStreak.realDay(startOfDay).dayOfYear
          != now().realDay(startOfDay).dayOfYear)
}

fun LocalDate.isPrevDay(today: LocalDate): Boolean {
  return plusDays(1).dayOfYear == today.dayOfYear
}

fun State.Builder.onFirstOpen(toast: Toaster, settings: Settings): State.Builder {
  val now = now()
  // if we forgot to stop tracking yesterday
  if (isTracking) {
    toast("Forgot to stop tracking, stopping for you.")
    timeWorked = elapsedTime(now)
    clearStartTime()
    isTracking = false
  }

  // xp
  prevXp += xpGoalsMap.count { it.value }
  clearXpGoals()

  // streak
  //  lastDayStreak = now.toBuilder().setSeconds(now.seconds - (24 * 60 * 60)).build()
  //  toast(lastDayStreak.realDay(startOfDay).toString() + " to " + now.toLocalDate().toString())
  if (lastDayStreak.realDay(settings.startOfDay).isPrevDay(now.realDay(settings.startOfDay))
    && (settings.dailyHoursMin..settings.dailyHoursMax).contains(timeWorked.hours)) {
    streakDays++
    // could add more fun stuff here
  } else {
    toast("Streak lost!")
    clearStreakDays()
  }
  lastDayStreak = now

  //time tracked
  clearTimeWorked()

  return this
}

