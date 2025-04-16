package com.example.timekeeping.ui.employees.list_employees

import com.example.timekeeping.models.Employee

sealed class EmployeePage {
    data class Unlinked(val employees: List<Employee>) : EmployeePage()
    data class Members(val employees: List<Employee>) : EmployeePage()
    data class Approval(
        val employees: List<Employee>,
        val onAcceptClick: () -> Unit,
        val onRejectClick: () -> Unit
    ) : EmployeePage()
}
