package com.example.timekeeping.view_models

import androidx.lifecycle.ViewModel
import com.example.timekeeping.models.Adjustment
import com.example.timekeeping.models.Salary
import com.example.timekeeping.repositories.SalaryRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SalaryViewModel @Inject constructor (
    private val salaryRepo: SalaryRepo
) : ViewModel() {

    private val _advanceMoney = MutableStateFlow<List<Adjustment>>(emptyList())
    val advanceMoney: StateFlow<List<Adjustment>> = _advanceMoney

    private val _salaryInfo = MutableStateFlow<List<Adjustment>>(emptyList())
    val salaryInfo: StateFlow<List<Adjustment>> = _salaryInfo

    private val _totalUnpaidSalary = MutableStateFlow(0)
    val totalUnpaidSalary: StateFlow<Int> = _totalUnpaidSalary


    fun createAdjustSalary(groupId: String, employeeId: String, adjustment: Adjustment, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        salaryRepo.createAdjustSalary(groupId, employeeId, adjustment, onSuccess, onFailure)
    }

    fun getAdvanceMoney(groupId: String, employeeId: String, month: Int, year: Int, onFailure: (Exception) -> Unit = {}) {
        salaryRepo.getAdvanceMoney(groupId, employeeId, month, year,
            onSuccess = {
                _advanceMoney.value = it
            }, onFailure)
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

    fun getTotalUnpaidSalary(groupId: String) {
        salaryRepo.getTotalUnpaidSalary(groupId) {
            _totalUnpaidSalary.value = it
        }
    }
}