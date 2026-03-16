package com.example.agenda_smart.data.repository

import com.example.agenda_smart.data.DateUtils // ¡Importamos el archivo de tu equipo!
import com.example.agenda_smart.data.local.dao.TaskDao
import com.example.agenda_smart.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    suspend fun insertTask(task: TaskEntity) {
        taskDao.insertTask(task)
    }

    fun getTodayTasks(): Flow<List<TaskEntity>> {
        // Usamos DateUtils para obtener el "Hoy" exacto
        val today = DateUtils.inicioDia()
        return taskDao.getTasksForDay(today)
    }

    fun getUpcomingTasks(): Flow<List<TaskEntity>> {
        val today = DateUtils.inicioDia()
        return taskDao.getUpcomingTasks(today)
    }

    fun getFavoriteTasks(): Flow<List<TaskEntity>> {
        return taskDao.getFavoriteTasks()
    }
}