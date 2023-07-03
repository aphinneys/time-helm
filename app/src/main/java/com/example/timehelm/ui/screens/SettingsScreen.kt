package com.example.timehelm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timehelm.state.Settings
import com.example.timehelm.state.SettingsUpdate
import java.lang.Integer.max

@Composable
fun SettingsScreen(settings: Settings, updateSettings: SettingsUpdate) {
  Section(Modifier.padding(15.dp), 10.dp) {
    Text(text = "Settings", fontSize = 40.sp)
    BodySection {
      Text(text = "Goal daily hours range:", fontSize = 30.sp)
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        NumberSetting(
          name = "Min",
          value = settings.dailyHoursMin
        ) {
          updateSettings { s ->
            s.setDailyHoursMin(it).setDailyHoursMax(max(s.dailyHoursMax, it))
            }
        }
        NumberSetting(
          name = "Max",
          value = settings.dailyHoursMax
          ) { updateSettings { s -> s.setDailyHoursMax(it) } }
      }
    }
  }
  BodySection {
      ToggleSetting(name = "Shabbat", value = settings.shabbat) {
        updateSettings { s -> s.setShabbat(it) }
      }
    }
}
