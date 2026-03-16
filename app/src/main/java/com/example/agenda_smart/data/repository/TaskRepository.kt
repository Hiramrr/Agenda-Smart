package com.example.agenda_smart.data.repository

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

    // Exponemos la lista completa de tareas
    fun getAllTasks(): Flow<List<TaskEntity>> {
        return taskDao.getAllTasks()
    }

    // Exponemos solo las tareas favoritas
    fun getFavoriteTasks(): Flow<List<TaskEntity>> {
        return taskDao.getFavoriteTasks()
    }
}