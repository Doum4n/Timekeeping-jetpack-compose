package com.example.timekeeping.repositories

import android.util.Log
import com.example.timekeeping.models.Name
import com.example.timekeeping.models.Payment
import com.example.timekeeping.utils.DateTimeMap
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.getField
import java.time.LocalDate
import javax.inject.Inject

class PaymentRepo @Inject constructor(
    val firestore: FirebaseFirestore
) {
    private val createAt = LocalDate.now().year.toString() + "-" + LocalDate.now().month.value.toString()

    fun createPayment(groupId: String, employeeId: String, payment: Payment, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        firestore.collection("salaries")
            .document(salaryDocId(groupId, employeeId))
            .collection("payments-$createAt")
            .document(LocalDate.now().dayOfMonth.toString())
            .set(payment)
            .addOnSuccessListener({
                onSuccess()
            }).addOnFailureListener({
                onFailure(it)
            })
    }

    fun getPayments(groupId: String, employeeId: String, onSuccess: (List<Payment>) -> Unit, onFailure: (Exception) -> Unit, month: Int, year: Int) {
        firestore.collection("salaries")
            .document(salaryDocId(groupId, employeeId))
            .collection("payments-$year-$month")
            .get()
            .addOnSuccessListener({
                val payments = it.documents.mapNotNull({ document ->
                    document.toObject(Payment::class.java)?.copy(id = document.id)
                })
                onSuccess(payments)
            }).addOnFailureListener({
                onFailure(it)
            })
    }

    fun updatePayment(groupId: String, employeeId: String, payment: Payment, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        firestore.collection("salaries")
            .document(salaryDocId(groupId, employeeId))
            .collection("payments-${payment.createAt.year}-${payment.createAt.month}")
            .document(payment.id)
            .set(payment)
            .addOnSuccessListener({
                onSuccess()
            }).addOnFailureListener({
                onFailure(it)
            })
    }

    fun deletePayment(groupId: String, employeeId: String, payment: Payment, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        firestore.collection("salaries")
            .document(salaryDocId(groupId, employeeId))
            .collection("payments-${payment.createAt.year}-${payment.createAt.month}")
            .document(payment.id)
            .delete()
            .addOnSuccessListener({
                onSuccess()
            }).addOnFailureListener({
                onFailure(it)
            })
    }

    fun getPaymentById(
        groupId: String,
        employeeId: String,
        paymentId: String,
        month: Int,
        year: Int,
        onSuccess: (Payment) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection("salaries")
            .document(salaryDocId(groupId, employeeId))
            .collection("payments-$year-$month")
            .document(paymentId)
            .get()
            .addOnSuccessListener({
                val payment = it.toObject(Payment::class.java)?.copy(id = it.id)
                if (payment != null) {
                    onSuccess(payment)
                }
            }).addOnFailureListener({
                onFailure(it)
            })
    }
}