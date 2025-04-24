package com.example.timekeeping.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class DateTimeMap(
    val year: Int = 0,
    val month: Int = 0,
    val day: Int = 0,
    val hour: Int = 0,
    val minute: Int = 0
) {
    fun toLocalDateTime(): LocalDateTime {
        return LocalDateTime.of(year, month, day, hour, minute)
    }

    fun format(pattern: String = "dd-MM-yyyy HH:mm"): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return toLocalDateTime().format(formatter)
    }

    fun isSameMonth(other: DateTimeMap): Boolean {
        return this.year == other.year &&
                this.month == other.month
    }

    override fun equals(other: Any?): Boolean {
        return this.day == (other as DateTimeMap).day &&
                this.month == other.month &&
                this.year == other.year
    }

    companion object {
        fun from(localDateTime: LocalDateTime): DateTimeMap {
            return DateTimeMap(
                year = localDateTime.year,
                month = localDateTime.monthValue,
                day = localDateTime.dayOfMonth,
                hour = localDateTime.hour,
                minute = localDateTime.minute
            )
        }
        fun from(localDate: LocalDate): DateTimeMap {
            return DateTimeMap(
                year = localDate.year,
                month = localDate.monthValue,
                day = localDate.dayOfMonth
            )
        }
    }
}
