package com.example.timekeeping.ui.calender

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun CalendarScreen() {
    val calendarState = rememberCalendarState()

    Column {
        CustomCalendar(
            state = calendarState,
            onDateSelected = { selectedDate ->
                println("Selected date: $selectedDate")
                // Xử lý khi chọn ngày
            },
            modifier = Modifier.wrapContentHeight()
        )

        // Các thành phần UI khác
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    CalendarScreen()
}

