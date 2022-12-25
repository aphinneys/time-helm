package com.example.timehelm.logic

import com.example.timehelm.state.State
import com.example.timehelm.state.StateUpdate
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.chrono.ChronoLocalDateTime

// GOALS
//Note: All XP bonuses are cumulative. So like, if you hit 100% of goal by noon for example you get the noon 3pm and 6pm XP, similarly if you work for 3hours you automatically get the 90 and 45 XP as well.

//Punching in for the second time that day
private const val SECOND_CHECK_IN = "SECOND_CHECK_IN"

//Starting work before 10am
private const val START_PRE_10 = "START_PRE_10"

//Being 25% of the way to goal minimum hours by noon
private const val PROGRESS_NOON = "PROGRESS_NOON"

//Being 50% of the way to goal minimum hours by 3pm
private const val PROGRESS_3PM = "PROGRESS_3PM"

//Being 75% of the way to goal minimum hours by 6pm
private const val PROGRESS_6PM = "PROGRESS_6PM"

//Working for 45 minutes at a stretch (e.g. a single “I’m working” session length)
private const val STREAK_45MIN = "STREAK_45MIN"

//Working for 90 minutes at a stretch
private const val STREAK_90MIN = "STREAK_90MIN"

//Working for 180 minutes (3 hr) at a stretch
private const val STREAK_180MIN = "STREAK_180MIN"

private val messages = hashMapOf(
  SECOND_CHECK_IN to "Second punch in of the day",
  START_PRE_10 to "Started before 10am",
  PROGRESS_NOON to "25% of goal minimum by noon",
  PROGRESS_3PM to "50% of goal minimum by 3pm",
  PROGRESS_6PM to "75% of goal minimum by 6pm",
  STREAK_45MIN to "Worked for a 45 min stretch",
  STREAK_90MIN to "Worked for a 90 min stretch",
  STREAK_180MIN to "Worked for a 180 min stretch",
)

val State.xp: Int
  get() = this.prevXp + this.xpGoalsMap.count { it.value }

// potentially just use a hashmap

fun Map<String, Boolean>.completed(key: String): Boolean {
  return this.getOrDefault(key, false)
}

fun Map<String, Boolean>.notCompleted(key: String): Boolean {
  return !this.completed(key)
}

fun dateTime(time: LocalTime): ChronoLocalDateTime<*> {
  return ChronoLocalDateTime.from(ZonedDateTime.of(LocalDate.now(), time, ZoneId.systemDefault()))
}

fun time10am(): ChronoLocalDateTime<*> {
  return dateTime(LocalTime.of(10, 0))
}

fun timeNoon(): ChronoLocalDateTime<*> {
  return dateTime(LocalTime.NOON)
}

fun time3pm(): ChronoLocalDateTime<*> {
  return dateTime(LocalTime.of(15, 0))
}

fun time6pm(): ChronoLocalDateTime<*> {
  return dateTime(LocalTime.of(18, 0))
}

fun State.checkGoals(update: StateUpdate, toast: Toaster) {
  val goals = HashMap(this.xpGoalsMap)
  val time = LocalDateTime.now()


  // pre 10am
  if (goals.notCompleted(START_PRE_10)
    && this.elapsedTime(now()).seconds > 0
    && time.isBefore(time10am())
  ) {
    goals[START_PRE_10] = true
  }

  //

  val diff = HashSet(goals.filterValues { it }.keys)
    .subtract(HashSet(this.xpGoalsMap.filterValues { it }.keys))
  if (diff.isNotEmpty()) {
    update { it.putAllXpGoals(goals) }
    toast("Gained xp for: " + diff.map { messages[it] }.joinToString())
  }
}

fun State.Builder.checkSecondCheckInGoal() {
  if (this.xpGoalsMap.notCompleted(SECOND_CHECK_IN)
    && !this.isTracking // we are now checking in
    && this.timeWorked.seconds > 0) {
    this.putXpGoals(SECOND_CHECK_IN, true)
  }
}

fun State.Builder.startDayXp() {
  this.prevXp += this.xpGoalsMap.count { it.value }
  this.clearXpGoals()
}





