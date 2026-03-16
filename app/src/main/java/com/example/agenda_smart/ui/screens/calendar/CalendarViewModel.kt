package com.example.agenda_smart.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenda_smart.data.DateUtils
import com.example.agenda_smart.data.local.entity.TaskEntity
import com.example.agenda_smart.data.local.entity.NoteEntity
import com.example.agenda_smart.data.repository.NoteRepository
import com.example.agenda_smart.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _selectedDay = MutableStateFlow(DateUtils.inicioDia())
    val selectedDay: StateFlow<Long> = _selectedDay

    val taskForDay: StateFlow<List<TaskEntity>> = _selectedDay
        .flatMapLatest { day -> taskRepository.getTasksForDay(day) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())


    val noteForDay: StateFlow<NoteEntity?> = _selectedDay
        .flatMapLatest { day -> noteRepository.getNoteByDay(day) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun onDaySelected(timestamp: Long){
        _selectedDay.value = DateUtils.inicioDia(timestamp)
    }

    fun saveNote(title: String, content: String){
        viewModelScope.launch {
            noteRepository.saveNote(_selectedDay.value, title, content)
        }
    }
}