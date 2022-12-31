package com.example.timehelm.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timehelm.R
import com.example.timehelm.logic.*
import com.example.timehelm.state.Settings
import com.example.timehelm.state.State
import com.example.timehelm.state.StateUpdate
import com.example.timehelm.state.isFullyInitialized
import com.google.protobuf.Timestamp
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(state: State, settings: Settings, updateState: StateUpdate, toast: Toaster) {
  // check that settings are initialized
  if (!settings.isFullyInitialized()) {
    toast("Settings are not fully initialized!")
  }

  // use the now variable to determine the current time
  var now by remember { mutableStateOf(now()) }
  LaunchedEffect(Unit) { // refresh every five seconds
    while(true) {
      now = now()
      delay(5000)
    }
  }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight()
      .padding(20.dp)
  ) {
    Spacer(modifier = Modifier.padding(20.dp))
    StateIndicator(streak = state.streakDays, xp = state.xp)
    TimeClock(state, now)
    Message(state, settings, now)
    TrackingButton(state.isTracking, updateState)
    Spacer(modifier = Modifier.padding(20.dp))
    ManualModifyTime(updateState, toast)
    Text("${state.timeWorked.seconds}")
    Column(Modifier.verticalScroll(rememberScrollState())) {
      Row {
        Button({ updateState { it.onFirstOpen(toast, settings) } }) { Text("open") }
        Button({ updateState { it.clearPrevXp().clearXpGoals() } }) { Text("0XP") }
        Button({ updateState { it.setPrevXp(it.prevXp + 1) } }) { Text("+XP") }
        Button({ updateState { it.clearStreakDays() } }) { Text("0S") }
        Button({ updateState { it.setStreakDays(it.streakDays + 1) } }) { Text("+S") }
      }
      Row {
        Button({ updateState { it.setLastDayStreak(Timestamp.getDefaultInstance().toBuilder().setSeconds(10)) } }) { Text("log") }
      }
    }
  }
}


@Composable
fun StateIndicator(streak: Int, xp: Int) {
  Row(modifier = Modifier.padding(20.dp)) {
    Text(text = "\uD83D\uDD25 $streak", fontSize = 40.sp, color = Color(235, 129, 16))
    Spacer(modifier = Modifier.padding(20.dp))
    Text(text = "\uD83D\uDCA0 $xp", fontSize = 40.sp, color = Color(16, 107, 235))
  }
}

@Composable
fun TimeClock(state: State, now: Timestamp) {
  val mainColor = Color(0, 176, 80)
  val bgColor = Color(197, 223, 178)
  Box(modifier = Modifier.padding(vertical = 20.dp)) {
    Box(
      modifier = Modifier
        .border(width = 4.dp, color = mainColor)
        .background(color = bgColor)
    ) {
      Text(
        text = fmtTime(state.elapsedHours(now), state.elapsedMins(now)),
        fontSize = 80.sp,
        color = mainColor,
        modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp)

      )
    }
  }
}

@Composable
fun Message(state: State, settings: Settings, now: Timestamp) {
  Text(
    text = stringResource(
      when (state.elapsedHours(now)) {
        0 -> {
          R.string.starting_message
        }
        in 1 until settings.dailyHoursMin -> {
          R.string.working_message
        }
        in settings.dailyHoursMin until settings.dailyHoursMax -> {
          R.string.in_goal_message
        }
        else -> {
          R.string.done_message
        }
      }
    ), fontStyle = FontStyle.Italic, fontSize = 40.sp, modifier = Modifier.padding(10.dp)
  )
}

fun modifyTime(
  hr: Int,
  min: Int,
  updateState: StateUpdate,
  toast: Toaster,
  actionName: String,
  modifier: (Long, Long) -> Long
): () -> Unit {
  return {
    val hrs = hr.value()
    val mins = min.value()
    updateState {
      it.setTimeWorked(
        it.timeWorked.toBuilder()
          .setSeconds(modifier(it.timeWorked.seconds, hrs.toLong() * 3600 + mins * 60))
      )
    }
    toast(actionName + " " + fmtTime(hrs, mins))
  }
}

@Composable
fun ManualModifyTime(updateState: StateUpdate, toast: Toaster) {
  var hr by remember { mutableStateOf(0) }
  var min by remember { mutableStateOf(0) }
  BodySection {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
      Setting(name = "Hr", value = hr, setValue = { hr = it })
      Setting(name = "Min", value = min, setValue = { min = it })
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
      Button(onClick = modifyTime(hr, min, updateState, toast, "Added") { a, b -> a + b }) {
        Text(text = "Add", fontSize = 20.sp)
      }
      Button(onClick = modifyTime(hr, min, updateState, toast, "Removed") { a, b -> a - b }) {
        Text(text = "Remove", fontSize = 20.sp)
      }
    }
  }
}

@Composable
fun TrackingButton(isTracking: Boolean, updateState: StateUpdate) {
  Button(onClick = {
    updateState {
      it.checkSecondCheckInGoal()
      if (it.isTracking) { // finished tracking
        it.timeWorked = it.elapsedTime(now())
        it.clearStartTime()
      } else { // starting tracking
        it.startTime = now()
      }
      it.setIsTracking(!it.isTracking)
    }
  }) {
    Text(
      text = stringResource(
        if (isTracking) {
          R.string.tracking_message
        } else {
          R.string.not_tracking_message
        }
      ),
      fontSize = 30.sp,
    )
  }
}
