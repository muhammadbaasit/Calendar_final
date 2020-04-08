package com.example.calendarapp.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CalDao{
  /*  @Query("select * from Event where date=:date")
    fun getEvent(date:String) : LiveData<Event>*/

    @Query("select * from Event")
    fun getEvent() : LiveData<Event>

    @Insert
    suspend fun insertEvent(event: Event)
}