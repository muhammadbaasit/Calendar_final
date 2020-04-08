package com.example.calendarapp.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Event(

    @PrimaryKey(autoGenerate = true)
    private val mId: Int = 0,

    @ColumnInfo(name = "name")
    val name: String = "",

    @ColumnInfo(name = "date")
    val date:String = "",

    @ColumnInfo(name = "startTime")
    val startTime: Float = 0f ,

    @ColumnInfo(name = "endTime")
    val endTime: Float = 0f) {

    val startTimeInMinutes: Int
    val endTimeInMinutes: Int

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val event = o as Event
        return mId == event.mId
    }

    override fun hashCode(): Int {
        return mId
    }

    init {
        startTimeInMinutes = (startTime * 60f).toInt()
        endTimeInMinutes = (endTime * 60f).toInt()
    }
}