package com.example.timekeeping.models

import com.google.firebase.firestore.Exclude

data class Shift(
    @Exclude
    var id: String = "",
    val shiftName: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val allowance: Int = 0,
    val coefficient: Double = 1.0,
    val groupId: String = ""
) {

}