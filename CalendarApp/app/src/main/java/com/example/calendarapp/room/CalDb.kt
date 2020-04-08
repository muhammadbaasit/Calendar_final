package com.example.calendarapp.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Event::class],version = 1)
abstract class CalDb : RoomDatabase(){

    abstract fun calDao() : CalDao

    companion object{

        @Volatile
        private var INSTANCE : CalDb? = null

        fun getInstance(context:Context) : CalDb {

            val  tempInstance = INSTANCE

            if(tempInstance != null){
                return tempInstance
            }

            synchronized(CalDb::class){
                val instance = Room.databaseBuilder(context.applicationContext,
                    CalDb::class.java,"caldb").build()
                INSTANCE = instance
                return instance
            }
        }
    }
}