package com.example.agenda_smart.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Cloud // ¡Nuevo ícono para el clima!
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.example.agenda_smart.ui.screens.calendar.CalendarScreen
import com.example.agenda_smart.ui.screens.home.HomeScreen
import com.example.agenda_smart.ui.screens.weather.WeatherScreen

data class BottonNavItem(
    val label: String,
    val icon: ImageVector,
    val screen: Screen
)

@Composable
fun MainScreen(rootNavController: NavHostController) {
    val tabNavController = rememberNavController()

    // ¡Agregamos el Clima como el tercer botón de la barra!
    val items = listOf(
        BottonNavItem("Tareas", Icons.Filled.CheckBox, Screen.Task),
        BottonNavItem("Calendario", Icons.Filled.CalendarMonth, Screen.Calendar),
        BottonNavItem("Clima", Icons.Filled.Cloud, Screen.Weather)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any{
                            it.route == item.screen.route
                        } == true,
                        onClick = {
                            tabNavController.navigate(item.screen.route) {
                                popUpTo(tabNavController.graph.findStartDestination().id){
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = tabNavController,
            startDestination = Screen.Task.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Task.route) {
                HomeScreen(rootNavController)
            }
            composable(Screen.Calendar.route) {
                CalendarScreen()
            }
            composable(Screen.Weather.route) {
                WeatherScreen()
            }
        }
    }
}