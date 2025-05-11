package com.example.timekeeping.repositories

import android.util.Log
import com.example.timekeeping.models.Employee
import com.example.timekeeping.models.Shift
import com.example.timekeeping.models.Time
import com.example.timekeeping.utils.DateTimeMap
import com.example.timekeeping.utils.convertToReference
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import java.time.LocalDate
import java.time.LocalDateTime
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

    fun loadEmployees(shiftId: String, day: Int, onResult: (List<Employee>) -> Unit) {
        db.collection("assignments")
            .whereEqualTo("shiftId", shiftId.convertToReference("shifts"))
            .whereArrayContains("dates", day)
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
                    Log.d("ShiftRepository", "Employees: $employees")
                    onResult(employees)
                }
            }.addOnFailureListener { e ->
                Log.e("ShiftRepository", "Load employees failed", e)
            }
    }

    fun getOnGoingShift(employeeId: String, groupId: String, callback: (List<Shift>) -> Unit) {
        val now = LocalDateTime.now()
        val currentTime = Time.form(now)

        Log.d("ShiftRepository", "$employeeId, $groupId, $currentTime")

        db.collection("assignments")
            .whereEqualTo("employeeId", employeeId.convertToReference("employees"))
            .whereArrayContains("dates", now.dayOfMonth)
            .get()
            .addOnSuccessListener { assignments ->
                val shiftRefs = assignments.documents.mapNotNull {
                    it.getDocumentReference("shiftId")?.get()
                }

                Tasks.whenAllSuccess<DocumentSnapshot>(shiftRefs)
                    .addOnSuccessListener { documents ->
                        val assignedShifts = documents.mapNotNull { doc ->
                            doc.toObject(Shift::class.java)?.apply {
                                id = doc.id
                            }
                        }

                        val assignedShiftIds = assignedShifts.map { it.id }
                        Log.d("ShiftRepository", "Assigned shift ids: $assignedShiftIds")

                        db.collection("shifts")
                            .whereEqualTo("groupId", groupId)
                            .whereLessThanOrEqualTo("startTime", currentTime)
                            .whereGreaterThanOrEqualTo("endTime", currentTime)
                            .get()
                            .addOnSuccessListener { documents ->
                                val ongoingShifts = documents.mapNotNull {
                                    it.toObject(Shift::class.java).apply {
                                        id = it.id
                                    }
                                }.filter { it.id in assignedShiftIds }

                                Log.d("ShiftRepository", "On going shift: $ongoingShifts")
                                callback(ongoingShifts)
                            }
                            .addOnFailureListener { e ->
                                Log.e("ShiftRepository", "Get on going shift failed", e)
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e("ShiftRepository", "Failed to load assigned shift documents", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("ShiftRepository", "Load assignments failed", e)
            }
    }
}
