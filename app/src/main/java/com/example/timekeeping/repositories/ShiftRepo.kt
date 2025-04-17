package com.example.timekeeping.repositories

import android.util.Log
import com.example.timekeeping.models.Employee
import com.example.timekeeping.models.Shift
import com.example.timekeeping.utils.convertToReference
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject

class ShiftRepository @Inject constructor(
    private val db: FirebaseFirestore
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

    fun loadEmployees(shiftId: String, onResult: (List<Employee>) -> Unit) {
        db.collection("assignments")
            .whereEqualTo("shiftId", shiftId.convertToReference("shifts"))
            .get()
            .addOnSuccessListener { assignments ->
                val tasks = assignments?.documents?.mapNotNull {
                    it.getDocumentReference("employeeId")?.get()
                }
                Tasks.whenAllSuccess<DocumentSnapshot>(tasks).addOnSuccessListener { documents ->
                    val employees = documents.mapNotNull { doc ->
                        doc.toObject(Employee::class.java)?.apply {
                            id = doc.id
                        }
                    }
                    onResult(employees)
                }
            }.addOnFailureListener { e ->
                Log.e("ShiftRepository", "Load employees failed", e)
            }
    }
}
