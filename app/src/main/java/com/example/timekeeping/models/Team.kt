package com.example.timekeeping.models

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude

data class Team(
    @Exclude
    val id: String = "",
    val name: String = "",
    val groupId: String = "",
    val description: String = "",
    val members: List<DocumentReference> = emptyList(),
) {
}