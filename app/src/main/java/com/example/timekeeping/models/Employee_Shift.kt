package com.example.timekeeping.models

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class Employee_Shift(
    val employeeId: String = "",
    val shiftId: String = "",
    val daysAssigned: List<Date> = emptyList(),
) {

}