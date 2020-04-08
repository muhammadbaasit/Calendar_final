package com.example.calendarapp

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendarapp.room.Event
import com.example.calendarapp.room.CalDb
import com.example.calendarapp.room.CalRepository
import kotlinx.android.synthetic.main.dialog_layout.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CalViewModel : ViewModel(){

    var ftlBtn = MutableLiveData<Boolean>().apply { value = false }
    var ontvClick = MutableLiveData<Boolean>().apply { value = false }
    var btnsaveEvent = MutableLiveData<Boolean>().apply { value = false }
    var repository:CalRepository ?= null
    var allEvent:LiveData<Event> ?= null
    var dialog:Dialog ?= null
    val c = Calendar.getInstance()
    var eventMessage = ""
    var eventDate = ""
    var eventStartTime = ""
    var eventEndTime = ""
    var cnt : Boolean = false

    fun insertEvent(event: Event) = viewModelScope.launch {
        repository?.insertEvent(event)
    }

    fun getEvent(context: Context){
        val calDao = CalDb.getInstance(context).calDao()
        repository = CalRepository(calDao)
        allEvent = repository?.allEvent
    }

    fun onTvClick(view: View){
        ontvClick.value = true
    }

    fun onFltClick(view: View){
        ftlBtn.value = true
    }

    fun showDialog(context: Context){
        dialog = Dialog(context)
        dialog?.setContentView(R.layout.dialog_layout)
        dialog?.setCancelable(true)
        dialog?.show()
        val myFormat2 = "HH.mm"
        val sdf2 = SimpleDateFormat(myFormat2, Locale.US)
        dialog?.textView3?.text = sdf2.format(c.time)
        dialog?.textView4?.text = sdf2.format(c.time)

        dialog?.textView3?.setOnClickListener(View.OnClickListener {
            cnt = true
            timePickerDialog(context)
        })

        dialog?.textView4?.setOnClickListener(View.OnClickListener {
            cnt = false
            timePickerDialog(context)
        })

        dialog?.btnSaveEvent?.setOnClickListener(View.OnClickListener {
            eventMessage = dialog?.tvTitle?.text.toString()
            btnsaveEvent.value = true
        })

        val window: Window? = dialog?.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun datePickerDialog(context: Context){

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            c.set(Calendar.YEAR, year)
            c.set(Calendar.MONTH, monthOfYear)
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd.MM.yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            //textView.text = sdf.format(cal.time)
            eventDate = sdf.format(c.time)


        }
            DatePickerDialog(context, dateSetListener,
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)).show()
    }

    fun timePickerDialog(context: Context){

        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            c.set(Calendar.HOUR_OF_DAY, hour)
            c.set(Calendar.MINUTE, minute)
            val myFormat = "HH.mm" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)


            if(cnt == true){
                dialog?.textView3?.text = sdf.format(c.time)
                eventStartTime = sdf.format(c.time)
            }
            if(cnt == false){
                dialog?.textView4?.text = sdf.format(c.time)
                eventEndTime = sdf.format(c.time)
            }
        }
        TimePickerDialog(context, timeSetListener, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
    }
}