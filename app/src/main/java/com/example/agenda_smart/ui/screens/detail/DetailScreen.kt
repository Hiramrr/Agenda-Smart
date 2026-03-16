package com.example.agenda_smart.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Star
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.agenda_smart.ui.navigation.Screen //esperando que este correcto
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    navController: NavHostController,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val task by viewModel.task.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                // ¡Agregamos el botón de editar aquí!
                actions = {
                    IconButton(
                        onClick = {
                            // Navegamos a la ruta de edición enviando el ID
                            task?.let { navController.navigate(Screen.EditTask.createRoute(it.id)) }
                        }
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Editar Tarea")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        if (task == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val currentTask = task!!
        val formatter = remember { SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault()).apply { timeZone = java.util.TimeZone.getTimeZone("UTC") } }
        val dateString = formatter.format(Date(currentTask.dateTimestamp))

        // Hacemos que la pantalla sea scrolleable por si la descripción es muy larga
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // --- 1. SECCIÓN HERO (Título y Estado) ---
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Etiquetas de estado (Badges)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (currentTask.isCompleted) {
                        StatusBadge(
                            text = "Completada",
                            icon = Icons.Outlined.CheckCircle,
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    if (currentTask.isFavorite) {
                        StatusBadge(
                            text = "Importante",
                            icon = Icons.Outlined.Star,
                            containerColor = Color(0xFFFFF8E1), // Amarillo muy suave
                            contentColor = Color(0xFFFFB300)  // Dorado
                        )
                    }
                }

                // Título gigante
                Text(
                    text = currentTask.title,
                    style = MaterialTheme.typography.displaySmall, // Tipografía súper grande
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = MaterialTheme.typography.displaySmall.lineHeight
                )
            }

            // --- 2. TARJETA DE FECHA Y HORA ---
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) // Fondo muy suave
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Bloque de Fecha
                    InfoBlock(
                        icon = Icons.Outlined.CalendarToday,
                        label = "Fecha",
                        value = dateString
                    )

                    // Pequeño divisor vertical
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .width(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )

                    // Bloque de Hora
                    InfoBlock(
                        icon = Icons.Outlined.AccessTime,
                        label = "Hora",
                        value = currentTask.timeString
                    )
                }
            }

            // --- 3. DESCRIPCIÓN ---
            if (currentTask.description.isNotBlank()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Detalles",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = currentTask.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                    )
                }
            }
        }
    }
}

// Componente visual para los "Badges" (Etiquetas estilo píldora)
@Composable
fun StatusBadge(text: String, icon: ImageVector, containerColor: Color, contentColor: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50)) // Forma de píldora
            .background(containerColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(16.dp))
        Text(text = text, style = MaterialTheme.typography.labelMedium, color = contentColor, fontWeight = FontWeight.Bold)
    }
}

// Componente visual para el texto dentro de la tarjeta de Fecha/Hora
@Composable
fun InfoBlock(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        // Círculo para el ícono
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        }

        Column {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}