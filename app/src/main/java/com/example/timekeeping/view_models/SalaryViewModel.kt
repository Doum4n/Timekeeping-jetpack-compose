package com.example.timekeeping.view_models

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.timekeeping.models.Adjustment
import com.example.timekeeping.models.Salary
import com.example.timekeeping.repositories.SalaryRepo
import com.example.timekeeping.ui.admin.employees.form.TypeAllowance
import com.example.timekeeping.ui.admin.employees.form.TypeDeduct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class SalaryViewModel @Inject constructor (
    savedStateHandle: SavedStateHandle,
    private val salaryRepo: SalaryRepo
) : ViewModel() {

    val groupId: String = savedStateHandle["groupId"] ?: ""
    val employeeId: String = savedStateHandle["employeeId"] ?: ""
    val adjustmentId: String = savedStateHandle["adjustmentId"] ?: ""

    private val _advanceMoney = MutableStateFlow<List<Adjustment>>(emptyList())
    val advanceMoney: StateFlow<List<Adjustment>> = _advanceMoney

    private val _salaryInfo = MutableStateFlow<List<Adjustment>>(emptyList())
    val salaryInfo: StateFlow<List<Adjustment>> = _salaryInfo

    private val _totalUnpaidSalary = MutableStateFlow(0)
    val totalUnpaidSalary: StateFlow<Int> = _totalUnpaidSalary

    fun createAdjustSalary(adjustment: Adjustment, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        salaryRepo.createAdjustSalary(adjustment, onSuccess, onFailure)
    }

    fun getAdjustSalary(adjustmentId: String, onSuccess: (Adjustment?) -> Unit) {
        salaryRepo.getAdjustSalary(adjustmentId, onSuccess)
    }

    fun getAdvanceMoney(groupId: String, employeeId: String, month: Int, year: Int, onFailure: (Exception) -> Unit = {}) {
        salaryRepo.getAdvanceMoney(groupId, employeeId, month, year,
            onSuccess = {
                _advanceMoney.value = it
            }, onFailure)
    }

    fun getTotalWorkDay(groupId: String, month: Int, year: Int,     onResult: (workDays: Int, paidLeaveDays: Int) -> Unit) {
        salaryRepo.getTotalWorkDay(groupId, month, year, onResult)
    }

    fun deleteAdjustSalary(adjustment: Adjustment, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        salaryRepo.deleteAdjustSalary(adjustment, onSuccess, onFailure)
    }

    fun getSalaryInfoByMonth(groupId: String, employeeId: String, month: Int, year: Int, onFailure: (Exception) -> Unit = {}) {
        salaryRepo.getSalaryInfoByMonth(groupId, employeeId, month, year,
            onSuccess = {
                _salaryInfo.value = it
            }, onFailure)
    }

    fun calculateTotalWage(groupId: String, employeeId: String, month: Int, year: Int, onResult: (Int) -> Unit) {
        salaryRepo.calculateTotalWage(groupId, employeeId, month, year, onResult)
    }

    fun getTotalUnpaidSalary(groupId: String, month: Int, year: Int, isAllTime: Boolean = false) {
        salaryRepo.getTotalUnpaidSalary(groupId, month, year, isAllTime) {
            _totalUnpaidSalary.value = it
        }
    }

    fun getTotalSalary(groupId: String, month: Int, year: Int, onResult: (Int) -> Unit) {
        salaryRepo.getTotalSalary(groupId, month, year, onResult)
    }

    fun getTotalAdvanceMoney(groupId: String, month: Int, year: Int, onResult: (Int) -> Unit) {
        salaryRepo.getTotalAdvance(groupId, month, year, onResult)
    }

    fun getTotalBonus(groupId: String, month: Int, year: Int, onResult: (Int) -> Unit) {
        salaryRepo.getTotalBonus(groupId, month, year, onResult)
    }

    fun getTotalUnpaidSalaryByEmployee(totalWage: Int, totalPayment: Int): Int {
        val allowanceLabels = TypeAllowance.entries.map { it.label }
        val deductLabels = TypeDeduct.entries.map { it.label }.filter { it != "Ứng lương" }
        val totalBonus = salaryInfo.value.filter { it.adjustmentType in allowanceLabels }.sumOf { it.adjustmentAmount }
        val totalAdvance = salaryInfo.value.filter { it.adjustmentType == "Ứng lương" }.sumOf { it.adjustmentAmount }
        val totalDeduct = salaryInfo.value.filter { it.adjustmentType in deductLabels }.sumOf { it.adjustmentAmount }

        Log.d("SalaryViewModel", "totalWage: $totalWage, totalBonus: $totalBonus, totalAdvance: $totalAdvance, totalDeduct: $totalDeduct, totalPayment: $totalPayment")

        return totalWage + totalBonus + totalAdvance + totalDeduct - totalPayment
    }

    fun getSalaryById(groupId: String, employeeId: String, onSuccess: (Salary?) -> Unit) {
        salaryRepo.getSalaryById(groupId, employeeId, onSuccess)
    }

    fun updateAdjustSalary(
        adjustments: Adjustment,
        onSuccess: () -> Boolean,
        onFailure: (Exception) -> Unit
    ) {
        salaryRepo.updateAdjustSalary(adjustmentId, adjustments, onSuccess, onFailure)
    }

    fun getDeductMoney(groupId: String, employeeId: String, monthValue: Int, year: Int) {
        salaryRepo.getDeductMoney(groupId, employeeId, monthValue, year,
            onSuccess = {
                _salaryInfo.value = it
            })
    }

    fun getBonusAdjustment(groupId: String, employeeId: String, monthValue: Int, year: Int) {
        salaryRepo.getBonusAdjustment(groupId, employeeId, monthValue, year,
            onSuccess = {
                _salaryInfo.value = it
            })
    }
}