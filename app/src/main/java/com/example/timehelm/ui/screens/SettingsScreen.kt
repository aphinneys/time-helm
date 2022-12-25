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
        Setting(
          name = "Min",
          value = settings.dailyHoursMin
        ) { v ->
          updateSettings {
            it.setDailyHoursMin(v).setDailyHoursMax(max(it.dailyHoursMax, v))
          }
        }
        Setting(
          name = "Max",
          value = settings.dailyHoursMax
        ) { v -> updateSettings { it.setDailyHoursMax(v) } }
      }
    }
    BodySection {
      Setting(
        name = "Difficulty Level Avg",
        value = settings.difficultyAvg
      ) { v -> updateSettings { it.setDifficultyAvg(v) } }
      Setting(
        name = "Difficulty Variance",
        value = settings.difficultyVariance
      ) { v -> updateSettings { it.setDifficultyVariance(v) } }
    }
    BodySection {
      Setting(
        name = "Vacation (TODO)",
        value = settings.vacationDays
      ) { v -> updateSettings { it.setVacationDays(v) } }
    }
  }
}
