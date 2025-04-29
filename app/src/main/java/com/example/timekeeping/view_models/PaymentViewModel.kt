package com.example.timekeeping.view_models

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.timekeeping.models.Payment
import com.example.timekeeping.repositories.PaymentRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val paymentRepo: PaymentRepo
) : ViewModel() {

    val groupId: String = savedStateHandle.get<String>("groupId") ?: ""
    val employeeId: String = savedStateHandle.get<String>("employeeId") ?: ""
    val paymentId: String = savedStateHandle.get<String>("paymentId") ?: ""

    private val _payments = MutableStateFlow<List<Payment>>(emptyList())
    val payments: StateFlow<List<Payment>> = _payments.asStateFlow()

    fun createPayment(groupId: String, employeeId: String, payment: Payment, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        paymentRepo.createPayment(groupId, employeeId, payment, onSuccess, onFailure)
    }

    fun getPayments(groupId: String, employeeId: String, month: Int, year: Int) {
        paymentRepo.getPayments(groupId, employeeId, onSuccess = {
            _payments.value = it
        }, {}, month, year)
    }

    fun getTotalPayment(): Int {
        return payments.value.sumOf { it.amount }
    }

    fun getPaymentById(groupId: String, employeeId: String, paymentId: String, month: Int, year: Int, onSuccess: (Payment) -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        paymentRepo.getPaymentById(groupId, employeeId, paymentId, month, year, onSuccess, onFailure)
    }

    fun updatePayment(groupId: String, employeeId: String, payment: Payment, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        paymentRepo.updatePayment(
            groupId,
            employeeId,
            payment,
            onSuccess,
            onFailure,
        )
    }

    fun deletePayment(groupId: String, employeeId: String, payment: Payment, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        paymentRepo.deletePayment(groupId, employeeId, payment, onSuccess, onFailure)
    }
}