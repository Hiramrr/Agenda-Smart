package com.example.agenda_smart.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.agenda_smart.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes WHERE dayTimestamp = :dayTimestamp")
    fun getNoteByDay(dayTimestamp: Long): Flow<NoteEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE dayTimestamp = :dayTimestamp")
    suspend fun deleteNote(dayTimestamp: Long)
}