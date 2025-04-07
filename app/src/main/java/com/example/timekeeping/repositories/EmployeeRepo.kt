package com.example.timekeeping.repositories

import android.util.Log
import com.example.timekeeping.models.Employee
import com.example.timekeeping.models.Status
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EmployeeRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {

    fun loadEmployees(groupId: String, onResult: (List<Employee>) -> Unit) {
        load(groupId, Status.ACCEPTED, onResult)
    }

    fun loadPendingEmployees(groupId: String, onResult: (List<Employee>) -> Unit) {
        load(groupId, Status.PENDING, onResult)
    }

    fun loadUnlinkedEmployees(groupId: String, onResult: (List<Employee>) -> Unit) {
        load(groupId, Status.UNAUTHORIZED, onResult)
    }

    private fun load(
        groupId: String,
        status: Status,
        onResult: (List<Employee>) -> Unit,
    ){
        if (groupId.isBlank()) {
            Log.e("EmployeeRepository", "Invalid groupId: empty or null")
            return
        }

        val groupRef = db.collection("groups").document(groupId)

        // Lấy creatorId trước
        groupRef.get().addOnSuccessListener { groupSnapshot ->
            // Lấy danh sách nhân viên đã ACCEPTED trong subcollection "employees"
            groupRef.collection("employees")
                .whereEqualTo("status", status)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val employeeList = querySnapshot.documents.map { doc ->
                        Employee(
                            id = doc.id,
                            fullName = doc.getString("name") ?: "",
                            status = status,
                            isCreator = false,
                        )
                    }

                    onResult(employeeList)
                }
                .addOnFailureListener { exception ->
                    Log.e("EmployeeRepository", "Error loading employees", exception)
                }
        }.addOnFailureListener { exception ->
            Log.e("EmployeeRepository", "Error getting group", exception)
        }
    }

    fun saveEmployee(employee: Employee, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("employees").add(employee)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun requestJoinGroup(groupId: String, employeeId: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val employeeData = Employee(
            userId = currentUserId,
            status = Status.PENDING,
            isCreator = false
        )

        db.collection("groups")
            .document(groupId)
            .collection("employees")
            .document()
            .set(employeeData)
            .addOnSuccessListener {
                Log.d("EmployeeRepository", "Join request sent for employeeId: $employeeId")
            }
            .addOnFailureListener { exception ->
                Log.e("EmployeeRepository", "Failed to request join group", exception)
            }
    }

    fun acceptJoinGroup(groupId: String, employeeId: String) {
        val employeeRef = db.collection("groups")
            .document(groupId)
            .collection("employees")
            .document(employeeId)

        employeeRef.update("status", Status.ACCEPTED.toString())
            .addOnSuccessListener {
                Log.d("EmployeeRepository", "Employee accepted into group: $employeeId")
            }
            .addOnFailureListener { exception ->
                Log.e("EmployeeRepository", "Error accepting join group", exception)
            }
    }

    fun getSalaryById(
        employeeId: String,
        groupId: String,
        onSuccess: (Double) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("groups")
            .document(groupId)
            .collection("employees")
            .document(employeeId)
            .collection("salaries").get().addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    onFailure(Exception("No salary found for the employee"))
                    return@addOnSuccessListener
                }
                val salary = querySnapshot.documents.first().getDouble("salary") ?: 0.0
                Log.d("EmployeeRepository", "Loaded salary: $salary")
                onSuccess(salary)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun saveEmployees(
        employees: List<Employee>,
        groupId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val batch = db.batch()
        val groupRef = db.collection("groups").document(groupId)

        employees.forEach { employee ->
            // Tạo document cho nhân viên trong subcollection "employees" của group
            val employeeId = employee.id.ifEmpty { db.collection("employees").document().id }
            val employeeRef = groupRef.collection("employees").document(employeeId)

            val employeeData = hashMapOf(
                "name" to employee.fullName,
                "status" to Status.UNAUTHORIZED.toString(),
                "isCreator" to false
            )
            batch.set(employeeRef, employeeData)

            // Tạo document lương trong subcollection "salaries" của nhân viên
            val salaryRef = employeeRef.collection("salaries").document()
            val salaryData = hashMapOf(
                "salaryType" to employee.salaryType,
                "salary" to employee.salary,
                "createdAt" to Timestamp.now(),
                "approveAt" to Timestamp.now()
            )
            batch.set(salaryRef, salaryData)
        }

        batch.commit()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

}