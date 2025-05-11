package com.example.timekeeping.ui.admin.calender

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.time.LocalDate
import java.time.YearMonth

@Stable
class CalendarState(
    initialDate: LocalDate = LocalDate.now()
) {
    var selectedDate by mutableStateOf(initialDate)
    var visibleMonth by mutableStateOf(YearMonth.from(initialDate))
    var visibleDate by mutableStateOf(initialDate)

    fun nextMonth() {
        visibleMonth = visibleMonth.plusMonths(1)
        visibleDate = visibleDate.plusDays(1) // Để tạm
    }

    fun prevMonth() {
        visibleMonth = visibleMonth.minusMonths(1)
        visibleDate = visibleDate.minusDays(1) // Để tạm
    }
}

@Composable
fun rememberCalendarState(
    initialDate: LocalDate = LocalDate.now()
) = remember { CalendarState(initialDate) }