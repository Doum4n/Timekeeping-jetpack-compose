package com.example.timekeeping.repositories

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
                onResult(attendances)
            }
    }

    fun CheckOut(){
//        val attendance = Attendance(
//            employeeId = "employeeId",
//            shiftId = "shiftId",
//            startTime = Timestamp.now(),
//            endTime = Date(),
//            dayCheckIn = Timestamp.now().toDate()
//        )
//        db.collection("attendances").add(attendance)
    }

    fun getAttendanceByShiftId(shiftId: String, dayCheckIn: Date, onResult: (List<Attendance>) -> Unit) {
        val calendar = Calendar.getInstance()
        calendar.time = dayCheckIn

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1 // Tháng tính từ 0
        val year = calendar.get(Calendar.YEAR)
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