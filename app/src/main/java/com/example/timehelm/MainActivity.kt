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
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


val items = listOf("Creative", "Learning" , "Campus + Meetings", "Iterating", "Handling")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimeHelmTheme {
                var selectedIndex by remember { mutableStateOf(0) }
                val state: TimeState by LocalContext.current.stateDataStore.data.collectAsState(
                    initial = TimeState.getDefaultInstance()
                ) // how to use state
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
                    var tracking by remember { mutableStateOf(false) }

                    val thing = state.fucksTally
                    Text("we have a state!!! $thing")

                    if (tracking) {
                        StopGivingAFuckButton(stopTracking = { tracking = false })
                        CheckInButton(state = state)
                    } else {
                        GiveAFuckButton(startTracking = { tracking = true })
                    }
                    if (!tracking) {
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


@Composable
fun CheckInButton(state: TimeState) {
    // TODO your code here :)
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

@Composable
fun GiveAFuckButton(startTracking: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Button(onClick = {
        startTracking()
        updateState(scope, context) {
            it.setFucksTally(it.fucksTally + 1)
        }
    }, shape = RoundedCornerShape(50)) {
        Text("give a fuck :)")
    }
}

@Composable
fun StopGivingAFuckButton(stopTracking: () -> Unit) {
    Button(onClick = stopTracking, colors = ButtonDefaults.buttonColors(
        backgroundColor = Color.Red,
        contentColor = Color.White),
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
        Text(items[selectedIndex], modifier = Modifier
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
            items.forEachIndexed { index, s ->
                DropdownMenuItem(onClick = {
                    setIndex(index)
                    expanded = false
                }) {
                    Text(text = s)
                }
            }
        }
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

