package com.example.timekeeping.ui.calender

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.time.LocalDate
import java.time.YearMonth

class CalendarState(
    initialDate: LocalDate = LocalDate.now()
) {
    var selectedDate by mutableStateOf(initialDate)
    var visibleMonth by mutableStateOf(YearMonth.from(initialDate))
    var visibleDate by mutableStateOf(initialDate)

    fun nextMonth() {
        visibleMonth = visibleMonth.plusMonths(1)
        visibleDate = visibleDate.plusDays(1)
    }

    fun prevMonth() {
        visibleMonth = visibleMonth.minusMonths(1)
        visibleDate = visibleDate.minusDays(1)
    }
}

@Composable
fun rememberCalendarState(
    initialDate: LocalDate = LocalDate.now()
) = remember { CalendarState(initialDate) }