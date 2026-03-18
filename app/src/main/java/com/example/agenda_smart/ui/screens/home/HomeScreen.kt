package com.example.agenda_smart.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.agenda_smart.data.local.entity.TaskEntity
import com.example.agenda_smart.ui.navigation.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.filled.DateRange


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    rootNavController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val tabs = listOf("Hoy", "Programados", "Importante", "Historial")
    var selectedTab by remember { mutableIntStateOf(0) }

    val todayTasks by viewModel.todayTasks.collectAsState()
    val upcomingTasks by viewModel.upcomingTasks.collectAsState()
    val favoriteTasks by viewModel.favoriteTasks.collectAsState()
    val historyTasks by viewModel.historyTasks.collectAsState()

    var selectedTask by remember { mutableStateOf<TaskEntity?>(null) }
    val sheetState = rememberModalBottomSheetState()

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
            ScrollableTabRow(selectedTabIndex = selectedTab, edgePadding = 8.dp) {
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
                3 -> historyTasks
                else -> emptyList()
            }

            if (tasksToShow.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay tareas para mostrar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tasksToShow) { task ->
                        TaskCard(
                            task = task,
                            onClick = {
                                rootNavController.navigate(Screen.Detail.createRoute(task.id))
                            },
                            onLongClick = {
                                selectedTask = task // ¡Aquí activamos el menú inferior!
                            }
                        )
                    }
                }
            }
        }

        // --- MENÚ INFERIOR (BOTTOM SHEET) MINIMALISTA ---
        if (selectedTask != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedTask = null },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface // Fondo limpio
            ) {
                // Usamos un Row con SpaceEvenly para distribuir los botones como en tu imagen
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp, top = 8.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Completar
                    if (!selectedTask!!.isCompleted) {
                        MenuActionItem(
                            icon = Icons.Default.Check,
                            label = "Completar",
                            onClick = {
                                viewModel.completeTask(selectedTask!!)
                                selectedTask = null
                            }
                        )
                    }

                    // 2. Importante
                    MenuActionItem(
                        icon = if (selectedTask!!.isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                        label = "Importante",
                        iconColor = if (selectedTask!!.isFavorite) Color(0xFFFFC107) else MaterialTheme.colorScheme.onSurface,
                        onClick = {
                            viewModel.toggleFavoriteTask(selectedTask!!)
                            selectedTask = null
                        }
                    )

                    // 3. Eliminar
                    MenuActionItem(
                        icon = Icons.Outlined.Delete,
                        label = "Eliminar",
                        iconColor = MaterialTheme.colorScheme.error,
                        onClick = {
                            viewModel.deleteTask(selectedTask!!)
                            selectedTask = null
                        }
                    )
                }
            }
        }
    }
}

// componente visual para cada botoncito del menú inferior
@Composable
fun MenuActionItem(
    icon: ImageVector,
    label: String,
    iconColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskCard(
    task: TaskEntity,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val formatter = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
            timeZone = java.util.TimeZone.getTimeZone("UTC")
        }
    }
    val dateString = formatter.format(Date(task.dateTimestamp))

    // eeefectos visuales si la tarea ya está completada
    val cardAlpha = if (task.isCompleted) 0.6f else 1f
    val textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() }
            )
            .alpha(cardAlpha),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // --- FILA SUPERIOR: Textos e Íconos ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textDecoration = textDecoration // Se tacha si está completada
                    )

                    if (task.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = textDecoration,
                            maxLines = 2, // Limita a 2 líneas para mantener el minimalismo
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Íconos de estado agrupados a la derecha
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(start = 12.dp)
                ) {
                    if (task.isCompleted) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completada",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    if (task.isFavorite) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Importante",
                            tint = Color(0xFFFFC107), // Color Dorado
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- FILA INFERIOR: Fecha y hora ---
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$dateString • ${task.timeString}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}