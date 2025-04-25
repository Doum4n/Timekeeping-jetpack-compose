package com.example.timekeeping.models

import com.example.timekeeping.utils.DateTimeMap

data class Payment(
    val amount: Int = 0,
    val createAt: DateTimeMap = DateTimeMap(),
    val note: String = "",
) {
}