package com.example.timekeeping.models

import java.util.Date

class Attendance(
    val employeeId: String = "",
    val shiftId: String = "",
    val date: Date = Date(),
    val startTime: Date = Date(),
    val endTime: Date = Date(),
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "employeeId" to employeeId,
            "shiftId" to shiftId,
            "date" to date
        )
    }
}