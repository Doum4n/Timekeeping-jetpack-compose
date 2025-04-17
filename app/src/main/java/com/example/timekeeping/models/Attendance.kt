package com.example.timekeeping.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

data class Attendance(
    @Exclude
    var id: String = "",
    val employeeId: DocumentReference = FirebaseFirestore.getInstance().document(""),
    val shiftId: String = "",
    val attendanceType: String = "",
    val dayCheckIn: Date = Date(),
    val startTime: Timestamp = Timestamp.now(),
    val endTime: Date = Date(),
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "employeeId" to employeeId,
            "shiftId" to shiftId,
            "attendanceType" to attendanceType,
            "dayCheckIn" to dayCheckIn,
            "startTime" to startTime,
            "endTime" to endTime
        )
    }
}