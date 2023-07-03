package com.example.timehelm.logic

import com.example.timehelm.state.Pokemon
import com.example.timehelm.state.PokemonState
import com.example.timehelm.state.PokemonUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Float.min
import java.net.URL


data class PokemonData(
  val id: Int,
  val name: String,
  val picture_url: String?,
  val types: List<String>,
  val catchRate: Int,
) {
  fun toStoredPokemon(): Pokemon {
    return Pokemon.newBuilder()
      .setId(id)
      .setName(name)
      .setCount(1)
      .setPictureUrl(picture_url)
      .addAllTypes(types)
      .build()
  }
}


val picturePriorities = listOf(
  "front_shiny_female",
  "front_shiny",
  "front_female",
  "front_default",
)

fun JSONObject.choosePokemonPicture(): String? {
  val k = keys().asSequence().toList()
  for (pic in picturePriorities) {
    if (k.contains(pic) && !isNull(pic)) {
      return getString(pic)
    }
  }
  return if (k.isNotEmpty()) {
    getString(k[0])
  } else {
    null
  }
}

fun <T> JSONArray.toList(transform: (JSONObject) -> T): List<T> {
  val list = mutableListOf<T>()
  for (i in 0 until length()) {
    val value = this[i]
    if (value !is JSONObject)
      throw Exception("error parsing value")
    list.add(transform(value))
  }
  return list
}


const val POKEMON_API = "https://pokeapi.co/api/v2/pokemon/"
const val SPECIES_API = "https://pokeapi.co/api/v2/pokemon-species/"
val VALID_IDS = 1..905

fun pickId(): Int {
  return VALID_IDS.random()
}

suspend fun getPokemon(id: Int): PokemonData? {
  if (!VALID_IDS.contains(id)) {
    return null
  }
  return try {
    withContext(Dispatchers.IO) {
      val pokeJson = JSONObject(
        URL(POKEMON_API + id.toString()).openStream().bufferedReader().use { it.readText() })
      val speciesJson = JSONObject(
        URL(SPECIES_API + id.toString()).openStream().bufferedReader().use { it.readText() })
      PokemonData(
        id,
        pokeJson.getString("name"),
        pokeJson.getJSONObject("sprites").choosePokemonPicture(),
        pokeJson.getJSONArray("types").toList {
          it.getJSONObject("type").getString("name")
        },
        speciesJson.getInt("capture_rate"),
      )
    }
  } catch (ex: Exception) {
    null
  }
}

fun PokemonState.indexOf(id: Int): Int {
  pokemonList.forEachIndexed { index, pokemon ->
    if (pokemon.id == id) {
      return index
    }
  }
  return -1
}

fun Pokemon.increment(): Pokemon.Builder {
  return toBuilder().setCount(count + 1)
}

fun PokemonState.addPokemon(update: PokemonUpdate, pokemon: PokemonData?) {
  if (pokemon == null) {
    return
  }
  val idx = indexOf(pokemon.id)
  if (idx >= 0) { // we already have it
    update { it.setPokemon(idx, it.getPokemon(idx).increment()) }
  } else { // we don't have it yet
    update { it.addPokemon(pokemon.toStoredPokemon()) }
  }
}

const val MAGIC_NUMBER = 1250f

fun Float.didCatch(): Boolean {
  return Math.random() < this
}

fun PokemonData.catchProbability(xp: Int): Float {
  return min(1f, (catchRate * xp) / MAGIC_NUMBER)
}

