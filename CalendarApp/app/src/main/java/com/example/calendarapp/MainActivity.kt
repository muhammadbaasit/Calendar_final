package com.example.calendarapp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.calendarapp.databinding.ActivityMainBinding
import com.example.calendarapp.databinding.DialogLayoutBinding
import com.example.calendarapp.domain.Clique
import com.example.calendarapp.domain.Cluster
import com.example.calendarapp.room.Event
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    var binding : ActivityMainBinding ?= null
    var dialogBinding : DialogLayoutBinding ?= null
    var calViewModel : CalViewModel ?= null
    var COUNT = 1

    private val MINUTES_IN_A_HOUR = 24 * 60
    private val events= ArrayList<Event>()
    private val getevents = ArrayList<Event>()
    private var rlCalendarRoot: RelativeLayout? = null
    val c = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@MainActivity,R.layout.activity_main)
        binding!!.lifecycleOwner
        calViewModel = ViewModelProvider(this@MainActivity).get(CalViewModel::class.java)

        calViewModel.apply {
            binding?.apply {
                viewModel = calViewModel
            }
        }
        dialogBinding?.apply {
                dviewModel = calViewModel
        }

        setCurrentDateTime()

        /*events.add(Event(1, "Event A", "21-22-2020", 1f, 4f))
        events.add(Event(2, "Event B", "21-22-2020", 2f, 4f))
        events.add(Event(3, "Event C", "21-22-2020", 11.5f, 12.5f))
        events.add(Event(4, "Event D", "21-22-2020", 13f, 14.5f))
        events.add(Event(5, "Event E", "21-22-2020", 13f, 13.5f))*/
        /*events.add(Event(6, "Event F", "21-22-2020", 14f, 14.5f))
        events.add(Event(7, "Event G", "21-22-2020", 15f, 16f))
        events.add(Event(8, "Event H", "21-22-2020", 15f, 16.5f))
        events.add(Event(9, "Event I", "21-22-2020", 15.5f, 17.0f))
        events.add(Event(10, "Event J", "21-22-2020", 16.5f, 17.5f))*/

        calViewModel!!.allEvent?.observe(this@MainActivity,androidx.lifecycle.Observer {

                for(items in events){
                    calViewModel!!.insertEvent(Event(0, items.name, items.date, items.startTime, items.endTime))
                    ++ COUNT
                }


        })

        rlCalendarRoot = findViewById(R.id.rl_calendar_root)

       // rlCalendarRoot?.post(Runnable { drawEvents(events!!) })

        calViewModel!!.ontvClick.observe(this@MainActivity,androidx.lifecycle.Observer {
            if(it == true){
                calViewModel!!.datePickerDialog(this)
            }

        })

        calViewModel!!.ftlBtn.observe(this@MainActivity,androidx.lifecycle.Observer {
            if(it == true){
                calViewModel!!.showDialog(this)
            }
        })

        calViewModel!!.btnsaveEvent.observe(this@MainActivity,androidx.lifecycle.Observer {

            if(it == true){
               Log.d("DDD",">>>"+calViewModel?.eventMessage+".."+calViewModel?.eventStartTime+".."+calViewModel?.eventEndTime+""+calViewModel?.eventDate)

                getevents.add(Event(0,calViewModel?.eventMessage!!,calViewModel?.eventDate!!,calViewModel?.eventStartTime?.toFloat()!!,calViewModel?.eventEndTime?.toFloat()!!))

                Log.d("DDD",">>>"+getevents)

                rlCalendarRoot?.post(Runnable { drawEvents(getevents!!) })


                /*calViewModel!!.getEvent(this)
                calViewModel!!.allEvent?.observe(this@MainActivity,androidx.lifecycle.Observer {

                    Log.d("EVEBNTS",""+it.name)
                })*/
            }

            calViewModel!!.dialog?.hide()
        })

    }

    fun setCurrentDateTime(){
        val myFormat1 = "dd.MM.yyyy"
        val sdf1 = SimpleDateFormat(myFormat1, Locale.US)
        tvDate.text = sdf1.format(c.time)
    }


    fun drawEvents(events: List<Event>) {

        if (events != null && !events.isEmpty()) {

            Collections.sort(events,TimeComparator())
            val screenWidth = rlCalendarRoot!!.width
            val screenHeight = (23f * resources.getDimension(R.dimen.hour_divider_height) + 24f * resources.
                getDimension(R.dimen.hour_divider_margin_top)).toInt()

            val clusters: List<Cluster>? = createClusters(createCliques(events))

            for (c in clusters!!) {

                for (event in c.getEvents()!!) {

                    val eventWidth: Int = screenWidth / c.getMaxCliqueSize()
                    val leftMargin: Int = c.getNextPosition() * eventWidth
                    val eventHeight = minutesToPixels(screenHeight, event.endTimeInMinutes + 1) - minutesToPixels(screenHeight, event.startTimeInMinutes)
                    val topMargin = minutesToPixels(screenHeight, event.startTimeInMinutes)
                    val eventView = TextView(this)
                    eventView.text = getString(R.string.event_name_format, event.name)
                    eventView.setTextColor(Color.BLACK)
                    eventView.setBackgroundResource(R.drawable.cardback)
                    val params = RelativeLayout.LayoutParams(eventWidth, eventHeight)
                    params.setMargins(leftMargin, topMargin, 0, 0)
                    rlCalendarRoot!!.addView(eventView, params)
                }
            }
        }
    }

   fun minutesToPixels(screenHeight: Int, minutes: Int): Int {
        return screenHeight * minutes / MINUTES_IN_A_HOUR
    }

    fun createCliques(events: List<Event>): List<Clique> {
        val startTime: Int = events[0].startTimeInMinutes
        val endTime: Int = events[events.size - 1].endTimeInMinutes
        val cliques: MutableList<Clique> = ArrayList<Clique>()
        for (i in startTime..endTime) {
            var c: Clique? = null
            for (e in events) {
                if (e.startTimeInMinutes <= i && e.endTimeInMinutes >= i) {
                    if (c == null) {
                        c = Clique()
                    }
                    c.addEvent(e)
                }
            }
            if (c != null) {
                if (!cliques.contains(c)) {
                    cliques.add(c)
                }
            }
        }
        return cliques
    }

    fun createClusters(cliques: List<Clique?>): List<Cluster>? {
        val clusters: MutableList<Cluster> = ArrayList<Cluster>()
        var cluster: Cluster? = null
        for (c in cliques) {
            if (cluster == null) {
                cluster = Cluster()
                cluster.addClique(c!!)
            } else {
                if (cluster.getLastClique()?.intersects(c!!)!!) {
                    cluster.addClique(c!!)
                } else {
                    clusters.add(cluster)
                    cluster = Cluster()
                    cluster.addClique(c!!)
                }
            }
        }
        if (cluster != null) {
            clusters.add(cluster)
        }
        return clusters
    }

    class TimeComparator : Comparator<Any?> {

        override fun compare(obj1: Any?, obj2: Any?): Int {
            val o1 = obj1 as Event
            val o2 = obj2 as Event
            val change1: Float = o1.startTime
            val change2: Float = o2.startTime
            if (change1 < change2) {
                return -1
            }
            if (change1 > change2) {
                return 1
            }
            val change3: Float = o1.endTime
            val change4: Float = o2.endTime
            if (change3 < change4) return -1
            return if (change3 > change4) 1 else 0
        }
    }
}
