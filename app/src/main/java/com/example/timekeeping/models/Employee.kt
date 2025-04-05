package com.example.timekeeping.models

import com.google.firebase.firestore.Exclude

/**
 * Nếu nhân viên chưa liên kết tài khoản thì khi truy xuất sẽ lấy `var`,
 * ngược lại lấy `userId`.
 */
data class Employee (
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
}
