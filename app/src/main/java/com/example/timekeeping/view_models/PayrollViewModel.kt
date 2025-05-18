package com.example.timekeeping.view_models

import androidx.lifecycle.ViewModel
import com.example.timekeeping.repositories.PayrollsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PayrollViewModel @Inject constructor (
    val payRollRepo: PayrollsRepo
) : ViewModel() {

    fun getTotalPaymentEmployeeByMonth(groupId: String, employeeId: String, month: Int, year: Int, onSuccess: (Int) -> Unit = {}, onFailure: (Exception) -> Unit) {
        payRollRepo.getTotalPaymentByMonth(groupId, employeeId, month, year, onSuccess, onFailure)
    }

    fun getTotalWageEmployeeByMonth(groupId: String, employeeId: String, month: Int, year: Int, onSuccess: (Int) -> Unit = {}) {
        payRollRepo.getTotalWageEmployeeByMonth(groupId, employeeId, month, year, onSuccess)
    }

    fun getTotalWageGroupByMonth(groupId: String, month: Int, year: Int, onSuccess: (Int) -> Unit = {}) {
        payRollRepo.getTotalWageGroupByMonth(groupId, month, year, onSuccess)
    }

    fun getTotalPayment(groupId: String, onSuccess: (Int) -> Unit = {}) {
        payRollRepo.getTotalPayment(groupId, onSuccess)
    }

    fun getTotalPaymentGroupByMonth(groupId: String, month: Int, year: Int, onSuccess: (Int) -> Unit = {}) {
        payRollRepo.getTotalPaymentGroupByMonth(groupId, month, year, onSuccess)
    }

    fun getTotalWageGroup(groupId: String, onSuccess: (Int) -> Unit = {}) {
        payRollRepo.getTotalWage(groupId, onSuccess)
    }
}