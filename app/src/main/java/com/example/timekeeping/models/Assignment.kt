package com.example.timekeeping.models

import java.time.Month
import java.time.Year

// Cho phép phần công theo team hoặc theo cá nhân
class Assignment(
    val shiftId: Int,
    val userId: Int,
    val teamId: Int,
    val month: Month,
    val year: Year,
    val dates: List<Int> // Danh sách các ngày trong tháng được phân công

    // Khả năng tái sử dụng lại và không cần phải phân công lại TỪNG NHÂN VIÊN theo hằng tháng
) {
}