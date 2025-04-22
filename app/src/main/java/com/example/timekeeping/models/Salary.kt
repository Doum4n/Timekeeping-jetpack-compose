package com.example.timekeeping.models

import com.google.firebase.firestore.Exclude
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Adjustment(
    val adjustmentType: String = "", // Loại điều chỉnh (tăng hoặc giảm)
    val adjustmentAmount: Int = 0, // Số tiền điều chỉnh
    val note: String = "", // Ghi chú
    val createdAt: Date = Date() // Ngày tạo điều chỉnh
)

data class Salary(
    @Exclude
    var id: String = "",
    val employeeId: String = "",
    val groupId: String = "",
    val salaryType: String = "", // Phương thức tính lương
    val salary: Int = 0,
    val note: String = "", // Ghi chú
    val createdAt: Date = Date(), // Ngày tạo
    val dateApplied: Date = Date(), // Ngày áp dụng
) {
}