package com.example.agenda_smart.data.repository

import com.example.agenda_smart.data.DateUtils
import com.example.agenda_smart.data.local.dao.NoteDao
import com.example.agenda_smart.data.local.entity.NoteEntity
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class NoteRepository @Inject constructor(
    private val noteDao: NoteDao
) {
    fun getNoteByDay(dayTimestamp: Long): Flow<NoteEntity?> =
        noteDao.getNoteByDay(DateUtils.inicioDia(dayTimestamp))

    suspend fun saveNote(dayTimestamp: Long, title: String, content: String) {
        val note = NoteEntity(
            dayTimestamp = DateUtils.inicioDia(dayTimestamp),
            title = title,
            content = content
        )
        noteDao.insertNote(note)
    }
    
    suspend fun deleteNote(dayTimestamp: Long) {
        noteDao.deleteNote(DateUtils.inicioDia(dayTimestamp))
    }
}