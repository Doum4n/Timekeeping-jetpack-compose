package com.example.timekeeping.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.PropertyName
import java.util.Date

class Group(
    @get:Exclude
    var id: String = "",  // Không lưu vào Firestore
    @PropertyName("creator_id")
    var creatorId: String = "",
    var name: String = "",
    var payday: Date? = null,
) {
    // Hàm helper để convert từ Firestore DocumentSnapshot
    companion object {
        fun fromDocument(document: com.google.firebase.firestore.DocumentSnapshot): Group? {
            return document.toObject(Group::class.java)?.apply {
                id = document.id
            }
        }
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "creator_id" to creatorId,
            "name" to name,
            "payday" to payday
        )
    }

    fun delete(onSuccess: () -> Unit, onFailure: (Exception) -> Unit){
        val db = FirebaseFirestore.getInstance()
        db.collection("groups").document(id).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}