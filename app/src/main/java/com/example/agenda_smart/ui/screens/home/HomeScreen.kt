package com.example.agenda_smart.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.agenda_smart.ui.navigation.Screen // Importante para la navegación

@Composable
fun HomeScreen(rootNavController: NavHostController) {
    val tabs = listOf("Hoy", "Programados", "Importante")
    var selectedTab by remember { mutableIntStateOf(0) }

    // Usamos Scaffold para poder agregar el FloatingActionButton fácilmente
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Aquí le decimos que navegue a la pantalla de agregar tarea
                    rootNavController.navigate(Screen.AddTask.route)
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar Tarea")
            }
        }
    ) { paddingValues ->
        // Contenido principal de la pantalla
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues) // Respetamos el espacio del Scaffold
        ){
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = {selectedTab = index},
                        text = { Text(text = title)}
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(text = "Contenido de: ${tabs[selectedTab]}")
            }
        }
    }
}