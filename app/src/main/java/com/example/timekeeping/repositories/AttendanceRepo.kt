package com.example.timekeeping.repositories

import com.example.timekeeping.models.Attendance
import com.example.timekeeping.models.Employee
import com.example.timekeeping.utils.convertToReference
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class AttendanceRepo @Inject constructor(
    val db: FirebaseFirestore
) {
    fun CheckIn(attendance: Attendance){
        db.collection("attendances").add(attendance)
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

        // Set về đầu ngày (00:00:00.000)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.time

        // Tăng ngày lên 1 để lấy endOfDay (00:00:00 của ngày hôm sau)
        calendar.add(Calendar.DATE, 1)
        val endOfDay = calendar.time

        println("🔍 shiftId: $shiftId")
        println("🔍 dayCheckIn: $dayCheckIn")
        println("🔍 startOfDay: $startOfDay")
        println("🔍 endOfDay: $endOfDay")

        db.collection("attendances")
            .whereEqualTo("shiftId", shiftId)
            .whereGreaterThanOrEqualTo("dayCheckIn", startOfDay)
            .whereLessThan("dayCheckIn", endOfDay) // dùng lessThan thay vì lessThanOrEqualTo
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