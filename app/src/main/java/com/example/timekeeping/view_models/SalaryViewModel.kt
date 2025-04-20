package com.example.timekeeping.view_models

import androidx.lifecycle.ViewModel
import com.example.timekeeping.models.Salary
import com.example.timekeeping.repositories.SalaryRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SalaryViewModel @Inject constructor (
    private val salaryRepo: SalaryRepo
) : ViewModel() {

    private val _advanceMoney = MutableStateFlow<List<Salary>>(emptyList())
    val advanceMoney: StateFlow<List<Salary>> = _advanceMoney

    fun createSalary(salary: Salary, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        salaryRepo.createSalary(salary, onSuccess, onFailure)
    }

    fun getAdvanceMoney(employeeId: String, onFailure: (Exception) -> Unit = {}) {
        salaryRepo.getAdvanceMoney(employeeId,
            onSuccess = {
                _advanceMoney.value = it
            }, onFailure)
    }

}