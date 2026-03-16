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

@Composable
fun HomeScreen(
    rootNavController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel() // Inyectamos el ViewModel
) {
    val tabs = listOf("Hoy", "Programados", "Importante")
    var selectedTab by remember { mutableIntStateOf(0) }

    // Observamos las listas de tareas desde el ViewModel
    val allTasks by viewModel.allTasks.collectAsState()
    val favoriteTasks by viewModel.favoriteTasks.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { rootNavController.navigate(Screen.AddTask.route) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar Tarea")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(text = title) }
                    )
                }
            }

            // Dependiendo de la pestaña seleccionada, elegimos qué lista mostrar
            val tasksToShow = when (selectedTab) {
                2 -> favoriteTasks // Pestaña "Importante"
                else -> allTasks   // Pestañas "Hoy" y "Programados" (por ahora muestran todas)
            }

            if (tasksToShow.isEmpty()) {
                // Mensaje si no hay tareas
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay tareas para mostrar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                // Dibujamos la lista de tareas
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

// Componente visual para cada Tarea (Tarjetita)
@Composable
fun TaskCard(task: TaskEntity) {
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
                Text(text = "Hora: ${task.timeString}", style = MaterialTheme.typography.bodySmall)
                if (task.isFavorite) {
                    Text(text = "⭐ Importante", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}