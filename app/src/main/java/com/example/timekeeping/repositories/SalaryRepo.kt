package com.example.timekeeping.repositories

import android.util.Log
import com.example.timekeeping.models.Adjustment
import com.example.timekeeping.models.Assignment
import com.example.timekeeping.models.Attendance
import com.example.timekeeping.models.Salary
import com.example.timekeeping.utils.convertToReference
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import org.checkerframework.checker.units.qual.A
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import javax.inject.Inject

class SalaryRepo @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    val localDateTimeMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-M"))

    fun createSalary(salary: Salary, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("salaries").add(salary)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getAdvanceMoney(
        groupId: String,
        employeeId: String,
        month: Int,
        year: Int,
        onSuccess: (List<Adjustment>) -> Unit,
        onFailure: (Exception) -> Unit = {}
    ) {
        firestore.collection("salaries")
            .document(salaryDocId(groupId, employeeId))
            .collection("adjustments-$year-$month")
            .get()
            .addOnSuccessListener { documents ->
                val result = documents
                    .mapNotNull { it.toObject(Adjustment::class.java) }
                    .filter { it.adjustmentType == "Ứng lương" }

                Log.d("SalaryRepo_getAdvanceMoney", "result: $result")

                onSuccess(result)
            }
            .addOnFailureListener { onFailure(it) }
    }


    fun getSalaryInfoByMonth(groupId: String, employeeId: String, month: Int, year: Int, onSuccess: (List<Adjustment>) -> Unit, onFailure: (Exception) -> Unit = {}){
        firestore.collection("salaries")
            .document(salaryDocId(groupId, employeeId))
            .collection("adjustments-$year-$month")
            .get()
            .addOnSuccessListener { documents ->
                val salaries = documents.toObjects(Adjustment::class.java)
                Log.d("SalaryRepo_getSalaryInfoByMonth", "salaries: $salaries")
                Log.d("SalaryRepo_getSalaryInfoByMonth", "groupId: $groupId, employeeId: $employeeId, month: $month, year: $year")
                onSuccess(salaries)
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun createAdjustSalary(
        groupId: String,
        employeeId: String,
        adjustment: Adjustment,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val docRef = firestore.collection("salaries").document(salaryDocId(groupId, employeeId))

        Log.d("SalaryRepo_createAdjustSalary", "groupId: $groupId, employeeId: $employeeId, adjustment: $adjustment")

        docRef.get()
            .addOnSuccessListener { document ->
                val currentSalary = document.toObject(Salary::class.java)
                if (currentSalary != null) {
                    docRef.collection("adjustments-$localDateTimeMonth")
                        .add(adjustment)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onFailure(it) }
                } else {
                    onFailure(Exception("Salary not found"))
                }
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun calculateTotalWage(groupId: String, employeeId: String, month: Int, year: Int, onResult: (Int) -> Unit) {

        var totalWage: Int
        var salaryType = ""
        var salaryAmount = 0

        firestore.collection("salaries").document(salaryDocId(groupId, employeeId)).get().addOnSuccessListener({
            documents ->
            val salary = documents.toObject(Salary::class.java)
                salaryType = salary?.salaryType ?: ""
                salaryAmount = salary?.salary ?: 0


            when(salaryType) {
                "Ca" -> {
                    firestore.collection("attendances")
                        .whereEqualTo("employeeId", employeeId.convertToReference("employees"))
//                    .whereGreaterThanOrEqualTo("startTime", Timestamp(Date(year, month, 1)))
//                    .whereLessThanOrEqualTo("endTime", Timestamp(Date(year, month + 1, 0)))
                        .get()
                        .addOnSuccessListener({
                            val assignments = it.toObjects(Attendance::class.java)
                            totalWage = (assignments.size * salaryAmount)
                            onResult(totalWage)

                            Log.d("SalaryRepo_calculateTotalWage", "assignments: $assignments, salaryAmount: $salaryAmount")
                            Log.d("SalaryRepo_calculateTotalWage", "totalWage: $totalWage")
                        })
                }
                "Tháng" -> {
                    firestore.collection("attendances")
                        .whereEqualTo("employeeId", employeeId.convertToReference("employees"))
//                    .whereGreaterThanOrEqualTo("startTime", Timestamp(Date(year, month, 1)))
//                    .whereLessThanOrEqualTo("endTime", Timestamp(Date(year, month + 1, 0)))
                        .get()
                        .addOnSuccessListener({ documents ->
                            val assignments = documents.toObjects(Assignment::class.java)
                            totalWage = (assignments.size * salaryAmount / 30)
                            onResult(totalWage)
                        })
                }
            }
        })
        }
}

fun salaryDocId(groupId: String, employeeId: String): String {
    return "${groupId}-${employeeId}"
}
