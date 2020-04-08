package com.example.calendarapp.room

import androidx.lifecycle.LiveData

class CalRepository(val calDao: CalDao){

    val allEvent : LiveData<Event> = calDao.getEvent()

    suspend fun insertEvent(event: Event){
        calDao.insertEvent(event)
    }
}