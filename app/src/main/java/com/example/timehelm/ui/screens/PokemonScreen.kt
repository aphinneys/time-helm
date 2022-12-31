package com.example.timehelm.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.timehelm.R
import com.example.timehelm.logic.PokemonData
import com.example.timehelm.logic.addPokemon
import com.example.timehelm.logic.getPokemon
import com.example.timehelm.logic.pickId
import com.example.timehelm.state.PokemonState
import com.example.timehelm.state.pokemonDataStore
import com.example.timehelm.state.useUpdatePokemon
import com.example.timehelm.ui.theme.Shapes
import kotlinx.coroutines.delay


fun hour(): Long {
  return System.currentTimeMillis() / (1000 * 3600)
}


@Composable
fun PokemonScreen() {
  // keep track of which hour it is, and update every ten seconds
  var hours by remember { mutableStateOf(hour()) }
  LaunchedEffect(Unit) {
    while (true) {
      hours = hour()
      delay(10_000)
    }
  }

  // pokemon
  var pokemonData: PokemonData? by remember { mutableStateOf(null) }
  LaunchedEffect(hours) { pokemonData = getPokemon(pickId()) }

  val storedPokemon by LocalContext.current.pokemonDataStore.data.collectAsState(PokemonState.getDefaultInstance())
  val updatePokemon = useUpdatePokemon(rememberCoroutineScope(), LocalContext.current)
  val sortedPokemon = remember(storedPokemon.pokemonList) {
    storedPokemon.pokemonList.sortedWith { p1, p2 -> p1.id.compareTo(p2.id) }
  }

  Section(Modifier.padding(15.dp), 10.dp) {
    Text(text = "Pokemon", fontSize = 40.sp)
    pokemonData?.let {
      Text(text = "You found a ${pokemonData!!.name}!", fontSize = 30.sp)
    }
    Row {
      Button({ hours = pickId().toLong() }) {
        Text("new")
      }
      Button({ storedPokemon.addPokemon(updatePokemon, pokemonData) }) {
        Text("Add")
      }
      Button({ updatePokemon { it.clearPokemon() } }) {
        Text("Clear")
      }
    }
    LazyColumn {
      items(items = sortedPokemon, key = { it.id }) {
        Card(
          elevation = 10.dp,
          shape = Shapes.medium,
          modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
            .padding(vertical = 5.dp)
        ) {
          Column(
            modifier = Modifier
              .padding(5.dp)
              .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Row(
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
            ) {
              Text(it.name, fontSize = 30.sp)
              if (it.count > 1) {
                Text("x${it.count}", fontSize = 30.sp)
              }
            }
            SubcomposeAsyncImage(
              model = ImageRequest.Builder(LocalContext.current)
                .data(it.pictureUrl)
                .crossfade(true)
                .size(5000, 5000)
                .build(),
              contentDescription = it.name,
              loading = { CircularProgressIndicator() },
              error = {
                Icon(
                  Icons.Filled.Close,
                  stringResource(R.string.image_load_failed),
                  Modifier.fillMaxWidth(.5f),
                  Color.Red
                )
                Text(
                  stringResource(R.string.image_load_failed),
                  fontSize = 30.sp,
                  color = Color.Red
                )
              },
              contentScale = ContentScale.FillWidth,
              modifier = Modifier.fillMaxWidth(),
            )
          }
        }
      }
    }
  }
}