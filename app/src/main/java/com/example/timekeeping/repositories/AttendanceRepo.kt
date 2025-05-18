package com.example.timekeeping.repositories

import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import com.example.timekeeping.models.Attendance
import com.example.timekeeping.models.Employee
import com.example.timekeeping.models.Salary
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
    val db: FirebaseFirestore,
    val salaryRepo: SalaryRepo,
) {

    fun checkIn(attendance: Attendance, groupId: String, isUpdate: Boolean = false) {
        Log.d("AttendanceRepo_checkIn", attendance.employeeId.id)

        salaryRepo.getSalaryById(groupId, attendance.employeeId.id) { salary ->
            if (salary == null) {
                Log.e("AttendanceRepo", "Salary not found.")
                return@getSalaryById
            }

            Log.d("AttendanceRepo_checkIn", "Salary: $salary")

            db.collection("shifts").document(attendance.shiftId).get()
                .addOnSuccessListener { shiftSnapshot ->
                    val coefficient = shiftSnapshot.getDouble("coefficient") ?: 1.0
                    val allowance = shiftSnapshot.getLong("allowance")?.toInt() ?: 0

                    Log.d("AttendanceRepo_checkIn", "Coefficient: $coefficient, Allowance: $allowance")

                    db.collection("assignments")
                        .whereEqualTo("employeeId", attendance.employeeId)
                        .whereEqualTo("shiftId", attendance.shiftId.convertToReference("shifts"))
                        .whereEqualTo("month", attendance.startTime.month)
                        .whereEqualTo("year", attendance.startTime.year)
                        .get()
                        .addOnSuccessListener { assignmentSnapshot ->

                            Log.d("AttendanceRepo_checkIn", "it work")

                            val assignmentDoc = assignmentSnapshot.documents.firstOrNull()
                            val dates = assignmentDoc?.get("dates") as? List<*>
                            val assignmentDates = dates?.size ?: 0

                            var salaryAmount = 0
//                            if (attendance.attendanceType != "Nghỉ không lương") {
                                salaryAmount = when (salary.salaryType) {
                                    "Ca" -> salary.salary * coefficient.toInt() + allowance
                                    "Tháng" -> if (assignmentDates > 0) salary.salary / assignmentDates else 0
                                    else -> 0
                                }
//                            }

                            if (!isUpdate) {
                                // Thêm attendance trước
                                db.collection("attendances").add(attendance)
                                    .addOnSuccessListener { docRef ->
                                        val payrollQuery = db.collection("payrolls")
                                            .whereEqualTo("employeeId", attendance.employeeId)
                                            .whereEqualTo("groupId", groupId)
                                            .whereEqualTo("month", attendance.startTime.month)
                                            .whereEqualTo("year", attendance.startTime.year).get()
                                            .addOnSuccessListener { payrollSnapshot ->
                                                val payrollDoc = payrollSnapshot.documents.firstOrNull()
                                                if (payrollDoc != null) {
                                                    val payrollRef = payrollDoc.reference
                                                    db.runTransaction { transaction ->
                                                        val snapshot = transaction.get(payrollRef)
                                                        val oldWage = snapshot.getLong("totalWage")?.toInt() ?: 0
                                                        val newWage = oldWage + salaryAmount
                                                        Log.d("AttendanceRepo_checkIn", "Old wage: $oldWage, New wage: $newWage")
                                                        transaction.update(payrollRef, "totalWage", newWage)
                                                    }.addOnSuccessListener {
                                                        Log.d("AttendanceRepo_checkIn", "Payroll updated.")
                                                    }.addOnFailureListener {
                                                        Log.e("AttendanceRepo_checkIn", "Failed to update payroll", it)
                                                    }
                                                } else {
                                                    // Payroll chưa tồn tại ⇒ tạo mới
                                                    val newPayroll = hashMapOf(
                                                        "employeeId" to attendance.employeeId.id,
                                                        "groupId" to groupId,
                                                        "month" to attendance.startTime.month,
                                                        "year" to attendance.startTime.year,
                                                        "totalPayment" to 0.0,
                                                        "totalWage" to salaryAmount
                                                    )
                                                    db.collection("payrolls").add(newPayroll)
                                                }

                                                Log.d("AttendanceRepo_checkIn", "Attendance saved with ID: ${docRef.id}")
                                            }
                                    }
                                    .addOnFailureListener {
                                        Log.e("AttendanceRepo", "Failed to add attendance", it)
                                    }
                            } else {

                                update(attendance, groupId, salaryAmount = salaryAmount, salary = salary, coefficient = coefficient, allowance = allowance, assignmentDates = assignmentDates)

                            }
                        }
                }
        }
    }

    private fun update(
        attendance: Attendance,
        groupId: String,
        salaryAmount: Int,
        salary: Salary,
        coefficient: Double,
        allowance: Int,
        assignmentDates: Int
    ) {
        db.collection("attendances").document(attendance.id).update(attendance.toMap())
        // Lấy oldAttendance trước khi chạy transaction
        db.collection("attendances").document(attendance.id).get()
            .addOnSuccessListener { oldAttendanceSnapshot ->
                val oldAttendance =
                    oldAttendanceSnapshot.toObject(Attendance::class.java)
                if (oldAttendance != null) {
                    db.collection("payrolls")
                        .whereEqualTo("employeeId", attendance.employeeId.id)
                        .whereEqualTo("groupId", groupId)
                        .whereEqualTo("month", attendance.startTime.month)
                        .whereEqualTo("year", attendance.startTime.year)
                        .get()
                        .addOnSuccessListener { payrollSnapshot ->
                            val payrollDoc =
                                payrollSnapshot.documents.firstOrNull()
                            if (payrollDoc != null) {
                                val payrollRef = payrollDoc.reference

                                db.runTransaction { transaction ->
                                    val snapshot = transaction.get(payrollRef)
                                    val oldWage = snapshot.getLong("totalWage")?.toInt() ?: 0

                                    val newSalaryAmount = if (attendance.attendanceType == "Chấm 1/2 công") salaryAmount/2
                                    else if (attendance.attendanceType == "Đi làm" || attendance.attendanceType == "Nghỉ có lương") salaryAmount
                                    else -salaryAmount

                                    val newWage = oldWage + newSalaryAmount

                                    Log.d("AttendanceRepo_checkIn", "Old wage: $oldWage, New salary amount: $newSalaryAmount, New wage: $newWage")

                                    transaction.update(payrollRef, "totalWage", newWage)
                                }.addOnSuccessListener {
                                    Log.d(
                                        "AttendanceRepo_checkIn",
                                        "Payroll updated."
                                    )
                                }.addOnFailureListener {
                                    Log.e(
                                        "AttendanceRepo_checkIn",
                                        "Failed to update payroll",
                                        it
                                    )
                                }
                            } else {
                                // Payroll chưa tồn tại ⇒ tạo mới
                                val newPayroll = hashMapOf(
                                    "employeeId" to attendance.employeeId.id,
                                    "groupId" to groupId,
                                    "month" to attendance.startTime.month,
                                    "year" to attendance.startTime.year,
                                    "totalPayment" to 0.0,
                                    "totalWage" to salaryAmount
                                )
                                db.collection("payrolls")
                                    .add(newPayroll)
                            }
                        }
                } else {
                    Log.e(
                        "AttendanceRepo_checkIn",
                        "Old attendance not found"
                    )
                }
            }
            .addOnFailureListener {
                Log.e(
                    "AttendanceRepo_checkIn",
                    "Failed to get old attendance",
                    it
                )
            }
    }

    fun getAttendanceByEmployeeId(groupId: String, employeeId: String, month: Int, year: Int, onResult: (List<Attendance>) -> Unit) {
        db.collection("attendances")
            .whereEqualTo("groupId", groupId)
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

    fun getAttendanceByEmployeeIdAndDate(groupId: String, employeeId: String, date: LocalDate, onResult: (List<Attendance>) -> Unit) {
        db.collection("attendances")
            .whereEqualTo("groupId", groupId)
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
}