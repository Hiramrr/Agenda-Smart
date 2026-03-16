package com.example.agenda_smart.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.agenda_smart.data.local.AppDatabase
import com.example.agenda_smart.data.local.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
//import jakarta.inject.Singleton
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "agenda_smart.db"
        ).build()

    @Provides
    @Singleton
    fun provideTaskDao(appDatabase: AppDatabase): TaskDao {
        return appDatabase.taskDao()
    }

    // se descomenta hasta que se implemente las notas
    //@Provides
    //fun provideNoteDao(database: AppDatabase) = database.noteDao()
}