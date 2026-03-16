package com.example.agenda_smart.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenda_smart.data.DateUtils
import com.example.agenda_smart.data.local.entity.TaskEntity
import com.example.agenda_smart.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

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

    // Lista 4: HISTORIAL
    val historyTasks: StateFlow<List<TaskEntity>> = taskRepository.getHistoryTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveTask(
        title: String,
        description: String,
        dateString: String,
        timeString: String,
        isFavorite: Boolean
    ) {
        viewModelScope.launch {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = formatter.parse(dateString) ?: Date()

            val normalizedTimestamp = DateUtils.inicioDia(date.time)

            val newTask = TaskEntity(
                title = title,
                description = description,
                dateTimestamp = normalizedTimestamp,
                timeString = timeString,
                isFavorite = isFavorite,
                isCompleted = false
            )
            taskRepository.insertTask(newTask)
        }
    }

    // 1. marca la tarea como completada (se irá al historial automáticamente)
    fun completeTask(task: TaskEntity) {
        viewModelScope.launch {
            // Hacemos una copia de la tarea pero con isCompleted = true
            taskRepository.updateTask(task.copy(isCompleted = true))
        }
    }

    // 2. alterna el estado de Favorito (la agrega o quita de Importante)
    fun toggleFavoriteTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.updateTask(task.copy(isFavorite = !task.isFavorite))
        }
    }

    // 3.elimina la tarea de la base de datos por completo
    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }
}