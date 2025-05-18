package com.example.timekeeping.repositories

import android.util.Log
import com.example.timekeeping.models.Payroll
import com.google.firebase.firestore.FirebaseFirestore
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
                        onSuccess(payroll.totalWage)
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
                onSuccess(totalUnpaid)
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