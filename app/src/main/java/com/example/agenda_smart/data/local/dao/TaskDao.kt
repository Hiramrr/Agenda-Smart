package com.example.agenda_smart.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.agenda_smart.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow // Asegúrate de importar esto

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    // Consulta para obtener TODAS las tareas, ordenadas por fecha
    @Query("SELECT * FROM tasks ORDER BY dateTimestamp ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    // Consulta para obtener SOLO las tareas marcadas como favoritas
    @Query("SELECT * FROM tasks WHERE isFavorite = 1 ORDER BY dateTimestamp ASC")
    fun getFavoriteTasks(): Flow<List<TaskEntity>>
}