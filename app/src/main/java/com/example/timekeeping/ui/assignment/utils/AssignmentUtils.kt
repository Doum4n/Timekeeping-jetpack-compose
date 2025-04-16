package com.example.timekeeping.ui.assignment.utils

import android.annotation.SuppressLint
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
    selectedDays: List<String>,
    selectedWeekdays: List<DayOfWeek>,
    assignedDates: List<Int>
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
        val isSelected = selectedWeekdays.contains(date.dayOfWeek).xor(selectedDays.contains(date.dayOfMonth.toString()))
        val isAssigned = assignedDates.contains(date.dayOfMonth)
        days.add(CalendarDay(day.toString(), isSelected, isAssigned))
    }

    return days
}

fun getDaysOfMonthShrunk(
    selectedDays: List<String>,
    selectedWeekdays: List<DayOfWeek>,
    assignedDates: List<Int>
): List<CalendarDay> {
    val now = LocalDate.now()
    val days = mutableListOf<CalendarDay>()
    val firstDay = now.withDayOfMonth(1)
    val startOffset = (firstDay.dayOfWeek.value + 5) % 6

    repeat(startOffset) {
        days.add(CalendarDay("", false))
    }

    for (day in 1..6) {
        val date = now.withDayOfMonth(day)
        val isSelected = selectedWeekdays.contains(date.dayOfWeek).xor(selectedDays.contains(date.dayOfMonth.toString()))
        val isAssigned = assignedDates.contains(date.dayOfMonth)
        days.add(CalendarDay(day.toString(), isSelected))
    }

    return days
}

fun isEmployeeCalendarModified(employeeId: String, initialCalendarByEmployee: Map<String, List<CalendarDay>>, calendarByEmployee: Map<String, List<CalendarDay>>): Boolean {
    val original = initialCalendarByEmployee[employeeId]
    val current = calendarByEmployee[employeeId]

    // Nếu một trong hai không tồn tại thì coi như có thay đổi
    if (original == null || current == null || original.size != current.size) return true

    return original.zip(current).any { (orig, curr) ->
        orig.isSelected != curr.isSelected ||
                orig.isAssigned != curr.isAssigned
    }
}