package com.timehelm.timehelm.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.timehelm.timehelm.logic.onFirstOpen
import com.timehelm.timehelm.logic.useToast
import com.timehelm.timehelm.state.Settings
import com.timehelm.timehelm.state.SettingsUpdate
import com.timehelm.timehelm.state.useUpdateState
import java.lang.Integer.max

@Composable
fun SettingsScreen(settings: Settings, updateSettings: SettingsUpdate) {
  Section(Modifier.padding(15.dp), 10.dp) {
    T40("Settings")
    BodySection {
      T30("Goal daily hours range:")
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        NumberSetting(
          name = "Min",
          value = settings.dailyHoursMin
        ) { v ->
          updateSettings {
            it.setDailyHoursMin(v).setDailyHoursMax(max(it.dailyHoursMax, v))
          }
        }
        NumberSetting(
          name = "Max",
          value = settings.dailyHoursMax
        ) { v -> updateSettings { it.setDailyHoursMax(v) } }
      }
    }
    BodySection {
      ToggleSetting(name = "Shabbat", value = settings.shabbat) {
        updateSettings { s -> s.setShabbat(it) }
      }
    }
    BodySection {
      NumberSetting(name = "Work Starts", value = settings.workStart) {
        updateSettings { s -> s.setWorkStart(it) }
      }
      // throw an error if work start + min > 24 ??
    }
    DebugMenu(settings)
  }
}

@Composable
fun DebugMenu(settings: Settings) {
  var isOpen by remember { mutableStateOf(false) }
  Button({ isOpen = true }) {
    T20("Debug", fontStyle = FontStyle.Italic)
  }
  val updateState = useUpdateState()
  val toast = useToast(Toast.LENGTH_SHORT)
  if (isOpen) {
    AlertDialog(
      onDismissRequest = { isOpen = false },
      title = {
        T30("Debug")
      },
      text = {
        Column(
          Modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = 10.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button({ updateState { it.onFirstOpen(toast, settings) } }) {
            T20("Reset Day")
          }
        }
      },
      confirmButton = {
        Button({ isOpen = false }) {
          T20("Close")
        }
      }
    )
  }
}
