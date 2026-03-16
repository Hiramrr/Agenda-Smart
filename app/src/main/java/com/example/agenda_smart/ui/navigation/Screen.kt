package com.example.agenda_smart.ui.navigation

sealed class Screen(val route: String) {
    object Task: Screen("task")
    object Calendar: Screen("calendar")

    data class Detail(val taskId: Int) : Screen("detail/{taskId"){
        companion object {
            const val ROUTE = "detail/{taskId}"
            fun createRoute(taskId: Int) = "detail/$taskId"
        }
    }

    object  AddTask : Screen("add_task")

}