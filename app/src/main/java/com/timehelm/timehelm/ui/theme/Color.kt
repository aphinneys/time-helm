package com.timehelm.timehelm.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Purple800 = Color(0xFF26007C)
val Purple900 = Color(0xFF1F0447)
val Teal200 = Color(0xFF03DAC5)

@Composable
fun getColorForTrackingState(tracking: Boolean, darkTheme: Boolean = isSystemInDarkTheme()): Color {
  return if (tracking) {
    if (darkTheme) {
      Purple800
    } else {
      Purple200
    }
  } else {
    MaterialTheme.colors.background
  }
}
