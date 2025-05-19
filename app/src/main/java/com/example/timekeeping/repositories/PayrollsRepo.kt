package com.example.timekeeping.repositories

import android.util.Log
import com.example.timekeeping.models.Payroll
import com.example.timekeeping.models.applyWageRules
import com.example.timekeeping.ui.admin.rule.SalaryFieldName
import com.example.timekeeping.utils.convertToReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class PayrollsRepo @Inject constructor(
    private val db : FirebaseFirestore,
) {
    fun getTotalPaymentByMonth(groupId: String, employeeId: String, month: Int, year: Int, onSuccess: (Int) -> Unit = {}, onFailure: (Exception) -> Unit ) {
        db.collection("payrolls")
            .whereEqualTo("groupId", groupId)
            .whereEqualTo("employeeId", employeeId)
            .whereEqualTo("month", month)
            .whereEqualTo("year", year)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val payroll = documents.documents[0].toObject(Payroll::class.java)
                    if (payroll != null) {
                        onSuccess(payroll.totalPayment)
                        Log.d("PayrollRepo", "totalPayment: ${payroll.totalPayment}")
                    } else {
                        onFailure(Exception("Payroll not found"))
                    }
                } else {
                    onFailure(Exception("Payroll not found"))
                }
            }

    }

    fun getTotalPaymentByMonth(groupId: String, month: Int, year: Int, onSuccess: (Int) -> Unit = {}, onFailure: (Exception) -> Unit ) {
        db.collection("payrolls")
            .whereEqualTo("groupId", groupId)
            .whereEqualTo("month", month)
            .whereEqualTo("year", year)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val payroll = documents.documents[0].toObject(Payroll::class.java)
                    if (payroll != null) {
                        onSuccess(payroll.totalPayment)
                        Log.d("PayrollRepo", "totalPayment: ${payroll.totalPayment}")
                    } else {
                        onFailure(Exception("Payroll not found"))
                    }
                } else {
                    onFailure(Exception("Payroll not found"))
                }
            }

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getTotalWageEmployeeByMonth(
        groupId: String,
        employeeId: String,
        month: Int,
        year: Int,
        onSuccess: (Int) -> Unit = {},
    ) {
        db.collection("payrolls")
            .whereEqualTo("groupId", groupId)
            .whereEqualTo("employeeId", employeeId)
            .whereEqualTo("month", month)
            .whereEqualTo("year", year)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val payroll = documents.documents[0].toObject(Payroll::class.java)
                    if (payroll != null) {
                        db.collection("attendances")
                            .whereEqualTo("groupId", groupId)
                            .whereEqualTo("employeeId", employeeId.convertToReference("employees"))
                            .get()
                            .addOnSuccessListener { documents ->
                                val countMap = mutableMapOf<String, Int>()
                                val document = documents.documents.firstOrNull()

                                if(document != null){
                                    val employeeId = document.getDocumentReference("employeeId")?.id
                                    if (employeeId != null) {
                                        countMap[employeeId] = countMap.getOrDefault(employeeId, 0) + 1
                                    }
                                }

                                for ((employeeId, count) in countMap) {
                                    GlobalScope.launch {
                                        val comparisonMap = mapOf(
                                            SalaryFieldName.NUMBER_OF_DAYS.label to count
                                        )

                                        try {
                                            val finalWage = applyWageRules(groupId, comparisonMap, payroll.totalWage)

                                            onSuccess(finalWage) // Trả kết quả sau khi có finalWage
                                        } catch (e: Exception) {
                                            //onSuccess(totalWage) // Fallback nếu có lỗi
                                        }
                                    }
                                    Log.d("AttendanceCount", "Employee $employeeId has $count attendances")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.w("Firestore", "Error getting documents: ", exception)
                            }

                        Log.d("PayrollRepo", "totalWage: ${payroll.totalWage}")
                    }
                }
            }
    }

    fun getTotalWageGroupByMonth(
        groupId: String,
        month: Int,
        year: Int,
        onSuccess: (Int) -> Unit = {},
    ) {
        db.collection("payrolls")
            .whereEqualTo("groupId", groupId)
            .whereEqualTo("month", month)
            .whereEqualTo("year", year)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val payroll = documents.documents[0].toObject(Payroll::class.java)
                    if (payroll != null) {
                        onSuccess(payroll.totalWage)
                        Log.d("PayrollRepo", "totalWage: ${payroll.totalWage}")
                    }
                }
            }
    }

    fun getAllTotalWage(
        groupId: String,
        onSuccess: (Int) -> Unit = {},
    ) {
        db.collection("payrolls")
            .whereEqualTo("groupId", groupId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val payroll = documents.documents[0].toObject(Payroll::class.java)
                    if (payroll != null) {
                        onSuccess(payroll.totalWage)
                        Log.d("PayrollRepo", "totalWage: ${payroll.totalWage}")
                    }
                }
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getTotalWage(
        groupId: String,
        onSuccess: (Int) -> Unit = {},
    ) {
        db.collection("payrolls")
            .whereEqualTo("groupId", groupId)
            .get()
            .addOnSuccessListener { documents ->
                var totalUnpaid = 0
                for (document in documents) {
                    val payroll = document.toObject(Payroll::class.java)
                    totalUnpaid += payroll.totalWage
                    Log.d("PayrollRepo", "totalUnpaid: $totalUnpaid")
                }

                db.collection("attendances")
                    .whereEqualTo("groupId", groupId)
                    .get()
                    .addOnSuccessListener { documents ->
                        val countMap = mutableMapOf<String, Int>()

                        for (document in documents) {
                            val employeeId = document.getDocumentReference("employeeId")?.id
                            if (employeeId != null) {
                                countMap[employeeId] = countMap.getOrDefault(employeeId, 0) + 1
                            }
                        }

                        for ((employeeId, count) in countMap) {
                            GlobalScope.launch {
                                val comparisonMap = mapOf(
                                    SalaryFieldName.NUMBER_OF_DAYS.label to count
                                )

                                try {
                                    val finalWage = applyWageRules(groupId, comparisonMap, totalUnpaid)
                                    onSuccess(finalWage) // Trả kết quả sau khi có finalWage
                                } catch (e: Exception) {
                                    //onSuccess(totalWage) // Fallback nếu có lỗi
                                }
                            }
                            Log.d("AttendanceCount", "Employee $employeeId has $count attendances")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w("Firestore", "Error getting documents: ", exception)
                    }


//                onSuccess(totalUnpaid)
            }
    }

    fun getTotalPayment(
        groupId: String,
        onSuccess: (Int) -> Unit = {},
    ) {
        db.collection("payrolls")
            .whereEqualTo("groupId", groupId)
            .get()
            .addOnSuccessListener { documents ->
                var totalPayment = 0
                for (document in documents) {
                    val payroll = document.toObject(Payroll::class.java)
                    totalPayment += payroll.totalPayment
                    Log.d("PayrollRepo", "totalPayment: $totalPayment")
                }
                onSuccess(totalPayment)
            }
    }

    fun getTotalPaymentGroupByMonth(
        groupId: String,
        month: Int,
        year: Int,
        onSuccess: (Int) -> Unit = {},
    ) {
        db.collection("payrolls")
            .whereEqualTo("groupId", groupId)
            .whereEqualTo("month", month)
            .whereEqualTo("year", year)
            .get()
            .addOnSuccessListener { documents ->
                var totalPayment = 0
                for (document in documents) {
                    val payroll = document.toObject(Payroll::class.java)
                    totalPayment += payroll.totalPayment
                    Log.d("PayrollRepo", "totalPayment: $totalPayment")
                }
                onSuccess(totalPayment)
            }
    }
}