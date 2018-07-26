package com.marcinmejner.miaumiau.utils

import java.text.SimpleDateFormat
import java.util.*

object DateManipulations {

    fun getTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("Europe/Warsaw")

        return sdf.format(Date())
    }

    fun getMinutesAndHours(): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("Europe/Warsaw")

        return sdf.format(Date())
    }
}