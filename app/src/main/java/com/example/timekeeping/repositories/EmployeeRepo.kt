package com.example.timekeeping.repositories

import android.util.Log
import com.example.timekeeping.models.Employee
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.database.FirebaseDatabase

class EmployeeRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val rtdb: FirebaseDatabase = FirebaseDatabase.getInstance()
) {

    // Load accepted employees by groupId
    fun loadEmployees(groupId: String, onResult: (List<Employee>) -> Unit) {
        db.collection("employee_group")
            .whereEqualTo("groupId", groupId)
            //.whereEqualTo("status", "accepted")
            .get()
            .addOnSuccessListener { query ->
                val employeeIds = query.documents.mapNotNull { it.getString("employeeId") }.filter { it.isNotEmpty() }
                loadEmployeeDetails(employeeIds, onResult)
            }.addOnFailureListener { exception ->

            }
    }

    // Load employee details using employeeIds
    private fun loadEmployeeDetails(employeeIds: List<String>, onResult: (List<Employee>) -> Unit) {
        db.collection("employees")
            .whereIn(FieldPath.documentId(), employeeIds)
            .get()
            .addOnSuccessListener { query ->
                val employees = query.documents.mapNotNull { doc ->
                    val employee = doc.toObject(Employee::class.java)
                    employee?.id = doc.id
                    employee
                }
                Log.d("EmployeeRepository", "Loaded employees: $employees")
                onResult(employees)
            }
            .addOnFailureListener { exception ->
                Log.e("EmployeeRepository", "Error loading employee details", exception)
            }
    }

    fun requestJoinGroup(groupId: String, employeeId: String) {
        val employeeGroup = hashMapOf(
            "employeeId" to employeeId,
            "groupId" to groupId,
            "status" to "pending" // accepted, rejected, pending
        )

        db.collection("employee_group").add(employeeGroup)
    }

    // Load pending employees (tbd)
    fun loadPendingEmployees(groupId: String, onResult: (List<Employee>) -> Unit) {
        db.collection("employee_group")
            .whereEqualTo("groupId", groupId)
            .whereEqualTo("status", "pending")
            .addSnapshotListener { query, error ->
                if (error != null) {
                    Log.e("EmployeeRepository", "Error loading pending employees", error)
                    return@addSnapshotListener
                }
                val employeeIds = query?.documents?.map { it.getString("employeeId") ?: "" } ?: emptyList()
                loadEmployeeDetails(employeeIds, onResult)
            }
    }

    // Load unlinked employees (tbd)
    fun loadUnlinkedEmployees(onResult: (List<Employee>) -> Unit) {
        // This method should fetch unlinked employees
    }
}