package com.example.timekeeping.models

import com.google.firebase.auth.FirebaseAuth

class User(
    val id: String = "", // Đồng thời cũng là employeeId
    val fullName: String = "",
    val avatarUrl: String = "",
    val email: String = "",
    val phone: String = "",
) {
}