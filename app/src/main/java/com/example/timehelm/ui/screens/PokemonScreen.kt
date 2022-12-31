package com.example.timehelm.ui.screens

import androidx.annotation.StringRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.timehelm.R
import com.example.timehelm.logic.*
import com.example.timehelm.state.*
import com.example.timehelm.state.State
import com.example.timehelm.ui.theme.Shapes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun hour(): Long {
  return System.currentTimeMillis() / (1000 * 3600)
}

@Composable
fun PokemonSprite(url: String, name: String, modifier: Modifier = Modifier) {
  SubcomposeAsyncImage(
    model = ImageRequest.Builder(LocalContext.current)
      .data(url)
      .crossfade(true)
      .build(),
    contentDescription = name,
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
    modifier = modifier,
  )

}

@Composable
fun popup(message: String, confirmText: String, imageURL: String?, close: () -> Unit) {
  AlertDialog(onDismissRequest = close,
    title = {
      Text(message, fontSize = 30.sp)
    },
    text = {
      imageURL?.let {
        PokemonSprite(
          it, "Caught Pokemon",
          Modifier
            .fillMaxWidth()
            .height(300.dp)
        )
      }
    },
    confirmButton = {
      Button(onClick = close) {
        Text(confirmText, fontSize = 20.sp)
      }
    })
}


@Composable
fun CatchPokemon(
  xp: Int,
  data: PokemonData,
  state: PokemonState,
  updatePokemon: PokemonUpdate,
  updateState: StateUpdate
) {
  val rotation = remember { Animatable(0f) }
  val scope = rememberCoroutineScope()

  @StringRes
  var caughtState: Int? by remember { mutableStateOf(null) }
  val catchProbability = remember(data) { data.catchProbability(xp) }
  val catch: () -> Unit = {
    if (!state.attempted) {
      scope.launch {
        rotation.animateTo(
          targetValue = 1080f,
          animationSpec = tween(1000, easing = LinearEasing)
        )
        rotation.snapTo(0f)
        if (catchProbability.didCatch()) {
          caughtState = R.string.caught_text
          state.addPokemon(updatePokemon, data)
          updateState { it.setPrevXp(it.prevXp - 1) } // todo change subtracted val
        } else {
          caughtState = R.string.escaped_text
        }
        updatePokemon { it.setAttempted(true) }
      }
    }
  }
  if (!state.attempted) {
    Column(
      verticalArrangement = Arrangement.spacedBy(25.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text("A wild ${data.name} appeared!", fontSize = 25.sp, textAlign = TextAlign.Center)
      Image(
        painterResource(R.drawable.pokeball), contentDescription = "Pokeball",
        Modifier
          .fillMaxWidth(.25f)
          .clickable(
            enabled = true, onClickLabel = "Clickable Pokeball", onClick = catch
          )
          .graphicsLayer {
            rotationZ = rotation.value
          },
        contentScale = ContentScale.Inside,
      )
      Text(
        "There is a ${(catchProbability * 100).toInt()}% chance to catch it (${data.catchRate})",
        fontSize = 20.sp
      )
    }
  }
  caughtState?.let {
    popup(
      stringResource(it),
      stringResource(
        if (it == R.string.caught_text)
          R.string.confirm_caught
        else R.string.confirm_escaped
      ),
      if (it == R.string.caught_text) data.picture_url else null
    ) {
      caughtState = null
    }
  }
}

@Composable
fun PokemonList(pokemon: List<Pokemon>) {
  LazyColumn {
    items(items = pokemon, key = { it.id }) {
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
          Text(it.typesList.joinToString())
          PokemonSprite(it.pictureUrl, it.name, Modifier.fillMaxWidth())
        }
      }
    }
  }

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

  val timeState by LocalContext.current.stateDataStore.data.collectAsState(State.getDefaultInstance())
  val updateTimeState = useUpdateState(rememberCoroutineScope(), LocalContext.current)
  val storedPokemon by LocalContext.current.pokemonDataStore.data.collectAsState(PokemonState.getDefaultInstance())
  val updatePokemon = useUpdatePokemon(rememberCoroutineScope(), LocalContext.current)
  val sortedPokemon = remember(storedPokemon.pokemonList) {
    storedPokemon.pokemonList.sortedWith { p1, p2 -> p1.id.compareTo(p2.id) }
  }
  // pokemon
  var pokemonData: PokemonData? by remember { mutableStateOf(null) }
  LaunchedEffect(hours) {
    pokemonData = getPokemon(pickId())
    updatePokemon { it.setAttempted(false) }
  }

  Section(Modifier.padding(15.dp), 10.dp) {
    Text(text = "Pokemon", fontSize = 40.sp)
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
      Button({ updatePokemon { it.setAttempted(false) } }) {
        Text("attempt")
      }
    }
    pokemonData?.let {
      CatchPokemon(
        timeState.xp,
        it,
        storedPokemon,
        updatePokemon,
        updateTimeState,
      )
    }
    PokemonList(sortedPokemon)
  }
}