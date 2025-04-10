package com.example.timekeeping.ui.assignment.utils

import android.annotation.SuppressLint
import com.example.timekeeping.ui.assignment.CalendarDay
import java.time.DayOfWeek
import java.time.LocalDate


fun getWeekdays(): List<DayOfWeek> = listOf(
    DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
    DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
)

fun getShortNameFor(dayOfWeek: DayOfWeek): String {
    return when (dayOfWeek) {
        DayOfWeek.MONDAY -> "T2"
        DayOfWeek.TUESDAY -> "T3"
        DayOfWeek.WEDNESDAY -> "T4"
        DayOfWeek.THURSDAY -> "T5"
        DayOfWeek.FRIDAY -> "T6"
        DayOfWeek.SATURDAY -> "T7"
        DayOfWeek.SUNDAY -> "CN"
    }
}

@SuppressLint("SimpleDateFormat")
fun getDaysOfMonthExpanded(
    selectedWeekdays: List<DayOfWeek>,
    assignedDates: List<DayOfWeek>
): List<CalendarDay> {
    val now = LocalDate.now()
    val firstDay = now.withDayOfMonth(1)
    val lastDay = now.withDayOfMonth(now.lengthOfMonth())

    val startOffset = (firstDay.dayOfWeek.value + 5) % 6
    val days = mutableListOf<CalendarDay>()

    // Add prefix empty cells
    repeat(startOffset) {
        days.add(CalendarDay("", false))
    }

    for (day in 1..lastDay.dayOfMonth) {
        val date = now.withDayOfMonth(day)
        val isSelected = selectedWeekdays.contains(date.dayOfWeek)
        val isAssigned = assignedDates.contains(date.dayOfWeek)
        days.add(CalendarDay(day.toString(), isSelected, isAssigned))
    }

    return days
}

fun getDaysOfMonthShrunk(selectedWeekdays: List<DayOfWeek>): List<CalendarDay> {
    val now = LocalDate.now()
    val days = mutableListOf<CalendarDay>()
    val firstDay = now.withDayOfMonth(1)
    val startOffset = (firstDay.dayOfWeek.value + 5) % 6

    repeat(startOffset) {
        days.add(CalendarDay("", false))
    }

    for (i in 1..6) {
        val date = now.plusDays(i.toLong())
        val isSelected = selectedWeekdays.contains(date.dayOfWeek)
        days.add(CalendarDay(i.toString(), isSelected))
    }

    return days
}

