package com.example.agenda_smart.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.agenda_smart.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow
import androidx.room.Update
import androidx.room.Delete

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    // TODAS las tareas (la que ya tenías)
    @Query("SELECT * FROM tasks ORDER BY dateTimestamp ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    // SOLO FAVORITAS (la que ya tenías)
    @Query("SELECT * FROM tasks WHERE isFavorite = 1 ORDER BY dateTimestamp ASC")
    fun getFavoriteTasks(): Flow<List<TaskEntity>>

    // NUEVA: Tareas para un día en específico (Ej. Hoy) ordenadas por hora
    @Query("SELECT * FROM tasks WHERE dateTimestamp = :dayTimestamp ORDER BY timeString ASC")
    fun getTasksForDay(dayTimestamp: Long): Flow<List<TaskEntity>>

    // NUEVA: Tareas programadas para después de hoy
    @Query("SELECT * FROM tasks WHERE dateTimestamp > :dayTimestamp ORDER BY dateTimestamp ASC, timeString ASC")
    fun getUpcomingTasks(dayTimestamp: Long): Flow<List<TaskEntity>>

    // NUEVA: Tareas del historial (ya pasaron de fecha o están completadas)
    @Query("SELECT * FROM tasks WHERE dateTimestamp < :dayTimestamp OR isCompleted = 1 ORDER BY dateTimestamp DESC, timeString DESC")
    fun getHistoryTasks(dayTimestamp: Long): Flow<List<TaskEntity>>

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)
}