package com.example.timehelm

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Home() {
  val state by LocalContext.current.stateDataStore.data.collectAsState(
    initial = TimeState.getDefaultInstance()
  )
  val now = System.currentTimeMillis() / 1000L
  val elapsedSeconds = now - 1670990000
  val elapsedMinutes = ((elapsedSeconds / 60) % 60).toInt()
  val elapsedHours = (elapsedSeconds / (60 * 60)).toInt()
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight()
      .padding(20.dp)
  ) {
    Spacer(modifier = Modifier.padding(20.dp))
    StateIndicator(streak = state.streakDays, xp = state.xp)
    TimeClock(elapsedHours, elapsedMinutes)
    Message(elapsedHours, goal = 10)
    Spacer(modifier = Modifier.padding(20.dp))
    ManualModifyTime()
    TrackingButton()
  }
}


@Composable
fun StateIndicator(streak: Int, xp: Int) {
  Row(modifier = Modifier.padding(20.dp)) {
    Icon(
      Icons.Filled.Face,
      contentDescription = "streak",
      modifier = Modifier.size(40.dp),
      tint = Color.Red
    )
    Text(text = "Streak: $streak", fontSize = 30.sp, color = Color.Red)
    Spacer(modifier = Modifier.padding(20.dp))
    Icon(
      Icons.Filled.Star,
      contentDescription = "XP",
      modifier = Modifier.size(40.dp),
      tint = Color.Blue
    )
    Text(text = "XP: $xp", fontSize = 30.sp, color = Color.Blue)
  }
}

@Composable
fun TimeClock(elapsedHours: Int, elapsedMinutes: Int) {
  val mainColor = Color(0, 176, 80)
  val bgColor = Color(197, 223, 178)
  Box(modifier = Modifier.padding(vertical = 20.dp)) {
    Box(
      modifier = Modifier
        .border(width = 4.dp, color = mainColor)
        .background(color = bgColor)
    ) {
      Text(
        text = "%02d:%02d".format(elapsedHours, elapsedMinutes),
        fontSize = 40.sp,
        color = mainColor,
        modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp)

      )
    }
  }
}

@Composable
fun Message(elapsedHours: Int, goal: Int) {
  Text(
    text = stringResource(
      when (elapsedHours) {
        0 -> {
          R.string.starting_message
        }
        in 1 until goal -> {
          R.string.working_message
        }
        else -> {
          R.string.done_message
        }
      }
    ), fontStyle = FontStyle.Italic, fontSize = 30.sp, modifier = Modifier.padding(10.dp)
  )
}

@Composable
fun ManualModifyTime() {
  var textState by remember { mutableStateOf(TextFieldValue()) }
  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    TextField(
      value = textState,
      onValueChange = { textState = it },
      modifier = Modifier.width(100.dp).height(40.dp)
    )
    Button(onClick = { /*TODO*/ }) {
      Text(text = "Add")
    }
    Button(onClick = { /*TODO*/ }) {
      Text(text = "Remove")
    }
  }
}

@Composable
fun TrackingButton() {
  var tracking by remember { mutableStateOf(false) }
  Button(onClick = { tracking = !tracking }) {
    Text(
      text = stringResource(
        if (tracking) {
          R.string.tracking_message
        } else {
          R.string.not_tracking_message
        }
      )
    )
  }
}
