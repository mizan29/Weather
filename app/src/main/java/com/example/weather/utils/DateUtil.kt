package com.example.weather.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

object DateUtil {

    fun getDate(dateInSeconds: Long): String? {
        val date = Date(dateInSeconds * 1000)
        val dateFormat = SimpleDateFormat("dd MMM, yyyy")
        return dateFormat.format(date)
    }

    fun getTime(timeInSeconds: Long): String? {
        val time = Date(timeInSeconds * 1000)
        val dateFormat = SimpleDateFormat("hh:mm a")
        return dateFormat.format(time)
    }

    fun getDay(timeInSeconds: Long): String? {
        val currentDate = Calendar.getInstance().time
        val day = Date(timeInSeconds * 1000)
        val datFormat = SimpleDateFormat("EEEE")
        val dayNameString = datFormat.format(day)
        return if (currentDate == day) {
            "Today"
        } else dayNameString
    }
}