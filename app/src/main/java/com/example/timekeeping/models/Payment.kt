package com.example.timekeeping.models

import com.example.timekeeping.utils.DateTimeMap

data class Payment(
    var id: String = "",
    val amount: Int = 0,
    val createdAt: DateTimeMap = DateTimeMap(),
    val imageUrl: String = "",
    val note: String = "",
    val groupId: String = "",
    val employeeId: String = ""
) {
}