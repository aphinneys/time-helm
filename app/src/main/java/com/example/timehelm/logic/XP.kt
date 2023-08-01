package com.example.timehelm.logic

import com.example.timehelm.state.Settings
import com.example.timehelm.state.State
import com.example.timehelm.state.StateUpdate
import java.time.*
import java.time.chrono.ChronoLocalDateTime

// GOALS
//Note: All XP bonuses are cumulative. So like, if you hit 100% of goal by noon for example you get the noon 3pm and 6pm XP, similarly if you work for 3hours you automatically get the 90 and 45 XP as well.

//Punching in for the second time that day
private const val SECOND_CHECK_IN = "SECOND_CHECK_IN"

//Starting work before 10am
private const val START_PRE_10 = "START_PRE_10"

//Being 25% of the way to goal minimum hours by noon
private const val PROGRESS_2HR = "PROGRESS_NOON"

//Being 50% of the way to goal minimum hours by 3pm
private const val PROGRESS_5HR = "PROGRESS_3PM"

//Being 75% of the way to goal minimum hours by 6pm
private const val PROGRESS_8HR = "PROGRESS_6PM"

//Working for 45 minutes at a stretch (e.g. a single “I’m working” session length)
private const val SESSION_45MIN = "STREAK_45MIN"

//Working for 90 minutes at a stretch
private const val SESSION_90MIN = "STREAK_90MIN"

//Working for 180 minutes (3 hr) at a stretch
private const val SESSION_180MIN = "STREAK_180MIN"

private val messages = hashMapOf(
  SECOND_CHECK_IN to "Second punch in of the day",
  START_PRE_10 to "Started by work start time",
  PROGRESS_2HR to "25% of goal minimum by 2hrs",
  PROGRESS_5HR to "50% of goal minimum by 5hrs",
  PROGRESS_8HR to "75% of goal minimum by 8hrs",
  SESSION_45MIN to "Worked for a 45 min stretch",
  SESSION_90MIN to "Worked for a 90 min stretch",
  SESSION_180MIN to "Worked for a 180 min stretch",
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

fun State.checkTimeCompletionGoal(
  goals: HashMap<String, Boolean>,
  completionGoal: String,
  dailyHoursMin: Int,
  time: Int,
  progress: Float
) {
  if (goals.notCompleted(completionGoal)
    && LocalDateTime.now().isBefore(dateTime(LocalTime.of(time, 0)))
    && elapsedTime(now()).seconds > dailyHoursMin * 3600 * progress
  ) {
    goals[completionGoal] = true
  }
}

fun State.checkSessionLengthGoal(
  goals: HashMap<String, Boolean>,
  sessionGoal: String,
  lengthMinutes: Int,
) {
  if (goals.notCompleted(sessionGoal)
    && isTracking
    && now().diff(startTime).minutes > lengthMinutes) {
    goals[sessionGoal] = true
  }

}

fun State.checkGoals(update: StateUpdate, settings: Settings, toast: Toaster) {
  val goals = HashMap(xpGoalsMap)

  // time of day based goals
  checkTimeCompletionGoal(goals, START_PRE_10, settings.dailyHoursMin, settings.workStart + 0, 0f)
  checkTimeCompletionGoal(goals, PROGRESS_2HR, settings.dailyHoursMin, settings.workStart + 2, .25f)
  checkTimeCompletionGoal(goals, PROGRESS_5HR, settings.dailyHoursMin, settings.workStart + 5, .5f)
  checkTimeCompletionGoal(goals, PROGRESS_8HR, settings.dailyHoursMin, settings.workStart + 8, .75f)

  // session based goals
  checkSessionLengthGoal(goals, SESSION_45MIN, 45)
  checkSessionLengthGoal(goals, SESSION_90MIN, 90)
  checkSessionLengthGoal(goals, SESSION_180MIN, 180)

  val diff = HashSet(goals.filterValues { it }.keys)
    .subtract(HashSet(this.xpGoalsMap.filterValues { it }.keys))
  if (diff.isNotEmpty()) {
    update { it.putAllXpGoals(goals) }
    toast("Gained xp for: " + diff.map { messages[it] }.joinToString())
  }
}

fun State.Builder.checkSecondCheckInGoal() {
  if (xpGoalsMap.notCompleted(SECOND_CHECK_IN)
    && !isTracking // we are now checking in
    && timeWorked.seconds > 0
  ) {
    putXpGoals(SECOND_CHECK_IN, true)
  }
}





