package com.example.timekeeping.ui.assignment.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.timekeeping.models.Employee
import com.example.timekeeping.ui.assignment.utils.Calendar
import com.example.timekeeping.ui.assignment.utils.CalendarDay
import com.example.timekeeping.ui.calender.CalendarState
import java.time.DayOfWeek

@Composable
fun EmployeeItem(
    state: CalendarState,
    employee: Employee,
    calendar: Calendar.Individual,
    onSharedCalendarClick: () -> Unit = {},
    onCalendarByEmployeeClick: () -> Unit = {},
    expanded: Boolean = false,
    onClick: (CalendarDay) -> Unit = {},
    onWeekdayToggle: (DayOfWeek) -> Unit = {},
    isEnableToggleWeekday: Boolean = true
) {
    Card(
        modifier = Modifier.padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(text = employee.name.fullName)
                Button(
                    modifier = Modifier.wrapContentSize(),
                    onClick = onSharedCalendarClick
                ) {
                    Text(text = "Phân theo tổ")
                }
                Button(
                    modifier = Modifier.wrapContentSize(),
                    onClick = onCalendarByEmployeeClick
                ) {
                    Text(text = "Phân theo cá nhân")
                }
            }

            ScheduleCalenda(
                state = state,
                expanded = expanded,
                calendar = calendar,
                onClick = { clickedDay ->
                    onClick(clickedDay)
                },
                onWeekdayToggle = { onWeekdayToggle(it) },
                enableToggleWeekday = isEnableToggleWeekday
            )

        }
    }
}