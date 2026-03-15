package com.example.agenda_smart.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType

@Composable
fun NavGraph() {
    val rootNavController = rememberNavController()

    NavHost(
        navController = rootNavController,
        startDestination = Screen.Task.route
    ){
       composable(Screen.Task.route) {
           MainScreen(rootNavController)
       }
        composable(Screen.Calendar.route) {
            MainScreen(rootNavController)
        }
        composable(
            route = Screen.Detail.ROUTE,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) {
        }
        composable(Screen.AddTask.route) {
        }
    }

}