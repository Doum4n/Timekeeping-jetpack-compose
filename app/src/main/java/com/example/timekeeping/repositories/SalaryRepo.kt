package com.example.timekeeping.repositories

import android.util.Log
import com.example.timekeeping.models.Adjustment
import com.example.timekeeping.models.Assignment
import com.example.timekeeping.models.Attendance
import com.example.timekeeping.models.Salary
import com.example.timekeeping.ui.employees.form.TypeAllowance
import com.example.timekeeping.ui.employees.form.TypeDeduct
import com.example.timekeeping.ui.employees.form.TypeDeductItem
import com.example.timekeeping.utils.DateTimeMap
import com.example.timekeeping.utils.convertToReference
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import org.checkerframework.checker.units.qual.A
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
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
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val result = documents
                    .mapNotNull {
                        it.toObject(Adjustment::class.java).copy(id = it.id) // Gán id vào adjustment
                    }
                    .filter { it.adjustmentType == "Ứng lương" }

                Log.d("SalaryRepo_getAdvanceMoney", "result: $result")

                onSuccess(result)
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun getDeductMoney(groupId: String, employeeId: String, month: Int, year: Int, onSuccess: (List<Adjustment>) -> Unit, onFailure: (Exception) -> Unit = {}){
        firestore.collection("salaries")
            .document(salaryDocId(groupId, employeeId))
            .collection("adjustments-$year-$month")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener({
                val result = it.documents.mapNotNull { doc ->
                    val adjustment = doc.toObject(Adjustment::class.java)
                    adjustment?.let {
                        it.id = doc.id // gán document ID
                        it
                    }
                }.filter {
                    it.adjustmentType in TypeDeduct.entries.filter { it.label != "Ứng lương" }.map { it.label }
                }

                onSuccess(result)
            })
    }

    fun getBonusAdjustment(groupId: String, employeeId: String, month: Int, year: Int, onSuccess: (List<Adjustment>) -> Unit, onFailure: (Exception) -> Unit = {}){
        firestore.collection("salaries")
            .document(salaryDocId(groupId, employeeId))
            .collection("adjustments-$year-$month")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener({
                val result = it.documents.mapNotNull { doc ->
                    val adjustment = doc.toObject(Adjustment::class.java)
                    adjustment?.let {
                        it.id = doc.id // gán document ID
                        it
                    }
                }.filter {
                    it.adjustmentType in TypeAllowance.entries.map { it.label }
                }

                onSuccess(result)
            })
    }

    fun getSalaryInfoByMonth(groupId: String, employeeId: String, month: Int, year: Int, onSuccess: (List<Adjustment>) -> Unit, onFailure: (Exception) -> Unit = {}){
        firestore.collection("salaries")
            .document(salaryDocId(groupId, employeeId))
            .collection("adjustments-$year-$month")
            .get()
            .addOnSuccessListener { documents ->
                // !!!
                val salaries = documents.mapNotNull { doc ->
                    doc.toObject(Adjustment::class.java).copy(id = doc.id)
                }
                Log.d("SalaryRepo_getSalaryInfoByMonth", "salaries: $salaries")
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
                        .whereEqualTo("startTime.month", month)
                        .whereEqualTo("startTime.year", year)
                        //.whereLessThanOrEqualTo("endTime", endDate)
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
                        .whereEqualTo("startTime.month", month)
                        .whereEqualTo("startTime.year", year)
                        .get()
                        .addOnSuccessListener({
                            val assignments = it.toObjects(Assignment::class.java)
                            totalWage = (assignments.size * salaryAmount / 30)
                            onResult(totalWage)
                        })
                }
            }
        })
    }

    fun getTotalUnpaidSalary(groupId: String, onResult: (Int) -> Unit) {
        firestore.collection("salaries")
            .whereEqualTo("groupId", groupId)
            .get()
            .addOnSuccessListener { documents ->
                val totalResults = documents.size()
                if (totalResults == 0) {
                    onResult(0)
                    return@addOnSuccessListener
                }

                var completedCount = 0
                var totalUnpaidSalary = 0

                // Tạo một object để lưu trữ các giá trị tạm thời
                val results = mutableMapOf<String, IntArray>() // employeeId to [wage, bonus, deduction, advance]

                for (document in documents) {
                    val salary = document.toObject(Salary::class.java)
                    val employeeId = salary.employeeId
                    val month = LocalDateTime.now().monthValue
                    val year = LocalDateTime.now().year

                    // Khởi tạo entry cho employee
                    results[employeeId] = IntArray(4) // [wage, bonus, deduction, advance]

                    // Đếm số lượng callback đã hoàn thành
                    var callbacksCompleted = 0
                    val totalCallbacks = 3 // calculateTotalWage, getSalaryInfoByMonth, getAdvanceMoney

                    fun checkAllCallbacksDone() {
                        callbacksCompleted++
                        if (callbacksCompleted == totalCallbacks) {
                            completedCount++
                            // Khi tất cả callback cho employee này hoàn thành, cộng vào tổng
                            results[employeeId]?.let {
                                totalUnpaidSalary += it[0] + it[1] + it[2] + it[3]
                                Log.d("SalaryRepo_getTotalUnpaidSalary", "wage: ${it[0]}, bonus: ${it[1]}, deduction: ${it[2]}, advance: ${it[3]}, totalUnpaidSalary: $totalUnpaidSalary")

                            }

                            // Khi tất cả employee đã xử lý xong
                            if (completedCount == totalResults) {
                                Log.d("SalaryRepo_getTotalUnpaidSalary", "totalUnpaidSalary: $totalUnpaidSalary")
                                onResult(totalUnpaidSalary)
                            }
                        }
                    }

                    calculateTotalWage(groupId, employeeId, month, year) { wage ->
                        results[employeeId]?.set(0, wage)
                        checkAllCallbacksDone()
                    }

                    getSalaryInfoByMonth(
                        groupId, employeeId, month, year,
                        onSuccess = { adjustments ->
                            val bonus = adjustments.filter { it.adjustmentType in TypeAllowance.entries.map { it.label } }
                                .sumOf { it.adjustmentAmount }
                            val deduction = adjustments.filter { it.adjustmentType in TypeDeduct.entries.map { it.label } && it.adjustmentType != "Ứng lương" }
                                .sumOf { it.adjustmentAmount }

                            results[employeeId]?.let {
                                it[1] = bonus
                                it[2] = deduction
                            }
                            checkAllCallbacksDone()
                        }
                    )

                    getAdvanceMoney(
                        groupId, employeeId, month, year,
                        onSuccess = { advances ->
                            val advanceTotal = advances.sumOf { it.adjustmentAmount }
                            results[employeeId]?.set(3, advanceTotal)
                            checkAllCallbacksDone()
                        }
                    ) {
                        // Xử lý lỗi nếu cần
                        checkAllCallbacksDone()
                    }
                }
            }
            .addOnFailureListener {
                // Xử lý lỗi nếu cần
                onResult(0)
            }
    }

    fun getAdjustSalary(
        groupId: String,
        employeeId: String,
        adjustmentId: String,
        month: Int,
        year: Int,
        onSuccess: (Adjustment?) -> Unit, // nên cho nullable vì có thể không tìm thấy
    ) {
        firestore.collection("salaries")
            .document(salaryDocId(groupId, employeeId))
            .collection("adjustments-$year-$month")
            .document(adjustmentId)
            .get()
            .addOnSuccessListener { documents ->
                val adjustment = documents.toObject(Adjustment::class.java)
                onSuccess(adjustment)
            }
    }

    fun updateAdjustSalary(
        groupId: String,
        employeeId: String,
        adjustmentId: String,
        adjustments: Adjustment,
        onSuccess: () -> Boolean,
        onFailure: (Exception) -> Unit = {}
    ) {
        firestore.collection("salaries")
            .document(salaryDocId(groupId, employeeId))
            .collection("adjustments-${adjustments.createdAt.year}-${adjustments.createdAt.month}")
            .document(adjustmentId)
            .set(adjustments)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun deleteAdjustSalary(
        groupId: String,
        employeeId: String,
        adjustment: Adjustment,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection("salaries")
            .document(salaryDocId(groupId, employeeId))
            .collection("adjustments-$localDateTimeMonth")
            .document(adjustment.id)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}

fun salaryDocId(groupId: String, employeeId: String): String {
    return "${groupId}-${employeeId}"
}
