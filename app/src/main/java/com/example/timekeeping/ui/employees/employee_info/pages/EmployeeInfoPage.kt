package com.example.timekeeping.ui.employees.employee_info.pages

import com.example.timekeeping.models.Employee
import com.example.timekeeping.models.Salary

sealed class EmployeeInfoPage {
    data class PersonalInfo(val employee: Employee) : EmployeeInfoPage()
    data class SalaryInfo(val salary: Salary) : EmployeeInfoPage()
}