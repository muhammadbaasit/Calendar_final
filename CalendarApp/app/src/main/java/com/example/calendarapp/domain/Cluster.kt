package com.example.calendarapp.domain

import com.example.calendarapp.room.Event
import java.util.*

class Cluster {

    private val cliques: MutableList<Clique> = ArrayList()
    private var maxCliqueSize = 1
    private var nextCurrentDrawPosition = 0

    fun addClique(c: Clique) {
        cliques.add(c)
        maxCliqueSize = Math.max(maxCliqueSize, c.getEvents().size)
    }

    fun getMaxCliqueSize(): Int {
        return maxCliqueSize
    }

    fun getLastClique(): Clique? {
        return if (cliques.size > 0) {
            cliques[cliques.size - 1]
        } else null
    }

    fun getEvents(): List<Event>? {
        val events: MutableList<Event> = ArrayList()
        for (clique in cliques) {
            for (event in clique.getEvents()) {
                if (!events.contains(event)) {
                    events.add(event)
                }
            }
        }
        return events
    }

    fun getNextPosition(): Int {
        var position = nextCurrentDrawPosition
        if (position >= maxCliqueSize) {
            position = 0
        }
        nextCurrentDrawPosition = position + 1
        return position
    }
}