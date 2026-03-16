package com.example.agenda_smart.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Subject
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.agenda_smart.data.local.entity.TaskEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
    taskIdToEdit: Int? = null
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isFavorite by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var selectedDateText by remember { mutableStateOf("") }

    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState()
    var selectedTimeText by remember { mutableStateOf("") }

    // Definimos un color dorado para la estrella
    val goldColor = Color(0xFFFFC107)

    // --- LÓGICA DE EDICIÓN ---
    var existingTask by remember { mutableStateOf<TaskEntity?>(null) }
    var isLoading by remember { mutableStateOf(taskIdToEdit != null) }

    // Si recibimos un ID, cargamos los datos de la base de datos
    LaunchedEffect(taskIdToEdit) {
        if (taskIdToEdit != null) {
            val task = viewModel.getTaskByIdSync(taskIdToEdit)
            if (task != null) {
                existingTask = task
                title = task.title
                description = task.description
                isFavorite = task.isFavorite
                selectedTimeText = task.timeString

                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply { timeZone = java.util.TimeZone.getTimeZone("UTC") }
                selectedDateText = formatter.format(Date(task.dateTimestamp))
                datePickerState.selectedDateMillis = task.dateTimestamp
            }
            isLoading = false
        }
    }

    // Título dinámico
    val screenTitle = if (taskIdToEdit != null) "Editar Actividad" else "Nueva Actividad"

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(screenTitle, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Scaffold
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Colores personalizados para los campos de texto (Soft UI)
            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Transparent, // Sin borde cuando no está enfocado
            )

            // Campo de Título
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título de la actividad") },
                leadingIcon = {
                    Icon(Icons.Outlined.Edit, contentDescription = "Título", tint = MaterialTheme.colorScheme.primary)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = textFieldColors
            )

            // Campo de Descripción
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción (opcional)") },
                leadingIcon = {
                    Icon(Icons.Outlined.Subject, contentDescription = "Descripción", tint = MaterialTheme.colorScheme.primary)
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(16.dp),
                colors = textFieldColors
            )

            // Fila para Fecha y Hora
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = selectedDateText.ifEmpty { "Fecha" },
                    onValueChange = { },
                    readOnly = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Fecha", tint = MaterialTheme.colorScheme.primary)
                        }
                    },
                    colors = textFieldColors
                )

                OutlinedTextField(
                    value = selectedTimeText.ifEmpty { "Hora" },
                    onValueChange = { },
                    readOnly = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = {
                        IconButton(onClick = { showTimePicker = true }) {
                            Icon(Icons.Default.AccessTime, contentDescription = "Hora", tint = MaterialTheme.colorScheme.primary)
                        }
                    },
                    colors = textFieldColors
                )
            }

            // Switch de Favorito rediseñado
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { isFavorite = !isFavorite },
                color = if (isFavorite) goldColor.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Favorito",
                            tint = if (isFavorite) goldColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Marcar como importante",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = if (isFavorite) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isFavorite,
                        onCheckedChange = { isFavorite = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = goldColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ¡BOTÓN DE GUARDAR DINÁMICO!
            Button(
                onClick = {
                    if (existingTask != null) {
                        // MODO EDICIÓN
                        viewModel.updateTaskFromEdit(
                            existingTask = existingTask!!,
                            newTitle = title,
                            newDescription = description,
                            newDateString = selectedDateText,
                            newTimeString = selectedTimeText,
                            newIsFavorite = isFavorite
                        )
                    } else {
                        // MODO NUEVA TAREA
                        viewModel.saveTask(
                            title = title,
                            description = description,
                            dateString = selectedDateText,
                            timeString = selectedTimeText,
                            isFavorite = isFavorite
                        )
                    }
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 2.dp),
                enabled = title.isNotBlank() && selectedDateText.isNotBlank() && selectedTimeText.isNotBlank()
            ) {
                Text(
                    text = if (existingTask != null) "Actualizar Actividad" else "Guardar Actividad",
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // --- Diálogos de Fecha y Hora---
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        // ¡AGREGA ESTA LÍNEA!
                        formatter.timeZone = java.util.TimeZone.getTimeZone("UTC")
                        selectedDateText = formatter.format(Date(millis))
                    }
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showTimePicker = false
                    val hour = timePickerState.hour.toString().padStart(2, '0')
                    val minute = timePickerState.minute.toString().padStart(2, '0')
                    selectedTimeText = "$hour:$minute"
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}