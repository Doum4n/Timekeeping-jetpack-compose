package com.example.timekeeping.ui.assignment.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.timekeeping.ui.assignment.CalendarDay

@Composable
fun ScheduleCalenda(
    calendarDays: List<CalendarDay>,
){
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
                            calendarDay.isAssigned -> Color.LightGray // ✅ hiển thị ngày đã phân công
                            else -> Color.Transparent
                        }
                    ).clickable {
                        calendarDays.map {
                            if (it.day == calendarDay.day) {
                                it.copy(isSelected = !it.isSelected)
                            } else {
                                it
                            }
                        }
                    },

                contentAlignment = Alignment.Center,
            ) {
                Text(text = calendarDay.day)
            }
        }
    }
}