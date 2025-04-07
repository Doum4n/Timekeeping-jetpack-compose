package com.example.timekeeping.models

import com.google.firebase.firestore.Exclude

data class Employee(
    @Exclude
    var id: String = "",
    val userId: String = "",
    val fullName: String = "",
    val avatarUrl: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",

    @Exclude
    val salary: Int = 0,
    @Exclude
    val salaryType: String = "",
    @Exclude
    val role: String = "",

    @Exclude
    val status: Status = Status.PENDING,
    @Exclude
    val isCreator: Boolean = false,
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "fullName" to fullName,
            "avatarUrl" to avatarUrl,
            "email" to email,
            "phone" to phone,
            "address" to address
            // Không bao gồm salary, salaryType và role ở đây
        )
    }

    fun toEmployeeData(): Map<String, Any?> {
        return mapOf(
            "name" to fullName,
            "status" to status.toString(),
            "isCreator" to isCreator
        )
    }
}
