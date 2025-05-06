package com.example.timekeeping.ui.calender

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun CalendarScreen() {
    val calendarState = rememberCalendarState()

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .then(Modifier.heightIn(max = 500.dp)) // giới hạn chiều cao tối đa
    ) {
        CustomCalendar(
            state = calendarState,
            onDateSelected = { selectedDate ->
                println("Selected date: $selectedDate")
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp) // đảm bảo không vượt quá
        )

        // Nếu có thêm UI, tiếp tục bên dưới
    }
}


@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    CalendarScreen()
}

