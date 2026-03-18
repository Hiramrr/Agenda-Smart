package com.example.agenda_smart.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType

import com.example.agenda_smart.ui.screens.home.AddTaskScreen // Importa tu nueva pantalla
import com.example.agenda_smart.ui.screens.detail.DetailScreen

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
            // Contenido de DetailScreen
            DetailScreen(navController = rootNavController)
        }

        // ¡Aquí conectamos la ruta con la pantalla!
        composable(Screen.AddTask.route) {
            AddTaskScreen(navController = rootNavController)
        }

        // ¡Ruta para EDITAR una tarea! Reutiliza AddTaskScreen pero le pasa el ID
        composable(
            route = Screen.EditTask.ROUTE,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId")
            AddTaskScreen(navController = rootNavController, taskIdToEdit = taskId)
        }
    }
}