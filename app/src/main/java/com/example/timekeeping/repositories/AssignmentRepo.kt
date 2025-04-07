package com.example.timekeeping.repositories

import android.util.Log
import com.example.timekeeping.models.Assignment
import com.example.timekeeping.models.Employee
import com.google.firebase.firestore.FirebaseFirestore

class AssignmentRepo(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getAssignments(employeeId: String) {
        db.collection("assignments")
            .whereEqualTo("employeeId", employeeId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val assignment = document.toObject(Assignment::class.java)
                    // Handle the assignment data
                }
            }.addOnFailureListener { exception ->
                // Handle the exception
                Log.e("AssignmentRepo", "Error getting assignments", exception)
            }
    }

    fun addAssignment(assignment: Assignment) {
        db.collection("assignments")
            .add(assignment)
            .addOnSuccessListener { documentReference ->
                // Handle the success
                Log.d("AssignmentRepo", "Assignment added with ID: ${documentReference.id}")
            }.addOnFailureListener { exception ->
                // Handle the exception
                Log.e("AssignmentRepo", "Error adding assignment", exception)
            }
    }
}