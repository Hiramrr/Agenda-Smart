package com.example.agenda_smart.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey
    val dayTimestamp: Long,
    val title: String,
    val content: String
)