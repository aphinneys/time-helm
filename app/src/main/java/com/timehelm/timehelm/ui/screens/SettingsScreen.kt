package com.timehelm.timehelm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.timehelm.timehelm.state.Settings
import com.timehelm.timehelm.state.SettingsUpdate
import java.lang.Integer.max

@Composable
fun SettingsScreen(settings: Settings, updateSettings: SettingsUpdate) {
  Section(Modifier.padding(15.dp), 10.dp) {
    T40("Settings")
    BodySection {
      T30("Goal daily hours range:")
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
        name = "Vacation (TODO)",
        value = settings.vacationDays
      ) { v -> updateSettings { it.setVacationDays(v) } }
    }
    BodySection {
      Setting(
        name = "Start of Day",
        value = settings.startOfDay
      ) { v -> updateSettings { it.setStartOfDay(v) } }
    }
  }
}
