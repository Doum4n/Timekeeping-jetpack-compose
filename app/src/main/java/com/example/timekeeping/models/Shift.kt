package com.example.timekeeping.models

import com.google.firebase.firestore.Exclude
import java.time.LocalDateTime
import java.time.LocalTime

data class Time(
    val hour: Int = 0,
    val minute: Int = 0
) : Comparable<Time> {

    override fun compareTo(other: Time): Int {
        return when {
            hour != other.hour -> hour.compareTo(other.hour)
            else -> minute.compareTo(other.minute)
        }
    }

    companion object {
        fun form(localDateTime: LocalDateTime): Time {
            return Time(localDateTime.hour, localDateTime.minute)
        }

        fun form(localTime: LocalTime): Time {
            return Time(localTime.hour, localTime.minute)
        }
    }

    override fun toString(): String {
        return "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
    }
}

data class Shift(
    @Exclude
    var id: String = "",
    val shiftName: String = "",
    val startTime: Time = Time(),
    val endTime: Time = Time(),
    var allowance: Int = 0,
    var coefficient: Double = 1.0,
    val groupId: String = ""
) {

}