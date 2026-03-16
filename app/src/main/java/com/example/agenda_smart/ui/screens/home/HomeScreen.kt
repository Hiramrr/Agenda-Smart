package com.example.agenda_smart.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.agenda_smart.data.local.entity.TaskEntity
import com.example.agenda_smart.ui.navigation.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    rootNavController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    // ¡Agregamos la cuarta pestaña!
    val tabs = listOf("Hoy", "Programados", "Importante", "Historial")
    var selectedTab by remember { mutableIntStateOf(0) }

    val todayTasks by viewModel.todayTasks.collectAsState()
    val upcomingTasks by viewModel.upcomingTasks.collectAsState()
    val favoriteTasks by viewModel.favoriteTasks.collectAsState()
    // Observamos el historial
    val historyTasks by viewModel.historyTasks.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { rootNavController.navigate(Screen.AddTask.route) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar Tarea")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Hacemos el TabRow desplazable (ScrollableTabRow) porque 4 pestañas pueden no caber en pantallas pequeñas
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 8.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(text = title) }
                    )
                }
            }

            val tasksToShow = when (selectedTab) {
                0 -> todayTasks
                1 -> upcomingTasks
                2 -> favoriteTasks
                3 -> historyTasks // ¡Mostramos el historial!
                else -> emptyList()
            }

            if (tasksToShow.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay tareas para mostrar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tasksToShow) { task ->
                        TaskCard(task = task)
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(task: TaskEntity) {
    // Convertimos los milisegundos a un texto legible (Ej. 25/10/2023)
    val formatter = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
            timeZone = java.util.TimeZone.getTimeZone("UTC")
        }
    }
    val dateString = formatter.format(Date(task.dateTimestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = task.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = task.description, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Ahora mostramos la fecha junto con la hora
                Text(text = "$dateString • ${task.timeString}", style = MaterialTheme.typography.bodySmall)

                if (task.isFavorite) {
                    Text(text = "⭐", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}