package com.example.timekeeping.ui.assignment.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = employee.name.fullName,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onSharedCalendarClick
                ) {
                    Text(text = "Phân theo tổ")
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onCalendarByEmployeeClick
                ) {
                    Text(text = "Phân theo cá nhân")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ScheduleCalenda(
                state = state,
                expanded = expanded,
                calendar = calendar,
                onClick = { clickedDay -> onClick(clickedDay) },
                onWeekdayToggle = onWeekdayToggle,
                enableToggleWeekday = isEnableToggleWeekday,
                isHideExpandButton = true
            )
        }
    }
}