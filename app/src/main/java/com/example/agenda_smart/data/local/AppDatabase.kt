package com.example.agenda_smart.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.agenda_smart.data.local.dao.NoteDao
import com.example.agenda_smart.data.local.dao.TaskDao
import com.example.agenda_smart.data.local.entity.NoteEntity
import com.example.agenda_smart.data.local.entity.TaskEntity

@Database(
    entities = [
        TaskEntity::class, NoteEntity::class
    ],
    version = 1,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    //abstract fun noteDao(): NoteDao
}