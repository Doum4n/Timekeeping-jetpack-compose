package com.example.timekeeping.models

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude

data class Name(
    val firstName: String = "",
    val lastName: String = ""
){
    @get:Exclude
    val fullName: String
        get() = "$lastName $firstName"

    fun form(fullName: String): Name {
        val parts = fullName.split(" ")
        return Name(
            lastName = parts.dropLast(1).joinToString(" "),
            firstName = parts.last()
        )
    }
}

data class Employee(
    @Exclude
    var id: String = "",
    val userId: String = "",
    var name: Name = Name(),
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
            "name" to name,
            "avatarUrl" to avatarUrl,
            "email" to email,
            "phone" to phone,
            "address" to address
        )
    }

    fun toMapWithoutUserId(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "avatarUrl" to avatarUrl,
            "email" to email,
            "phone" to phone,
            "address" to address
        )
    }
}
