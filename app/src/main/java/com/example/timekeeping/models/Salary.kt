package com.example.timekeeping.models

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Salary(
    val employeeId: String = "",
    val groupId: String = "",
    val salaryType: String = "", // Phương thức tính lương
    val salary: Int = 0,
    val createdAt: Date = Date(), // Ngày tạo
    val dateApplied: Date = Date(), // Ngày áp dụng
) {
}