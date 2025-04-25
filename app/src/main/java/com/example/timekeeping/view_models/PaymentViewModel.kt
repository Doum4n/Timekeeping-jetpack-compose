package com.example.timekeeping.view_models

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
    private val paymentRepo: PaymentRepo
) : ViewModel() {

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
}