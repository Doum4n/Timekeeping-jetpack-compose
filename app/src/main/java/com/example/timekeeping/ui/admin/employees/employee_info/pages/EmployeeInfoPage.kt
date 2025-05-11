package com.example.timekeeping.ui.admin.employees.employee_info.pages

import com.example.timekeeping.models.Employee
import com.example.timekeeping.models.Salary

sealed class EmployeeInfoPage {
    data class PersonalInfo(val employee: Employee, val onEmployeeChange: (Employee) -> Unit = {}) : EmployeeInfoPage()
    data class SalaryInfo(val salary: Salary, val onSalaryChange: (Salary) -> Unit = {}) : EmployeeInfoPage()
}