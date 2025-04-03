package com.example.timekeeping.view_models

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.timekeeping.models.Employee
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore

class EmployeeViewModel(
    groupId: String,
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    val employees = mutableStateListOf<Employee>()
    val pendingEmployees = mutableStateListOf<Employee>()
    val unlinkedEmployees = mutableStateListOf<Employee>()

    init {
        loadEmployees(groupId)
    }

    private fun loadEmployees(groupId: String) {
        db.collection("employee_group").whereEqualTo("groupId", groupId)
            .whereEqualTo("status", "accepted")
            .get()
            .addOnSuccessListener { query ->
                val employeeIds = query.documents.map { it.getString("employeeId") ?: "" }
                loadEmployeeDetails(employeeIds, employees)
            }
    }

    private fun loadEmployeeDetails(employeeIds: List<String>, employees: SnapshotStateList<Employee>) {
        db.collection("employees").whereIn(FieldPath.documentId(), employeeIds)
            .get()
            .addOnSuccessListener { query ->
                employees.clear()
                for (doc in query.documents) {
                    val employee = doc.toObject(Employee::class.java)
                    employee?.id = doc.id
                    employee?.let { employees.add(it) }
                }
                Log.d("EmployeeViewModel", "Employee IDs: " + employees.size)
            }.addOnFailureListener { exception ->

            }
    }


    fun loadPendingEmployees(groupId: String) {

    }

    fun loadUnlinkedEmployees() {}
}
