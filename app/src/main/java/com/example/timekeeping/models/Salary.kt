package com.example.timekeeping.models

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Salary(
    val employeeId: String,
    val groupId: String,
    val salaryType: String, // Phương thức tính lương
    val salary: Int,
    val createdAt: Date, // Ngày tạo
    val dateApplied: Date, // Ngày áp dụng
) {
}