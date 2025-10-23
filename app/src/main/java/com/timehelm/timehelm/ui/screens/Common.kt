package com.timehelm.timehelm.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
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
fun Header(
  fontSize: TextUnit,
  message: String,
  modifier: Modifier,
  fontStyle: FontStyle?,
  color: Color?
) {
  Text(
    text = message,
    fontSize = fontSize,
    fontStyle = fontStyle,
    style = TextStyle(lineHeight = 1.em),
    modifier = modifier,
    color = color ?: MaterialTheme.colors.onBackground
  )
}

@Composable
fun T40(
  text: String,
  modifier: Modifier = Modifier,
  fontStyle: FontStyle? = null,
  color: Color? = null
) {
  Header(40.sp, text, modifier, fontStyle, color)
}

@Composable
fun T30(
  text: String,
  modifier: Modifier = Modifier,
  fontStyle: FontStyle? = null,
  color: Color? = null
) {
  Header(30.sp, text, modifier, fontStyle, color)
}

@Composable
fun T25(
  text: String,
  modifier: Modifier = Modifier,
  fontStyle: FontStyle? = null,
  color: Color? = null
) {
  Header(25.sp, text, modifier, fontStyle, color)
}

@Composable
fun T20(
  text: String,
  modifier: Modifier = Modifier,
  fontStyle: FontStyle? = null,
  color: Color? = null
) {
  Header(20.sp, text, modifier, fontStyle, color)
}

@Composable
fun T10(
  text: String,
  modifier: Modifier = Modifier,
  fontStyle: FontStyle? = null,
  color: Color? = null
) {
  Header(10.sp, text, modifier, fontStyle, color)
}
@Composable
fun Setting(name: String, inner: @Composable () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
    if (name.isNotEmpty()) {
      T30("$name: ", Modifier.padding(horizontal = 10.dp))
    }
    inner()
  }
}

@Composable
fun NumberSetting(name: String, value: Int, setValue: (Int) -> Unit) {
  Setting(name) {
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
        color = MaterialTheme.colors.onBackground,
      ),
      modifier = Modifier.width(90.dp),
      singleLine = true,
    )
  }
}

@Composable
fun ToggleSetting(name: String, value: Boolean, setValue: (Boolean) -> Unit) {
  Setting(name) {
    Switch(checked = value, onCheckedChange = setValue)
  }
}