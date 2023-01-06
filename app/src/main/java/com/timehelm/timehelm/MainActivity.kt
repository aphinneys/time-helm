package com.timehelm.timehelm

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.timehelm.timehelm.logic.checkGoals
import com.timehelm.timehelm.logic.firstOpen
import com.timehelm.timehelm.logic.onFirstOpen
import com.timehelm.timehelm.logic.useToast
import com.timehelm.timehelm.state.*
import com.timehelm.timehelm.ui.screens.HomeScreen
import com.timehelm.timehelm.ui.screens.PokemonScreen
import com.timehelm.timehelm.ui.screens.SettingsScreen
import com.timehelm.timehelm.ui.theme.TimeHelmTheme
import com.timehelm.timehelm.ui.theme.getColorForTrackingState
import kotlinx.coroutines.delay

sealed class Screen(val route: String, @StringRes val resourceId: Int) {
  object Home : Screen("home", R.string.home_route)
  object Settings : Screen("settings", R.string.settings_route)
  object Pokemon : Screen("pokemon", R.string.pokemon_route)
}

val icons = hashMapOf(
  R.string.home_route to Icons.Filled.Home,
  R.string.settings_route to Icons.Filled.Settings,
  R.string.pokemon_route to Icons.Filled.Star,
)

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      TimeHelmTheme {
        val navController = rememberNavController()
        val items = listOf(Screen.Settings, Screen.Home, Screen.Pokemon)

        // Declare all necessary data/helpers
        val state by LocalContext.current.stateDataStore.data.collectAsState(State.getDefaultInstance())
        val settings by LocalContext.current.settingsDataStore.data.collectAsState(Settings.getDefaultInstance())
        val updateState = useUpdateState(rememberCoroutineScope(), LocalContext.current)
        val updateSettings = useUpdateSettings(rememberCoroutineScope(), LocalContext.current)
        val toast = useToast(LocalContext.current, Toast.LENGTH_SHORT)
        val toastLong = useToast(LocalContext.current, Toast.LENGTH_LONG)

        // first time loading the app for the day
        if (state.firstOpen(settings.startOfDay)) {
          updateState { it.onFirstOpen(toastLong, settings) }
        }

        // check the goals
        LaunchedEffect(Unit) {
          while (true) {
            state.checkGoals(updateState, settings, toastLong)
            delay(10_000)
          }
        }

        Scaffold(
          bottomBar = {
            BottomNavigation {
              val navBackStackEntry by navController.currentBackStackEntryAsState()
              val currentDestination = navBackStackEntry?.destination
              items.forEach { screen ->
                BottomNavigationItem(
                  icon = {
                    Icon(
                      icons[screen.resourceId]!!,
                      contentDescription = stringResource(id = screen.resourceId)
                    )
                  },
                  label = { Text(stringResource(screen.resourceId)) },
                  selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                  onClick = {
                    navController.navigate(screen.route) {
                      // Pop up to the start destination of the graph to
                      // avoid building up a large stack of destinations
                      // on the back stack as users select items
                      popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                      }
                      // Avoid multiple copies of the same destination when
                      // reselecting the same item
                      launchSingleTop = true
                      // Restore state when reselecting a previously selected item
                      restoreState = true
                    }
                  }
                )
              }
            }
          },
          backgroundColor = getColorForTrackingState(state.isTracking)
        ) { innerPadding ->
          NavHost(
            navController,
            startDestination = Screen.Home.route,
            Modifier.padding(innerPadding)
          ) {
            composable(Screen.Settings.route) { SettingsScreen(settings, updateSettings) }
            composable(Screen.Home.route) { HomeScreen(state, settings, updateState, toast) }
            composable(Screen.Pokemon.route) { PokemonScreen() }
          }
        }
      }
    }
  }
}
