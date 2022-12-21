package com.example.timehelm

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun Settings() {
  Column(modifier = Modifier.padding(20.dp)) {
    Text(text = "Settings", fontSize = 40.sp)
    Text(text = "Goal daily hours range:", fontSize = 30.sp)
    Text(text = "Min: ___ Max: ___", fontSize = 30.sp)
    Text(text = "Difficulty level: ___", fontSize = 30.sp)
    Text(text = "Vacation days: ___", fontSize = 30.sp)
  }
}
