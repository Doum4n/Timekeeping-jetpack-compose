package com.example.timekeeping.repositories

import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.timekeeping.models.Attendance
import com.example.timekeeping.models.Employee
import com.example.timekeeping.utils.convertToReference
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class AttendanceRepo @Inject constructor(
    val db: FirebaseFirestore
) {

    fun CheckIn(attendance: Attendance){
        db.collection("attendances")
            .add(attendance)
    }

    fun getAttendanceByEmployeeId(employeeId: String, month: Int, year: Int, onResult: (List<Attendance>) -> Unit) {
        db.collection("attendances")
            .whereEqualTo("employeeId", employeeId.convertToReference("employees"))
            .whereEqualTo("startTime.month", month)
            .whereEqualTo("startTime.year", year)
            .get()
            .addOnSuccessListener { result ->
                val attendances = result.documents.mapNotNull { doc ->
                    doc.toObject(Attendance::class.java)?.apply { id = doc.id }
                }
                Log.d("AttendanceRepo", "getAttendanceByEmployeeId: $attendances")
                onResult(attendances)
            }
    }

    fun getAttendanceByEmployeeIdAndDate(employeeId: String, date: LocalDate, onResult: (List<Attendance>) -> Unit) {
        db.collection("attendances")
            .whereEqualTo("employeeId", employeeId.convertToReference("employees"))
            .whereEqualTo("startTime.year", date.year)
            .whereEqualTo("startTime.month", date.monthValue)
            .whereEqualTo("startTime.day", date.dayOfMonth)
            .get()
            .addOnSuccessListener { result ->
                val attendances = result.documents.mapNotNull { doc ->
                    doc.toObject(Attendance::class.java)?.apply { id = doc.id }
                }
                Log.d("AttendanceRepo", "getAttendanceByEmployeeId: $attendances")
                onResult(attendances)
            }
    }

    fun CheckOut(attendance: Attendance){
        Log.d("AttendanceRepo", "CheckOut: $attendance")
        db.collection("attendances")
            .document(attendance.id)
            .update(attendance.toMap())
    }

    fun getAttendanceByShiftId(shiftId: String, dayCheckIn: Date, onResult: (List<Attendance>) -> Unit) {
        val calendar = Calendar.getInstance()
        calendar.time = dayCheckIn

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1 // Tháng tính từ 0
        val year = calendar.get(Calendar.YEAR)
        Log.d("AttendanceRepo", "shiftId: $shiftId, month: $month, year: $year, day: $day")

        db.collection("attendances")
            .whereEqualTo("shiftId", shiftId)
            .whereEqualTo("startTime.month", month)
            .whereEqualTo("startTime.year", year)
            .whereEqualTo("startTime.day", day)
            .get()
            .addOnSuccessListener { result ->
                val attendances = result.documents.mapNotNull { doc ->
                    doc.toObject(Attendance::class.java)?.apply { id = doc.id }
                }
                Log.d("AttendanceRepo", "getAttendanceByShiftId: $attendances")
                onResult(attendances)
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
    }


    fun updateAttendance(attendanceId: String, attendance: Attendance) {
        val attendanceRef = db.collection("attendances").document(attendanceId).update(attendance.toMap())
    }
}