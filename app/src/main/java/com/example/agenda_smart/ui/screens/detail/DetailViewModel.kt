package com.example.agenda_smart.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenda_smart.data.local.entity.TaskEntity
import com.example.agenda_smart.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Extraemos el ID de la tarea
    private val taskId: Int = checkNotNull(savedStateHandle["taskId"])

    // Obtenemos SOLO la tarea específica
    val task: StateFlow<TaskEntity?> = taskRepository.getTaskById(taskId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}