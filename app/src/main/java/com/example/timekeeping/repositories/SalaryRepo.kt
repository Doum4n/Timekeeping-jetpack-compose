package com.example.timekeeping.repositories

import com.example.timekeeping.models.Salary
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class SalaryRepo @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun createSalary(salary: Salary, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("salaries").add(salary)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getAdvanceMoney(employeeId: String, onSuccess: (List<Salary>) -> Unit, onFailure: (Exception) -> Unit = {}){
        firestore.collection("salaries")
            .whereEqualTo("employeeId", employeeId)
            .whereEqualTo("salaryType", "Tiền ứng")
            .get()
            .addOnSuccessListener { documents ->
                val salaries = documents.toObjects(Salary::class.java)
                onSuccess(salaries)
            }
            .addOnFailureListener { onFailure(it) }
    }
}