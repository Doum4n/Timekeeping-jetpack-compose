package com.example.timekeeping.repositories

import android.util.Log
import com.example.timekeeping.models.Assignment
import com.example.timekeeping.models.Employee
import com.example.timekeeping.utils.convertToReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class AssignmentRepo @Inject constructor(
    private val db: FirebaseFirestore
) {
    fun getAssignments(employeeId: String, shiftId: String, callback: (List<Assignment>) -> Unit) {
        db.collection("assignments")
            .whereEqualTo("employeeId", employeeId.convertToReference("employees"))
            .whereEqualTo("shiftId", shiftId.convertToReference("shifts"))
            .get()
            .addOnSuccessListener { documents ->
                val assignments = documents.map { doc ->
                    val assignment = doc.toObject(Assignment::class.java)
                    assignment.copy(id = doc.id)
                }
                callback(assignments)
            }
            .addOnFailureListener { exception ->
                Log.e("AssignmentRepo", "Error getting assignments", exception)
            }
    }

    fun addAssignment(assignment: Assignment) {
        db.collection("assignments")
            .add(assignment)
            .addOnSuccessListener { documentReference ->
            }.addOnFailureListener { exception ->
            }
    }

    fun updateAssignment(assignmentId: String, assignment: Assignment) {
        db.collection("assignments")
            .document(assignmentId)
            .set(assignment)
            .addOnSuccessListener {
                // Handle the success
                Log.d("AssignmentRepo", "Assignment updated with ID: ${assignment.id}")
            }.addOnFailureListener { exception ->
                // Handle the exception
                Log.e("AssignmentRepo", "Error updating assignment", exception)
            }
    }
}