package com.example.timekeeping.repositories

import android.util.Log
import com.example.timekeeping.models.Shift
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class ShiftRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // Load shifts by groupId
    fun loadShifts(groupId: String, onResult: (List<Shift>) -> Unit) {
        db.collection("shifts")
            .whereEqualTo("groupId", groupId)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("ShiftRepository", "Listen failed", error)
                    return@addSnapshotListener
                }

                val shiftsList = snapshots?.documents?.mapNotNull { doc ->
                    doc.toObject(Shift::class.java)?.copy(id = doc.id) // Ensure id is updated
                } ?: emptyList()

                // temp
                FirebaseMessaging.getInstance().subscribeToTopic("news")
                    .addOnCompleteListener { task ->
                        var msg = "Subscribed to topic"
                        if (!task.isSuccessful) {
                            msg = "Subscription failed"
                        }
                        Log.d("FirebaseMessaging", msg)
                    }


                onResult(shiftsList) // Return list of shifts
            }
    }

    // Create a new shift
    fun createShift(shift: Shift, onSuccess: () -> Unit) {
        db.collection("shifts").add(shift).addOnSuccessListener {
            onSuccess()
        }
    }

    // Update shift
    fun updateShift(shiftId: String, shift: Shift, onSuccess: () -> Unit) {
        db.collection("shifts").document(shiftId).set(shift).addOnSuccessListener {
            onSuccess()
        }
    }

    // Delete shift
    fun deleteShift(shiftId: String, onSuccess: () -> Unit) {
        db.collection("shifts").document(shiftId).delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("ShiftRepository", "Delete failed", e)
            }
    }

    // Get shift by id
    fun getShiftById(shiftId: String, callback: (Shift?) -> Unit) {
        db.collection("shifts").document(shiftId).get().addOnSuccessListener { document ->
            val shift = document.toObject(Shift::class.java)
            callback(shift)
        }
    }
}
