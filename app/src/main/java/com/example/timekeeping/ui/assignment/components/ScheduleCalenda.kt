package com.example.timekeeping.ui.assignment.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timekeeping.ui.assignment.utils.Calendar
import com.example.timekeeping.ui.assignment.utils.CalendarDay
import com.example.timekeeping.ui.assignment.utils.getDaysOfMonthShrunk
import com.example.timekeeping.ui.assignment.utils.getShortNameFor
import com.example.timekeeping.ui.assignment.utils.getWeekdays
import com.example.timekeeping.ui.calender.CalendarState
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ScheduleCalenda(
    state: CalendarState,
    expanded: Boolean = false,
    onExpandedChange: () -> Unit = {},
    calendar: Calendar,
    onClick: (CalendarDay) -> Unit,
    onWeekdayToggle: (DayOfWeek) -> Unit,
    enableToggleWeekday: Boolean = true,

    isHideExpandButton: Boolean = false
){
    val calendarDays = when (calendar) {
        is Calendar.Shared -> calendar.calendarDay
        is Calendar.Individual -> calendar.calendarByEmployee[calendar.employeeId] ?: listOf()
    }

    val selectedWeekdays = when (calendar) {
        is Calendar.Shared -> calendar.selectedWeekdays
        is Calendar.Individual -> calendar.weekdayByEmployee[calendar.employeeId] ?: mutableListOf()
    }

    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.weight(1f)) {
            CalendarHeader(state)
        }
        if(!isHideExpandButton) {
            Button(
                onClick = { onExpandedChange() },
            ) {
                Text(text = if (expanded) "Thu gọn" else "Mở rộng")
            }
        }
    }

    // Weekday Selector
    WeekSelector(
        selectedWeekdays = selectedWeekdays,
        onWeekdayToggle = { onWeekdayToggle(it) },
        enabled = enableToggleWeekday
    )
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier
            .padding(16.dp)
            .heightIn(max = 300.dp)
            .fillMaxWidth(),
        userScrollEnabled = false
    ) {
        items(calendarDays) { calendarDay ->
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .aspectRatio(1f)
                    .background(
                        color = when {
                            calendarDay.isSelected -> MaterialTheme.colorScheme.primary
                            calendarDay.isAssigned -> Color.LightGray
                            else -> Color.Transparent
                        }
                    )
                    .clickable {
                        onClick(calendarDay)
                    },

                contentAlignment = Alignment.Center,
            ) {
                Text(text = calendarDay.day)
            }
        }
    }
}

@Composable
fun CalendarHeader(
    state: CalendarState,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = { state.prevMonth() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
        }

        Box(
            modifier = modifier
        ){
            Text(
                text = state.visibleMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("vi", "VN"))),
                style = MaterialTheme.typography.titleMedium
            )
        }

        IconButton(onClick = { state.nextMonth() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
        }
    }
}

@Composable
fun WeekSelector(
    selectedWeekdays: MutableList<DayOfWeek>,
    onWeekdayToggle: (DayOfWeek) -> Unit,
    enabled: Boolean = true
){
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        getWeekdays().forEach { dayOfWeek ->
            val isChecked = selectedWeekdays.contains(dayOfWeek)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = getShortNameFor(dayOfWeek))
                Checkbox(
                    enabled = enabled,
                    checked = isChecked,
                    onCheckedChange = { onWeekdayToggle(dayOfWeek) },
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}