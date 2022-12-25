package com.example.timehelm.ui.screens

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
