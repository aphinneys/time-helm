package com.timehelm.timehelm.logic

import com.google.protobuf.Timestamp
import com.timehelm.timehelm.state.Settings
import com.timehelm.timehelm.state.State
import java.time.LocalDate
import android.icu.util.Calendar

const val START_OF_DAY = 3

fun Timestamp.realDay(startOfDay: Int): LocalDate {
  return toBuilder().setSeconds(seconds - (startOfDay.toLong() * 3600)).build().toLocalDate()
}

fun isSaturday(): Boolean {
  return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
}

fun State.firstOpen(startOfDay: Int, toast: Toaster): Boolean {
  //  toast("$ld to $nd, " + (nd != ld))
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
  prevXp += todayXp
  clearXpGoals()

  // streak
  //  lastDayStreak = now.toBuilder().setSeconds(now.seconds - (24 * 60 * 60)).build()
  //  toast(lastDayStreak.realDay(startOfDay).toString() + " to " + now.toLocalDate().toString())
  if (lastDayStreak.realDay(START_OF_DAY).isPrevDay(now.realDay(START_OF_DAY))
    && (settings.dailyHoursMin..settings.dailyHoursMax).contains(timeWorked.hours)) {
    streakDays++
    // could add more fun stuff here
  } else if (!(settings.shabbat && isSaturday())) {
    toast("Streak lost!")
    clearStreakDays()
  }
  lastDayStreak = now

  //time tracked
  clearTimeWorked()

  return this
}
