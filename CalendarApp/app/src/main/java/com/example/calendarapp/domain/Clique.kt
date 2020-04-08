package com.example.calendarapp.domain

import com.example.calendarapp.room.Event
import java.util.*

class Clique {
    private val events: MutableList<Event> = ArrayList<Event>()
    fun getEvents(): List<Event> {
        return events
    }

    fun addEvent(node: Event) {
        events.add(node)
    }

    fun intersects(clique2: Clique): Boolean {
        for (i in events) {
            for (k in clique2.events) {
                if (i.equals(k)) {
                    return true
                }
            }
        }
        return false
    }
}
