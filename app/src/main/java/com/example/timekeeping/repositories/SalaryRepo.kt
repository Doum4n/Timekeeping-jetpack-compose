package com.example.timekeeping.repositories

import android.util.Log
import com.example.timekeeping.models.Adjustment
import com.example.timekeeping.models.Assignment
import com.example.timekeeping.models.Attendance
import com.example.timekeeping.models.Payment
import com.example.timekeeping.models.Salary
import com.example.timekeeping.models.Shift
import com.example.timekeeping.models.applyWageRules
import com.example.timekeeping.ui.admin.employees.form.TypeAllowance
import com.example.timekeeping.ui.admin.employees.form.TypeDeduct
import com.example.timekeeping.ui.admin.rule.SalaryFieldName
import com.example.timekeeping.utils.RuleEvaluator
import com.example.timekeeping.utils.convertToReference
import com.example.timekeeping.utils.toPositive
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
        firestore.collection("adjustments")
            .whereEqualTo("groupId", groupId)
            .whereEqualTo("employeeId", employeeId)
            .whereEqualTo("createdAt.month", month)
            .whereEqualTo("createdAt.year", year)
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
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun getDeductMoney(groupId: String, employeeId: String, month: Int, year: Int, onSuccess: (List<Adjustment>) -> Unit, onFailure: (Exception) -> Unit = {}){
        firestore.collection("adjustments")
            .whereEqualTo("groupId", groupId)
            .whereEqualTo("employeeId", employeeId)
            .whereEqualTo("createdAt.month", month)
            .whereEqualTo("createdAt.year", year)
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
        firestore.collection("adjustments")
            .whereEqualTo("groupId", groupId)
            .whereEqualTo("employeeId", employeeId)
            .whereEqualTo("createdAt.month", month)
            .whereEqualTo("createdAt.year", year)
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
        firestore.collection("adjustments")
            .whereEqualTo("groupId", groupId)
            .whereEqualTo("employeeId", employeeId)
            .whereEqualTo("createdAt.month", month)
            .whereEqualTo("createdAt.year", year)
            .get()
            .addOnSuccessListener { documents ->
                val salaries = documents.mapNotNull { doc ->
                    doc.toObject(Adjustment::class.java).copy(id = doc.id)
                }
                Log.d("SalaryRepo_getSalaryInfoByMonth", "salaries: $salaries")
                onSuccess(salaries)
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun createAdjustSalary(
        adjustment: Adjustment,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val payrollRef = firestore.collection("payrolls")
            .whereEqualTo("groupId", adjustment.groupId)
            .whereEqualTo("employeeId", adjustment.employeeId)
            .whereEqualTo("month", adjustment.createdAt.month)
            .whereEqualTo("year", adjustment.createdAt.year)

        firestore.collection("adjustments")
            .add(adjustment)
            .addOnSuccessListener {
                payrollRef.get()
                    .addOnSuccessListener { querySnapshot ->
                        val document = querySnapshot.documents.firstOrNull()
                        if (document != null) {
                            val payrollRef = document.reference

                            firestore.runTransaction { transaction ->
                                val snapshot = transaction.get(payrollRef)
                                val oldPayment = snapshot.getDouble("totalWage") ?: 0.0
                                val newPayment = if (adjustment.adjustmentType in TypeAllowance.entries.map { it.label }) oldPayment + adjustment.adjustmentAmount
                                else oldPayment - adjustment.adjustmentAmount.toPositive()

                                transaction.update(payrollRef, "totalWage", newPayment)
                            }.addOnSuccessListener { onSuccess() }
                                .addOnFailureListener { onFailure(it) }
                        } else {
                            onFailure(Exception("Payroll document not found"))
                        }
                    }
                    .addOnFailureListener { onFailure(it) }
            }
            .addOnFailureListener { onFailure(it) }
    }


    @OptIn(DelicateCoroutinesApi::class)
    fun calculateAllTotalWage(groupId: String, employeeId: String, onResult: (Int) -> Unit) {
        var totalWage: Int = 0
        var salaryType = ""
        var salaryAmount = 0

        val AttendanceType = listOf("Đi làm", "Chấm 1/2 công", "Nghỉ có lương")

        firestore.collection("salaries").document(salaryDocId(groupId, employeeId)).get().addOnSuccessListener({
                documents ->
            val salary = documents.toObject(Salary::class.java)
            salaryType = salary?.salaryType ?: ""
            salaryAmount = salary?.salary ?: 0

            when(salaryType) {
//                "Giờ" -> {
//                    firestore.collection("attendances")
//                        .whereEqualTo("employeeId", employeeId.convertToReference("employees"))
//                        .whereEqualTo("startTime.month", month)
//                        .whereEqualTo("startTime.year", year)
//                        .get()
//                        .addOnSuccessListener({
//                            val assignments = it.toObjects(Attendance::class.java)
//                            totalWage = (assignments.sumOf { it.totalHours } * salaryAmount)
//                            onResult(totalWage)
//                        })
//                }
                "Ca" -> {
                    val attendancesRef = firestore.collection("attendances")
                    attendancesRef
                        .whereEqualTo("employeeId", employeeId.convertToReference("employees"))
                        .get()
                        .addOnSuccessListener { snapshot ->
                            val assignments = snapshot.toObjects(Attendance::class.java).filter { it.attendanceType in AttendanceType }

                            if (assignments.isEmpty()) {
                                onResult(0)
                                return@addOnSuccessListener
                            }

                            var fetched = 0

                            for (attendance in assignments) {
                                val shiftId = attendance.shiftId
                                firestore.collection("shifts")
                                    .document(shiftId)
                                    .get()
                                    .addOnSuccessListener { shiftSnap ->
                                        val shift = shiftSnap.toObject(Shift::class.java)
                                        val coefficient = shift?.coefficient ?: 1.0
                                        val allowance = shift?.allowance ?: 0
                                        totalWage += (salaryAmount * coefficient + allowance).toInt()

                                        fetched++
                                        if (fetched == assignments.size) {

                                            GlobalScope.launch {
                                                val comparisonMap = mapOf(
                                                    SalaryFieldName.NUMBER_OF_DAYS.label to assignments.size
                                                )

                                                try {
                                                    val finalWage = applyWageRules(groupId, comparisonMap, totalWage)
                                                    onResult(finalWage) // Trả kết quả sau khi có finalWage
                                                } catch (e: Exception) {
                                                    onResult(totalWage) // Fallback nếu có lỗi
                                                }
                                            }

                                            Log.d("SalaryRepo", "Final totalWage: $totalWage")
                                        }
                                    }
                                    .addOnFailureListener {
                                    }
                            }
                        }
                }
                "Tháng" -> {
                    firestore.collection("attendances")
                        .whereEqualTo("employeeId", employeeId.convertToReference("employees"))
                        .get()
                        .addOnSuccessListener({
                            val assignments = it.toObjects(Assignment::class.java)
                            totalWage = (assignments.size * salaryAmount / 30)

                            /* ============================================================================
                             * Apply Rule
                             * ============================================================================
                             */
                            GlobalScope.launch {
                                val comparisonMap = mapOf(
                                    SalaryFieldName.NUMBER_OF_DAYS.label to assignments.size
                                )

                                try {
                                    val finalWage = applyWageRules(groupId, comparisonMap, totalWage)
                                    onResult(finalWage) // Trả kết quả sau khi có finalWage
                                } catch (e: Exception) {
                                    onResult(totalWage) // Fallback nếu có lỗi
                                }
                            }
                        })
                }
            }
        })
    }

    fun calculateTotalWage(groupId: String, employeeId: String, month: Int, year: Int, onResult: (Int) -> Unit) {

        var totalWage: Int = 0
        var salaryType = ""
        var salaryAmount = 0

        val AttendanceType = listOf("Đi làm", "Chấm 1/2 công", "Nghỉ có lương")

        firestore.collection("salaries").document(salaryDocId(groupId, employeeId)).get().addOnSuccessListener({
            documents ->
            val salary = documents.toObject(Salary::class.java)
                salaryType = salary?.salaryType ?: ""
                salaryAmount = salary?.salary ?: 0

            when(salaryType) {
//                "Giờ" -> {
//                    firestore.collection("attendances")
//                        .whereEqualTo("employeeId", employeeId.convertToReference("employees"))
//                        .whereEqualTo("startTime.month", month)
//                        .whereEqualTo("startTime.year", year)
//                        .get()
//                        .addOnSuccessListener({
//                            val assignments = it.toObjects(Attendance::class.java)
//                            totalWage = (assignments.sumOf { it.totalHours } * salaryAmount)
//                            onResult(totalWage)
//                        })
//                }
                "Ca" -> {
                    val attendancesRef = firestore.collection("attendances")
                    attendancesRef.whereEqualTo("groupId", groupId)
                        .whereEqualTo("employeeId", employeeId.convertToReference("employees"))
                        .whereEqualTo("startTime.month", month)
                        .whereEqualTo("startTime.year", year)
                        .get()
                        .addOnSuccessListener { snapshot ->
                            val attendances = snapshot.toObjects(Attendance::class.java).filter { it.attendanceType in AttendanceType }

                            if (attendances.isEmpty()) {
                                onResult(0)
                                return@addOnSuccessListener
                            }

                            var fetched = 0

                            for (attendance in attendances) {
                                val shiftId = attendance.shiftId
                                firestore.collection("shifts")
                                    .document(shiftId)
                                    .get()
                                    .addOnSuccessListener { shiftSnap ->
                                        val shift = shiftSnap.toObject(Shift::class.java)
                                        val coefficient = shift?.coefficient ?: 1.0
                                        val allowance = shift?.allowance ?: 0
                                        totalWage += (salaryAmount * coefficient + allowance).toInt()

                                        fetched++
                                        if (fetched == attendances.size) {

                                            /* ============================================================================
                                             * Apply Rule
                                             * ============================================================================
                                             */

                                            GlobalScope.launch {
                                                val comparisonMap = mapOf(
                                                    SalaryFieldName.NUMBER_OF_DAYS.label to attendances.size
                                                )
                                                try {
                                                    val finalWage = applyWageRules(groupId, comparisonMap, totalWage)
                                                    onResult(finalWage) // Trả kết quả sau khi có finalWage
//                                                    firestore.collection("payrolls").document(payrollDocId(groupId, employeeId, month, year)).update("totalWage", finalWage)
                                                } catch (e: Exception) {
                                                    onResult(totalWage) // Fallback nếu có lỗi
                                                }
                                            }

                                            Log.d("SalaryRepo", "Final totalWage: $totalWage")
                                        }
                                    }
                                    .addOnFailureListener {
                                    }
                            }
                        }
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

    fun getTotalUnpaidSalary(
        groupId: String,
        month: Int,
        year: Int,
        isAllTime: Boolean,
        onResult: (Int) -> Unit
    ) {
        if (!isAllTime) {
            // ✅ Dùng logic hiện tại nếu là theo tháng
            getTotalUnpaidSalaryByMonth(groupId, month, year, onResult)
            return
        }

        firestore.collection("payrolls")
            .whereEqualTo("groupId", groupId)
            .get()
            .addOnSuccessListener { documents ->
                val totalPayment = documents.sumOf { it.getLong("totalPayment")?.toInt() ?: 0 }
                val totalWage = documents.sumOf { it.getLong("totalWage")?.toInt() ?: 0 }
                val totalUnpaidSalary = totalWage - totalPayment

                firestore.collection("attendances")
                    .whereEqualTo("groupId", groupId)
                    .whereEqualTo("startTime.month", month)
                    .whereEqualTo("startTime.year", year)
                    .get()
                    .addOnSuccessListener { attendances ->
                        val attendanceList = attendances.toObjects(Attendance::class.java)
                        val workDays = attendanceList.count { it.attendanceType == "Đi làm" }


                    }

                onResult(totalUnpaidSalary)
                Log.d("SalaryRepo_getTotalUnpaidSalary", "totalUnpaidSalary: $totalUnpaidSalary")
            }
            .addOnFailureListener {
                onResult(0)
            }
    }

        // ✅ Tính tổng toàn bộ thời gian bằng cách gom tất cả các khoản
//        var totalWage = 0
//        var totalBonus = 0
//        var totalAdvance = 0
//        var totalDeduct = 0
//        var totalPayment = 0
//        var doneCount = 0
//
//        fun checkDone() {
//            doneCount++
//            if (doneCount == 5) {
//                val totalUnpaid = totalWage + totalBonus + totalDeduct + totalAdvance - totalPayment
//                Log.d("SalaryRepo_getTotalUnpaidSalary", "totalWage: $totalWage, totalBonus: $totalBonus, totalDeduct: $totalDeduct, totalAdvance: $totalAdvance, totalPayment: $totalPayment, totalUnpaid: $totalUnpaid")
//                onResult(totalUnpaid)
////                firestore.collection("payrolls").document(payrollDocId(groupId, "", month, year)).update("totalPayment", totalUnpaid)
//            }
//        }
//
//        getAllSalaries(groupId,
//            onSuccess = {
//                totalWage = it
//                checkDone()
//            },
//            onFailure = {
//                Log.e("SalaryRepo_getTotalUnpaidSalary", "Error getting salaries", it)
//            }
//        )
//
//        getAllBonusAdjustment(groupId,
//            onSuccess = { bonusAdjustments ->
//                totalBonus = bonusAdjustments.sumOf { it.adjustmentAmount }
//                checkDone()
//            },
//            onFailure = {
//                Log.e("SalaryRepo_getTotalUnpaidSalary", "Error getting bonus adjustments", it)
//            }
//        )
//
//        getAllDeductMoney(groupId,
//            onSuccess = { deductAdjustments ->
//                totalDeduct = deductAdjustments.sumOf { it.adjustmentAmount }
//                checkDone()
//            },
//            onFailure = {
//                Log.e("SalaryRepo_getTotalUnpaidSalary", "Error getting deduct money", it)
//            }
//        )
//
//        getAllAdvanceMoney(groupId,
//            onSuccess = { advanceAdjustments ->
//                totalAdvance = advanceAdjustments.sumOf { it.adjustmentAmount }
//                checkDone()
//            },
//            onFailure = {
//                Log.e("SalaryRepo_getTotalUnpaidSalary", "Error getting advance money", it)
//            }
//        )
//
//        // 🔥 Truy vấn tất cả payments theo groupId (nếu bạn lưu `groupId` trong mỗi `payments` document)
//        firestore.collectionGroup("payments")
//            .whereEqualTo("groupId", groupId)
//            .get()
//            .addOnSuccessListener { payments ->
//                totalPayment = payments.sumOf { it.toObject(Payment::class.java).amount }
//                checkDone()
//            }
//            .addOnFailureListener {
//                Log.e("SalaryRepo_getTotalUnpaidSalary", "Error getting payments", it)
//            }
//    }


    fun getTotalUnpaidSalaryByMonth(
        groupId: String,
        month: Int,
        year: Int,
        onResult: (Int) -> Unit,
    ) {
        firestore.collection("payrolls")
            .whereEqualTo("groupId", groupId)
            .whereEqualTo("month", month)
            .whereEqualTo("year", year)
            .get()
            .addOnSuccessListener { documents ->
                val totalPayment = documents.sumOf { it.getLong("totalPayment")?.toInt() ?: 0 }
                val totalWage = documents.sumOf { it.getLong("totalWage")?.toInt() ?: 0 }
                val totalUnpaidSalary = totalWage - totalPayment
                onResult(totalUnpaidSalary)
            }
            .addOnFailureListener {
                onResult(0)
            }
    }

//        var totalWage = 0
//        var totalBonus = 0
//        var totalAdvance = 0
//        var totalDeduct = 0
//
//        firestore.collection("salaries")
//            .whereEqualTo("groupId", groupId)
//            .get()
//            .addOnSuccessListener { documents ->
//                val totalResults = documents.size()
//                if (totalResults == 0) {
//                    onResult(0)
//                    return@addOnSuccessListener
//                }
//
//                var completedCount = 0
//                var totalUnpaidSalary = 0
//
//                // Tạo một object để lưu trữ các giá trị tạm thời
//                val results = mutableMapOf<String, IntArray>() // employeeId to [wage, bonus, deduction, advance]
//
//                for (document in documents) {
//                    val salary = document.toObject(Salary::class.java)
//                    val employeeId = salary.employeeId
//
//                    // Khởi tạo entry cho employee
//                    results[employeeId] = IntArray(5) // [wage, bonus, deduction, advance, payment]
//
//                    // Đếm số lượng callback đã hoàn thành
//                    var callbacksCompleted = 0
//                    val totalCallbacks = 4 // calculateTotalWage, getSalaryInfoByMonth, getAdvanceMoney
//
//                    fun checkAllCallbacksDone() {
//                        callbacksCompleted++
//                        if (callbacksCompleted == totalCallbacks) {
//                            completedCount++
//                            // Khi tất cả callback cho employee này hoàn thành, cộng vào tổng
//                            results[employeeId]?.let {
//                                totalUnpaidSalary += it[0] + it[1] + it[2] + it[3] - it[4] // Dấu "+" là vì các loại trừ tiền trong csdl là "_"
//                                Log.d("SalaryRepo_getTotalUnpaidSalary", "wage: ${it[0]}, bonus: ${it[1]}, deduction: ${it[2]}, advance: ${it[3]}, payment: ${it[4]}, totalUnpaidSalary: $totalUnpaidSalary")
//                            }
//
//                            // Khi tất cả employee đã xử lý xong
//                            if (completedCount == totalResults) {
//                                Log.d("SalaryRepo_getTotalUnpaidSalary", "totalUnpaidSalary: $totalUnpaidSalary")
//                                onResult(totalUnpaidSalary)
//                            }
//                        }
//                    }
//
//                    firestore.collection("salaries")
//                        .document(salaryDocId(groupId, employeeId))
//                        .collection("payments") // theo tháng như cũ
//                        .whereEqualTo("createdAt.month", month)
//                        .whereEqualTo("createdAt.year", year)
//                        .get()
//                        .addOnSuccessListener { payments ->
//                            val totalPayment = payments.sumOf { it.toObject(Payment::class.java).amount }
//                            results[employeeId]?.set(4, totalPayment)
//                            checkAllCallbacksDone()
//
//                            Log.d("SalaryRepo_getTotalUnpaidSalary", "totalPayment: $totalPayment")
//                        }
//
//                    calculateTotalWage(groupId, employeeId, month, year) { wage ->
//                        results[employeeId]?.set(0, wage)
//                        totalWage += wage
//                        checkAllCallbacksDone()
//                    }
//
//                    getSalaryInfoByMonth(
//                        groupId, employeeId, month, year,
//                        onSuccess = { adjustments ->
//
//                            val bonus = adjustments.filter { it.adjustmentType in TypeAllowance.entries.map { it.label } }
//                                .sumOf { it.adjustmentAmount }
//                            totalBonus += bonus
//
//                            val deduction = adjustments.filter { it.adjustmentType in TypeDeduct.entries.map { it.label } && it.adjustmentType != "Ứng lương" }
//                                .sumOf { it.adjustmentAmount }
//                            totalDeduct += deduction
//
//                            results[employeeId]?.let {
//                                it[1] = bonus
//                                it[2] = deduction
//                            }
//                            checkAllCallbacksDone()
//                        }
//                    )
//
//                    getAdvanceMoney(
//                        groupId, employeeId, month, year,
//                        onSuccess = { advances ->
//                            val advanceTotal = advances.sumOf { it.adjustmentAmount }
//                            results[employeeId]?.set(3, advanceTotal)
//                            totalAdvance += advanceTotal
//                            checkAllCallbacksDone()
//                        }
//                    ) {
//                        // Xử lý lỗi nếu cần
//                        checkAllCallbacksDone()
//                    }
//                }
//            }
//            .addOnFailureListener {
//                // Xử lý lỗi nếu cần
//                onResult(0)
//            }
//    }

    fun getAllDeductMoney(groupId: String, onSuccess: (List<Adjustment>) -> Unit, onFailure: (Exception) -> Unit = {}) {
        firestore.collection("adjustments")
            .whereEqualTo("groupId", groupId) // Trường này phải tồn tại trong documents trong `adjustments`
            .whereIn("adjustmentType", TypeDeduct.entries.filter { it.label != "Ứng lương" }.map { it.label })
            .get()
            .addOnSuccessListener { snapshot ->
                val adjustments = snapshot.toObjects(Adjustment::class.java)
                Log.d("SalaryRepo_getAllDeductMoney", "adjustments: $adjustments")
                onSuccess(adjustments)
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun getAllBonusAdjustment(groupId: String, onSuccess: (List<Adjustment>) -> Unit, onFailure: (Exception) -> Unit = {}) {
        firestore.collection("adjustments")
            .whereIn("adjustmentType", TypeAllowance.entries.map { it.label })
            .whereEqualTo("groupId", groupId) // Trường này phải tồn tại trong documents trong `adjustments`
            .get()
            .addOnSuccessListener { snapshot ->
                val adjustments = snapshot.toObjects(Adjustment::class.java)
                Log.d("SalaryRepo_getAllBonusAdjustment", "adjustments: $adjustments")
                onSuccess(adjustments)
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun getAllAdvanceMoney(groupId: String, onSuccess: (List<Adjustment>) -> Unit, onFailure: (Exception) -> Unit = {}) {
        firestore.collection("adjustments")  // Truy vấn qua tất cả các subcollection adjustments
            .whereEqualTo("adjustmentType", "Ứng lương")  // Lọc các điều chỉnh có loại "Ứng lương"
            .whereEqualTo("groupId", groupId)  // Lọc theo groupId đã lưu trong adjustments
            .get()
            .addOnSuccessListener { snapshot ->
                val adjustments = snapshot.toObjects(Adjustment::class.java)  // Chuyển đổi thành danh sách Adjustment
                Log.d("SalaryRepo_getAllAdvanceMoney", "adjustments: $adjustments")
                onSuccess(adjustments)  // Gọi onSuccess với kết quả
            }
            .addOnFailureListener { exception ->
                onFailure(exception)  // Gọi onFailure nếu có lỗi
            }
    }

    fun getAllSalaries(groupId: String, onSuccess: (Int) -> Unit, onFailure: (Exception) -> Unit) {
        var totalWage = 0
        val tasks = mutableListOf<Task<Void>>()

        firestore.collection("salaries")
            .whereEqualTo("groupId", groupId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val salary = document.toObject(Salary::class.java)
                    val employeeId = salary.employeeId

                    // Create a TaskCompletionSource for each calculation
                    val taskSource = TaskCompletionSource<Void>()

                    calculateAllTotalWage(groupId, employeeId,
                    ) { wage ->
                        totalWage += wage
                        Log.d("SalaryRepo_getAllSalaries", "totalWage: $totalWage")
                        taskSource.setResult(null) // Mark task as complete
                    }

                    tasks.add(taskSource.task)
                }

                Tasks.whenAllComplete(tasks).addOnCompleteListener {
                    onSuccess(totalWage)
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }


    fun getAdjustSalary(
        adjustmentId: String,
        onSuccess: (Adjustment?) -> Unit, // nên cho nullable vì có thể không tìm thấy
    ) {
        firestore.collection("adjustments")
            .document(adjustmentId)
            .get()
            .addOnSuccessListener { documents ->
                val adjustment = documents.toObject(Adjustment::class.java)
                onSuccess(adjustment)
            }
    }

    fun updateAdjustSalary(
        adjustmentId: String,
        adjustments: Adjustment,
        onSuccess: () -> Boolean,
        onFailure: (Exception) -> Unit = {}
    ) {
        val payrollQuery = firestore.collection("payrolls")
            .whereEqualTo("groupId", adjustments.groupId)
            .whereEqualTo("employeeId", adjustments.employeeId)
            .whereEqualTo("month", adjustments.createdAt.month)
            .whereEqualTo("year", adjustments.createdAt.year)

        val adjustmentRef = firestore.collection("adjustments").document(adjustmentId)

        adjustmentRef.get().addOnSuccessListener { adjustmentSnapshot ->
            val oldAdjustment = adjustmentSnapshot.toObject(Adjustment::class.java)

            if (oldAdjustment == null) {
                onFailure(Exception("Old adjustment not found"))
                return@addOnSuccessListener
            }

            adjustmentRef.set(adjustments).addOnSuccessListener {
                payrollQuery.get().addOnSuccessListener { querySnapshot ->
                    val document = querySnapshot.documents.firstOrNull()
                    if (document != null) {
                        val payrollRef = document.reference

                        firestore.runTransaction { transaction ->
                            val snapshot = transaction.get(payrollRef)
                            val oldWage = snapshot.getDouble("totalWage") ?: 0.0

                            // Tính diff giữa điều chỉnh cũ và mới
                            val oldAmount = oldAdjustment.adjustmentAmount.toPositive()
                            val newAmount = adjustments.adjustmentAmount.toPositive()

                            val diff = if (adjustments.adjustmentType in TypeAllowance.entries.map { it.label }) {
                                newAmount - oldAmount
                            } else {
                                -(newAmount - oldAmount)
                            }

                            val newWage = oldWage + diff

                            transaction.update(payrollRef, "totalWage", newWage)
                            Log.d("SalaryRepo_updateAdjustSalary", "oldWage: $oldWage, diff: $diff, newWage: $newWage")
                        }.addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { onFailure(it) }
                    } else {
                        onFailure(Exception("Payroll document not found"))
                    }
                }.addOnFailureListener { onFailure(it) }
            }.addOnFailureListener { onFailure(it) }
        }.addOnFailureListener { onFailure(it) }
    }

    fun deleteAdjustSalary(
        adjustment: Adjustment,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val payrollQuery = firestore.collection("payrolls")
            .whereEqualTo("groupId", adjustment.groupId)
            .whereEqualTo("employeeId", adjustment.employeeId)
            .whereEqualTo("month", adjustment.createdAt.month)
            .whereEqualTo("year", adjustment.createdAt.year)

        firestore.collection("adjustments")
            .document(adjustment.id)
            .delete()
            .addOnSuccessListener {

                payrollQuery.get().addOnSuccessListener { querySnapshot ->
                    val document = querySnapshot.documents.firstOrNull()
                    if (document != null) {
                        val payrollRef = document.reference

                        firestore.runTransaction { transaction ->

                            val snapshot = transaction.get(payrollRef)
                            val oldWage = snapshot.getDouble("totalWage") ?: 0.0
                            val newWage = oldWage - adjustment.adjustmentAmount.toPositive()

                            transaction.update(payrollRef, "totalWage", newWage)
                        }.addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { onFailure(it) }
                    } else {
                        onFailure(Exception("Payroll document not found"))
                    }

                    onSuccess()
                }
                    .addOnFailureListener { onFailure(it) }
            }
    }

    fun getSalaryById(groupId: String, employeeId: String, onSuccess: (Salary?) -> Unit) {
        firestore.collection("salaries")
            .document(salaryDocId(groupId, employeeId))
            .get()
            .addOnSuccessListener { documents ->
                val salary = documents.toObject(Salary::class.java)
                onSuccess(salary)
            }
    }

    fun getTotalSalary(groupId: String, month: Int, year: Int, onResult: (Int) -> Unit) {
        firestore.collection("salaries")
            .whereEqualTo("groupId", groupId)
            .get()
            .addOnSuccessListener { documents ->
                val salaries = documents.toObjects(Salary::class.java)
                var totalSalary = 0
                var finishedCount = 0

                if (salaries.isEmpty()) {
                    onResult(0)
                    return@addOnSuccessListener
                }

                salaries.forEach { salary ->
                    calculateTotalWage(groupId, salary.employeeId, month, year) { wage ->
                        totalSalary += wage
                        finishedCount++

                        if (finishedCount == salaries.size) {
                            Log.d("SalaryRepo_getTotalSalary", "totalSalary: $totalSalary")
                            onResult(totalSalary)
                        }
                    }
                }
            }
    }

    fun getTotalAdvance(groupId: String, month: Int, year: Int, onResult: (Int) -> Unit) {
        firestore.collection("salaries")
            .whereEqualTo("groupId", groupId)
            .get()
            .addOnSuccessListener { documents ->
                val salaries = documents.toObjects(Salary::class.java)
                var totalAdvance = 0
                var finishedCount = 0
                if (salaries.isEmpty()) {
                    onResult(0)
                    return@addOnSuccessListener
                }
                salaries.forEach { salary ->
                    getAdvanceMoney(groupId, salary.employeeId, month, year,
                        onSuccess = { advances ->
                            totalAdvance += advances.sumOf { it.adjustmentAmount }
                            finishedCount++
                            if (finishedCount == salaries.size) {
                                onResult(totalAdvance)
                            }
                        }
                    ) {
                        finishedCount++
                        if (finishedCount == salaries.size) {
                            onResult(totalAdvance.toPositive())
                        }
                        Log.d("SalaryRepo_getTotalAdvance", "error: $it")
                    }
                }
            }
    }

    fun getTotalBonus(groupId: String, month: Int, year: Int, onResult: (Int) -> Unit) {
        firestore.collection("salaries")
            .whereEqualTo("groupId", groupId)
            .get()
            .addOnSuccessListener { documents ->
                val salaries = documents.toObjects(Salary::class.java)
                var totalBonus = 0
                var finishedCount = 0
                if (salaries.isEmpty()) {
                    onResult(0)
                    return@addOnSuccessListener
                }
                salaries.forEach { salary ->
                    getBonusAdjustment(groupId, salary.employeeId, month, year,
                        onSuccess = { bonuses ->
                            totalBonus += bonuses.filter { it.adjustmentType in TypeAllowance.entries.map { it.label }.filter { it != "Ứng lương" } }.sumOf { it.adjustmentAmount }
                            finishedCount++
                            if (finishedCount == salaries.size) {
                                onResult(totalBonus)
                            }
                            Log.d("SalaryRepo_getTotalBonus", "totalBonus: $totalBonus")
                        }
                    ) {
                        finishedCount++
                        if (finishedCount == salaries.size) {
                            onResult(totalBonus.toPositive())
                        }
                        Log.d("SalaryRepo_getTotalBonus", "error: $it")
                    }
                }
                Log.d("SalaryRepo_getTotalBonus", "totalBonus: $totalBonus")
            }
    }

    fun getTotalWorkDay(
        groupId: String,
        month: Int,
        year: Int,
        onResult: (workDays: Int, paidLeaveDays: Int) -> Unit
    ) {
        firestore.collection("shifts")
            .whereEqualTo("groupId", groupId)
            .get()
            .addOnSuccessListener { shiftSnapshot ->

                val shifts = shiftSnapshot.documents.mapNotNull { doc ->
                    val shift = doc.toObject(Shift::class.java)
                    shift?.id = doc.id
                    shift
                }

                val shiftsId = shifts.map { it.id }
                Log.d("SalaryRepo_getTotalWorkDay", "shiftsId: $shiftsId")

                if (shiftsId.isEmpty()) {
                    onResult(0, 0)
                    Log.d("SalaryRepo_getTotalWorkDay", "shiftsId is empty")
                    return@addOnSuccessListener
                }

                firestore.collection("attendances")
                    .whereIn("shiftId", shiftsId)
                    .get()
                    .addOnSuccessListener { attendanceSnapshot ->
                        val attendances = attendanceSnapshot.toObjects(Attendance::class.java)
                            .filter {
                                it.startTime.month == month &&
                                        it.startTime.year == year
                            }

                        val workDays = attendances.count {
                            it.attendanceType == "Đi làm" || it.attendanceType == "Chấm 1/2 công"
                        }

                        val paidLeaveDays = attendances.count {
                            it.attendanceType == "Nghỉ có lương"
                        }

                        Log.d("SalaryRepo_getTotalWorkDay", "workDays: $workDays, paidLeaveDays: $paidLeaveDays")
                        onResult(workDays, paidLeaveDays)
                    }
                    .addOnFailureListener {
                        Log.d("SalaryRepo_getTotalWorkDay", "attendance error: $it")
                        onResult(0, 0)
                    }
            }
            .addOnFailureListener {
                Log.d("SalaryRepo_getTotalWorkDay", "shift error: $it")
                onResult(0, 0)
            }
    }

    /* ============================================================================
     * Apply Rule
     * ============================================================================
     */

    fun getSalaryInfo(
        groupId: String,
        onResult: (List<Adjustment>) -> Unit
    ) {
        firestore.collection("adjustments")
            .whereEqualTo("groupId", groupId)
            .get()
            .addOnSuccessListener { snapshot ->
                val adjustments = snapshot.toObjects(Adjustment::class.java)
                onResult(adjustments)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

}

fun salaryDocId(groupId: String, employeeId: String): String {
    return "${groupId}-${employeeId}"
}

fun applyRule(originalValue: Int): Map<String, Int> {
    return mapOf(
        SalaryFieldName.NUMBER_OF_DAYS.label to originalValue
    )
}