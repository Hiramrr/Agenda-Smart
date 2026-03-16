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

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    // Convertimos el Flow del repositorio en un StateFlow para que la UI lo observe
    val allTasks: StateFlow<List<TaskEntity>> = taskRepository.getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList() // Empieza como una lista vacía
        )

    // Hacemos lo mismo para las tareas favoritas (importantes)
    val favoriteTasks: StateFlow<List<TaskEntity>> = taskRepository.getFavoriteTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // La función de guardar que ya tenías
    fun saveTask(
        title: String,
        description: String,
        dateTimestamp: Long,
        timeString: String,
        isFavorite: Boolean
    ) {
        viewModelScope.launch {
            val newTask = TaskEntity(
                title = title,
                description = description,
                dateTimestamp = dateTimestamp,
                timeString = timeString,
                isFavorite = isFavorite,
                isCompleted = false
            )
            taskRepository.insertTask(newTask)
        }
    }
}