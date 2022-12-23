package com.example.timehelm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.lang.Integer.max

@Composable
fun Settings() {
  val settings by LocalContext.current.settingsDataStore.data.collectAsState(
    initial = Settings.getDefaultInstance()
  ) // how to use state

  val updateSettings = useUpdateSettings(rememberCoroutineScope(), LocalContext.current)
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
