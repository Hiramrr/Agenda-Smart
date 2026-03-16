package com.example.agenda_smart.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenda_smart.data.local.entity.TaskEntity
import com.example.agenda_smart.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.agenda_smart.data.DateUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    // Lista 1: HOY
    val todayTasks: StateFlow<List<TaskEntity>> = taskRepository.getTodayTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Lista 2: PROGRAMADOS
    val upcomingTasks: StateFlow<List<TaskEntity>> = taskRepository.getUpcomingTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Lista 3: FAVORITAS
    val favoriteTasks: StateFlow<List<TaskEntity>> = taskRepository.getFavoriteTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ¡Cambiamos Long por String en dateString!
    fun saveTask(
        title: String,
        description: String,
        dateString: String, // Ahora recibimos el texto "dd/MM/yyyy"
        timeString: String,
        isFavorite: Boolean
    ) {
        viewModelScope.launch {
            // 1. Convertimos el texto que el usuario ve a una fecha local
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = formatter.parse(dateString) ?: Date()

            // 2. Pasamos esa fecha por DateUtils para normalizarla a las 00:00:00 exactas
            val normalizedTimestamp = DateUtils.inicioDia(date.time)

            val newTask = TaskEntity(
                title = title,
                description = description,
                dateTimestamp = normalizedTimestamp, // Guardamos la fecha perfectamente alineada
                timeString = timeString,
                isFavorite = isFavorite,
                isCompleted = false
            )
            taskRepository.insertTask(newTask)
        }
    }
}