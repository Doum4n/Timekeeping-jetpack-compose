package com.example.timekeeping.models

import com.google.firebase.firestore.Exclude
import java.time.Month
import java.time.Year

// Cho phép phần công theo team hoặc theo cá nhân
data class Assignment(
    @Exclude
    val id: String = "",
    val shiftId: String = "",
    val employeeId: String = "",
    val teamId: String = "",
    val month: Month = Month.JANUARY,
    val year: Int = Year.now().value,
    val dates: List<Int> = listOf() // Danh sách các ngày trong tháng được phân công

    // Khả năng tái sử dụng lại và không cần phải phân công lại TỪNG NHÂN VIÊN theo hằng tháng
) {
}