package com.example.timehelm

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.protobuf.Timestamp
import java.lang.Integer.max
import java.lang.Long.min

typealias Toaster = (String) -> Unit

fun useToast(context: Context): Toaster {
  return { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
}

fun fmtTime(hours: Int, minutes: Int): String {
  return "%02d:%02d".format(hours, minutes)
}

fun Int.value() = max(this, 0)

fun now(): Timestamp {
  return Timestamp.getDefaultInstance().toBuilder()
    .setSeconds(System.currentTimeMillis() / 1000).build()
}

fun Timestamp.add(other: Timestamp): Timestamp {
  return this.toBuilder()
    .setSeconds(this.seconds + other.seconds)
    .setNanos(this.nanos + other.nanos).build()
}

fun Timestamp.diff(other: Timestamp): Timestamp {
  return this.toBuilder()
    .setSeconds(this.seconds - other.seconds)
    .setNanos(this.nanos - other.nanos).build()
}

fun State.Builder.elapsedTime(now: Timestamp): Timestamp {
  return this.build().elapsedTime(now)
}

fun State.elapsedTime(now: Timestamp): Timestamp {
  if (this.isTracking) {
    return this.timeWorked.add(now.diff(this.startTime))
  }
  return this.timeWorked
}

// Don't show more than 100 hours
fun State.elapsedHours(now: Timestamp): Int {
  return min(100, this.elapsedTime(now).seconds / 3600).toInt()
}

fun State.elapsedMins(now: Timestamp): Int {
  return ((this.elapsedTime(now).seconds / 60) % 60).toInt()
}

@Composable
fun BodySection(content: @Composable () -> Unit) {
  Section(
    Modifier
      .padding(top = 30.dp)
      .border(1.dp, MaterialTheme.colors.primary, MaterialTheme.shapes.large), 5.dp, content
  )
}

@Composable
fun Section(modifier: Modifier, spacing: Dp, content: @Composable () -> Unit) {
  Box(modifier) {
    Column(
      modifier = Modifier.padding(spacing),
      verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
      content()
    }
  }
}

@Composable
fun Setting(name: String, value: Int, setValue: (Int) -> Unit) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    if (name.isNotEmpty()) {
      Text(text = "$name: ", fontSize = 30.sp)
    }
    OutlinedTextField(
      value = if (value >= 0) value.toString() else "",
      onValueChange = {
        if (it == "") {
          setValue(-1)
        } else {
          try {
            setValue(Integer.parseInt(it))
          } catch (_: NumberFormatException) {
          }
        }
      },
      keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
      textStyle = TextStyle(
        fontSize = 30.sp,
      ),
      modifier = Modifier.width(90.dp),
      singleLine = true,
    )
  }

}
