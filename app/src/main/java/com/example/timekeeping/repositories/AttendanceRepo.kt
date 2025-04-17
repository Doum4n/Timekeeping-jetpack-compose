package com.example.timekeeping.repositories

import com.example.timekeeping.models.Attendance
import com.example.timekeeping.models.Employee
import com.example.timekeeping.utils.convertToReference
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import javax.inject.Inject

class AttendanceRepo @Inject constructor(
    val db: FirebaseFirestore
) {
    fun CheckIn(employeeId: String, shiftId: String, attendanceType: String){
        val attendance = Attendance(
            employeeId = employeeId.convertToReference("employees"),
            shiftId = shiftId,
            attendanceType = attendanceType,
            startTime = Timestamp.now(),
            endTime = Date(),
            dayCheckIn = Timestamp.now().toDate()
        )
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

    fun getAttendanceByShiftId(shiftId: String, onResult: (List<Attendance>) -> Unit){
        db.collection("attendances").whereEqualTo("shiftId", shiftId).get()
            .addOnSuccessListener { result ->
                val attendances = result.documents.mapNotNull { doc ->
                    doc.toObject(Attendance::class.java)?.apply {id = doc.id}
                }
                onResult(attendances)
                println("AttendanceRepo: $attendances")
                println("AttendanceRepo: $shiftId")
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
    }

    fun updateAttendance(attendanceId: String, attendance: Attendance) {
        val attendanceRef = db.collection("attendances").document(attendanceId).update(attendance.toMap())
    }
}