package com.example.timekeeping.utils

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//fun getEmployeeReferenceByUserId(
//    userId: String,
//    onResult: (DocumentReference?) -> Unit
//) {
//    FirebaseFirestore.getInstance().collection("employees")
//        .whereEqualTo("userId", userId)
//        .limit(1)
//        .get()
//        .addOnSuccessListener { querySnapshot ->
//            val doc = querySnapshot.documents.firstOrNull()
//            val ref = doc?.reference
//            onResult(ref)
//        }
//        .addOnFailureListener {
//            onResult(null)
//        }
//}

fun String.convertToReference(collectionName: String): DocumentReference {
    return FirebaseFirestore.getInstance().collection(collectionName).document(this)
}