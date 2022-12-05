package com.example.timehelm

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timehelm.ui.theme.TimeHelmTheme
import com.google.protobuf.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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
                var selectedIndex by remember { mutableStateOf(0) }
                val state: TimeState by LocalContext.current.stateDataStore.data.collectAsState(
                    initial = TimeState.getDefaultInstance()
                ) // how to use state
                val context = LocalContext.current
                val scope = rememberCoroutineScope()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Time Helm")
                }
                //a BUTTON
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(20.dp)
                ) {
                    val now = now()
                    val firstOpenToday = state.lastDayStreak.seconds < now.seconds
                    if (firstOpenToday) {
                        if (state.fucksTally in 10.0..15.0){
                            updateState(scope, context) {
                                it.setStreakDays(it.streakDays + 1)
                            }}
                        } else {
                        updateState(scope, context) {
                            it.setStreakDays(0) // lost the streak!
                        }}
                        updateState(scope, context) {
                            it.setFucksTally(0F).setLastDayStreak(now)
                        }
                }
                Text("${state.streakDays} Day Sustainable Fuckery Streak!")
                //todo : include XP in proto save file thingy, and load it here...
                val XP =  (2..8).random()
                var tracking = state.hasTracking()
                // val thing = state.fucksTally
                // Text("we have a state!!! $thing")

                if (tracking) {
                    StopGivingAFuckButton(state = state) //stopTracking = { tracking = false })
                    CheckInButton(state = state)
                } else {
                    GiveAFuckButton(state = state, setidx = selectedIndex)
                    Text("select an activity lol")
                    DropdownDemo(selectedIndex, setIndex = { selectedIndex = it })

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

fun fucksToday(state: TimeState): Float {
    val elapsed = now().seconds - state.tracking.startTime.seconds
    return state.fucksTally + elapsed / 3600f * rates[Activities.values()[state.tracking.activityValue]]!!
}


@Composable
fun CheckInButton(state: TimeState) {
    var expanded by remember { mutableStateOf(false) }
    Button(onClick = { expanded = !expanded}, shape = RoundedCornerShape(50)) {
        Text("check progress")
    }
    val fucksToday = fucksToday(state)
    // this is the same function as the stop button, returns same info, just doesn't "stop" , maybe better to combine somehow?
    if (expanded) {
        CircularProgressIndicator(min(1f, fucksToday / 10f))
        if (fucksToday > 15) {
            Text("Please stop working, it's been a long day.")
        }
    }
}

//https://www.baeldung.com/kotlin/builder-pattern#implementation

@Composable
fun GiveAFuckButton(state: TimeState, setidx: Int) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Button(onClick = {
        val ts = com.google.protobuf.Timestamp.newBuilder().build()
        updateState(scope, context) {
            it.setTracking(TimeState.Tracking.newBuilder().setStartTime(ts).setActivityValue(setidx).build())
                //add a set XP: decrement by 1 IF current time is before 10am
            // decrement XP if this is the second time today...
        }}
        , shape = RoundedCornerShape(50)) {
        Text("give a fuck :)")
    }
}

@Composable
fun StopGivingAFuckButton(state: TimeState) { //stopTracking: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Button(
        onClick = {
            val ts = com.google.protobuf.Timestamp.newBuilder().build()
            val elapsed = ts.seconds - state.tracking.startTime.seconds
            val fuckstoday = state.fucksTally + elapsed * state.tracking.activityValue
            updateState(scope, context) { it.clearTracking().setFucksTally(fuckstoday) }
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
    val fucksToday = fucksToday(state)
    CircularProgressIndicator(progress = min(1f, fucksToday / 10f))
    if (fucksToday > 15) {
        Text("Please stop working, it's been a long day.")
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



@Composable
fun Greeting(name: String) {
    Text(text = "Welcome to $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TimeHelmTheme {
        Greeting("Time Helm")
    }
}


//
//@Composable
//fun MidnightTally(){
//    if (fucks_tally in 10..16) {
//        streak += 1
//    } else{
//        streak = 0
//    }
//    Text("$streak Day Sustainable Fuckery Streak!")
//}

