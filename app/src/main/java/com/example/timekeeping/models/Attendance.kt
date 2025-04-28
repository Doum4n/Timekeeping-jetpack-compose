package com.example.timekeeping.models

import com.example.timekeeping.utils.DateTimeMap
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
    val note: String = "",
    val startTime: DateTimeMap = DateTimeMap(),
    val endTime: DateTimeMap = DateTimeMap(),
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "employeeId" to employeeId,
            "shiftId" to shiftId,
            "attendanceType" to attendanceType,
            "note" to note,
            "startTime" to startTime,
            "endTime" to endTime
        )
    }
}