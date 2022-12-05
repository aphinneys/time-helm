package com.example.timehelm

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timehelm.TimeState.Tracking
import com.example.timehelm.ui.theme.TimeHelmTheme
import com.google.protobuf.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.min


enum class Activities {
    Creative,
    Learning,
    CampusMeetings,
    Iterating,
    Handling,
}

val rewards = listOf("I AM AWESOME I AM AWESOME")
val rates = hashMapOf(
    Activities.Creative to 3f,
    Activities.Learning to 2.5f,
    Activities.CampusMeetings to 2.0f,
    Activities.Iterating to 1.7f,
    Activities.Handling to 1.0f
)

val dailygoal = 10
val dailymax = 15

fun now(): Timestamp {
    return Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000L).build()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimeHelmTheme {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(20.dp)
                ) {
                    Text(text = "Welcome to Time Helm!", fontSize = 30.sp, modifier = Modifier.padding(10.dp))
                    var selectedIndex by remember { mutableStateOf(0) }
                    val state: TimeState by LocalContext.current.stateDataStore.data.collectAsState(
                        initial = TimeState.getDefaultInstance()
                    ) // how to use state
                    val context = LocalContext.current
                    val scope = rememberCoroutineScope()
                    val now = now()
                    val startOfDay = LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().epochSecond
                    val firstOpenToday = state.lastDayStreak.seconds < startOfDay
                    if (firstOpenToday) {
                        updateState(scope, context) {
                            it.setStreakDays(if (state.fucksTally in 10.0..15.0) it.streakDays + 1 else 0)
                                .setFucksTally(0F).setLastDayStreak(now)
                        }
                    }
                    Text("${state.streakDays} Day Sustainable Fuckery Streak!")
                    //todo : include XP in proto save file thingy, and load it here...
                    val XP =  (2..8).random()
                    val tracking = state.hasTracking()
                    var time by remember { mutableStateOf(now()) }
                    ProgressIndicator(state = state, time = time)
                    if (tracking) {
                        StopGivingAFuckButton() //stopTracking = { tracking = false })
                        CheckInButton(update = { time = now() })
                    } else {
                        GiveAFuckButton(setIdx = selectedIndex)
                        Text("select an activity lol")
                        DropdownDemo(selectedIndex, setIndex = { selectedIndex = it })
                    }
                }
            }
        }
    }
}
fun updateState(scope: CoroutineScope, context: Context, update: (TimeState.Builder) -> TimeState.Builder) {
    scope.launch {
        context.stateDataStore.updateData { currentState ->
            currentState.toBuilder().also { update(it) }.build()
        }
    }
}

fun fucksToday(tracking: Tracking, fucksTally: Float, time: Timestamp? = null): Float {
    val since = time ?: now()
    val elapsed = since.seconds - tracking.startTime.seconds
    return fucksTally + elapsed / 3600f * rates[Activities.values()[tracking.activityValue]]!!
}


@Composable
fun CheckInButton(update: () -> Unit) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    Button(onClick = update, shape = RoundedCornerShape(50)) {
        Text("check progress")
    }
}

@Composable
fun ProgressIndicator(state: TimeState, time: Timestamp) {
    val fucksToday = fucksToday(state.tracking, state.fucksTally, time = time)
    // this is the same function as the stop button, returns same info, just doesn't "stop" , maybe better to combine somehow?

    CircularProgressIndicator(min(1f, fucksToday * 5),
        modifier = Modifier.border(
            width = 1.dp,
            color = Color.Black,
            shape = CircleShape)
            .size(120.dp),
        strokeWidth = 20.dp)
    if (fucksToday > 15) {
        Text("Please stop working, it's been a long day.")
    }
}

//https://www.baeldung.com/kotlin/builder-pattern#implementation

@Composable
fun GiveAFuckButton(setIdx: Int) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Button(onClick = {
        updateState(scope, context) {
            it.setTracking(TimeState.Tracking.newBuilder().setStartTime(now())
                .setActivityValue(setIdx).build())
                //add a set XP: decrement by 1 IF current time is before 10am
            // decrement XP if this is the second time today...
        }},
        shape = RoundedCornerShape(50)) {
        Text("give a fuck :)")
    }
}

@Composable
fun StopGivingAFuckButton() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Button(
        onClick = {
            updateState(scope, context) {
                it.setFucksTally(fucksToday(it.tracking, it.fucksTally))
                .clearTracking()
            }
        },
        //add a set XP: decrement by 1 IF fuckstoday > 3 and time is before noon
        // if 5 fucks before 5pm
        // if 8 fucks before 8pm
        //if fucks elapsed in current state > 3
        //if fucks elapsed in current state > 5
        //if XP <= 0: perform Reward, and reset XP (maybe make that a function?)
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Red,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(50),
    ) {
        Text("Ok we done giving a fuck")
    }
}

@Composable
fun DropdownDemo(selectedIndex: Int, setIndex: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.TopStart)) {
        Text(
            Activities.values()[selectedIndex].name, modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { expanded = true })
                .background(
                    Color.LightGray
                )
                .clip(RoundedCornerShape(50)) //why this no work
                .padding(10.dp))
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color.Gray
                )
        ) {
            Activities.values().forEachIndexed { index, s ->
                DropdownMenuItem(onClick = {
                    setIndex(index)
                    expanded = false
                }) {
                    Text(text = s.name)
                }
            }
        }
    }
}