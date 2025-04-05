package com.example.timekeeping.models

import java.util.Date

class Attendance(
    val employeeId: String = "",
    val shiftId: String = "",
    val dayCheckIn: Date = Date(),
    val startTime: Date = Date(),
    val endTime: Date = Date(),
) {
}