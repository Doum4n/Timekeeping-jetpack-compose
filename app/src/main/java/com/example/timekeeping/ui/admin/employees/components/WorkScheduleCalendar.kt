package com.example.timekeeping.ui.admin.employees.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.timekeeping.ui.admin.calender.CalendarState
import com.example.timekeeping.ui.admin.calender.rememberCalendarState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class WorkStatus(val label: String) {
    WORK("Đi làm"),
    HALF_DAY("Nửa ngày"),
    PAID_LEAVE("Nghỉ có lương"),
    UNPAID_LEAVE("Nghỉ không lương"),

    OTHER("Khác")
}

@Composable
fun WorkScheduleCalendar(
    modifier: Modifier = Modifier,
    workStatusMap: Map<LocalDate, WorkStatus>,
    state: CalendarState = rememberCalendarState(),
    onDateSelected: (LocalDate) -> Unit = {}
) {
    Column(modifier = modifier.padding(16.dp).fillMaxWidth()) {
        CalendarHeader(state)

        // Day names header
        Row(Modifier.fillMaxWidth()) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        CalendarGrid(state, workStatusMap)
    }

    LaunchedEffect(state.selectedDate) {
        onDateSelected(state.selectedDate)
    }
}

@Composable
fun CalendarHeader(
    state: CalendarState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { state.prevMonth() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Previous")
        }

        Text(
            text = state.visibleMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("vi", "VN"))),
            style = MaterialTheme.typography.titleMedium
        )

        IconButton(onClick = { state.nextMonth() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, "Next")
        }
    }
}

@Composable
fun CalendarGrid(
    state: CalendarState,
    workStatusMap: Map<LocalDate, WorkStatus>,
    modifier: Modifier = Modifier
) {
    val daysInMonth = state.visibleMonth.lengthOfMonth()
    val firstDayOfMonth = state.visibleMonth.atDay(1)
    val firstDayOfWeek = (firstDayOfMonth.dayOfWeek.value + 5) % 6 // Adjust for Monday start

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = modifier
    ) {
        // Empty cells for days before the 1st of month
        items(firstDayOfWeek) { Spacer(modifier = Modifier) }

        items(daysInMonth) { day ->
            val date = firstDayOfMonth.plusDays(day.toLong())
            val status = workStatusMap[date] ?: WorkStatus.OTHER
            DayItem(
                day = date,
                isSelected = date == state.selectedDate,
                onClick = { state.selectedDate = it },
                workStatus = status
            )
        }
    }
}

@Composable
fun DayItem(
    day: LocalDate,
    isSelected: Boolean,
    workStatus: WorkStatus,
    onClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else Color.Transparent,
                shape = CircleShape
            )
            .clickable { onClick(day) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.dayOfMonth.toString(),
            color = when(workStatus){
                WorkStatus.WORK -> Color.Green
                WorkStatus.HALF_DAY -> Color.Yellow
                WorkStatus.PAID_LEAVE -> Color.Blue
                WorkStatus.UNPAID_LEAVE -> Color.Red
                else -> Color.Gray
            }
        )
    }
}