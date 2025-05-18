package com.example.timekeeping.repositories

import android.util.Log
import com.example.timekeeping.models.Name
import com.example.timekeeping.models.Payment
import com.example.timekeeping.utils.DateTimeMap
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.getField
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class PaymentRepo @Inject constructor(
    val firestore: FirebaseFirestore
) {
    private val createdAt = LocalDate.now().year.toString() + "-" + LocalDate.now().month.value.toString()

    fun createPayment(
        groupId: String,
        employeeId: String,
        payment: Payment,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val payrollQuery = firestore.collection("payrolls")
            .whereEqualTo("groupId", groupId)
            .whereEqualTo("employeeId", employeeId)
            .whereEqualTo("month", payment.createdAt.month)
            .whereEqualTo("year", payment.createdAt.year)

        firestore.collection("payments")
            .add(payment)
            .addOnSuccessListener {
                payrollQuery.get()
                    .addOnSuccessListener { querySnapshot ->
                        val document = querySnapshot.documents.firstOrNull()
                        if (document != null) {
                            val payrollRef = document.reference

                            firestore.runTransaction { transaction ->
                                val snapshot = transaction.get(payrollRef)
                                val oldPayment = snapshot.getDouble("totalPayment") ?: 0.0
                                val newPayment = oldPayment - payment.amount

                                transaction.update(payrollRef, "totalPayment", newPayment)
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


    fun getPayments(groupId: String, employeeId: String, onSuccess: (List<Payment>) -> Unit, onFailure: (Exception) -> Unit, month: Int, year: Int) {
        firestore.collection("payments")
            .whereEqualTo("groupId", groupId)
            .whereEqualTo("employeeId", employeeId)
            .whereEqualTo("createdAt.year", year)
            .whereEqualTo("createdAt.month", month)
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
            .collection("payments-${payment.createdAt.year}-${payment.createdAt.month}")
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
            .collection("payments-${payment.createdAt.year}-${payment.createdAt.month}")
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
        firestore.collection("payments")
            .whereEqualTo("createdAt.year", year)
            .whereEqualTo("createdAt.month", month)
            .whereEqualTo(FieldPath.documentId(), paymentId)
            .get()
            .addOnSuccessListener({
                val payment = it.documents.firstOrNull()?.toObject(Payment::class.java)?.copy(id = paymentId)
                if (payment != null) {
                    Log.d("PaymentRepo", "Payment: $payment")
                    onSuccess(payment)
                }
            }).addOnFailureListener({
                onFailure(it)
            })
    }
}