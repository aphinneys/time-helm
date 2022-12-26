package com.example.timehelm.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun PokemonScreen() {
  Section(Modifier.padding(15.dp), 10.dp) {
    Text(text = "Pokemon", fontSize = 40.sp)
  }
}