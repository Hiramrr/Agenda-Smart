package com.example.agenda_smart.data

import java.util.Calendar

object DateUtils {

    fun inicioDia(timestamp: Long): Long{
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    fun inicioDia():Long = inicioDia(System.currentTimeMillis())
}