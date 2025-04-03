package com.example.timekeeping.models

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FirebaseFirestore

class Employee_Group (
    @Exclude
    var id: String = "",
    var employeeId: String = "",
    var groupId: String = "",
    var status: String = "", // "pending", "accepted", "rejected"
    var role: String = "" // "admin", "member"
) {

}