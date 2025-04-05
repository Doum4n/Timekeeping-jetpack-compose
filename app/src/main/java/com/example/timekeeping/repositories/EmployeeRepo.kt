package com.example.timekeeping.repositories

import android.util.Log
import com.example.timekeeping.models.Employee
import com.example.timekeeping.models.Status
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.database.FirebaseDatabase

class EmployeeRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {

    fun saveEmployee(employee: Employee, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("employees").add(employee)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    // Load accepted employees by groupId
    fun loadEmployees(groupId: String, onResult: (List<Employee>) -> Unit) {
        db.collection("employee_group")
            .whereEqualTo("groupId", groupId)
            .whereEqualTo("status", Status.ACCEPTED.toString())
            .get()
            .addOnSuccessListener { query ->
                val employeeIds = query.documents.mapNotNull { it.getString("employeeId") }.filter { it.isNotEmpty() }
                loadEmployeeDetails(employeeIds, false, onResult)
            }.addOnFailureListener { exception ->

            }
    }

    // Load employee details using employeeIds
    private fun loadEmployeeDetails(
        employeeIds: List<String>,
        hasUserId: Boolean = false, // tài khoản liên kết hay chưa
        onResult: (List<Employee>) -> Unit
    ) {
        val field: FieldPath = if (hasUserId) FieldPath.of("userId") else FieldPath.documentId()

        // Nếu danh sách rỗng, trả về danh sách rỗng ngay lập tức
        // để tránh lỗi khi gọi whereIn với danh sách rỗng
        if (employeeIds.isEmpty()){
            onResult(emptyList())
            return
        }

        db.collection("employees")
            .whereIn(field, employeeIds)
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
                // Bị dừng ngay lập tức khi có lỗi
                Log.e("EmployeeRepository", "Error loading employee details", exception)
            }
    }


    fun requestJoinGroup(groupId: String, employeeId: String) {
        val employeeGroup = hashMapOf(
            "employeeId" to employeeId,
            "groupId" to groupId,
            "status" to Status.PENDING.toString()
        )

        db.collection("employee_group").add(employeeGroup)
    }

    fun acceptJoinGroup(groupId: String, employeeId: String) {
        db.collection("employee_group")
            .document(FieldPath.documentId().toString())
            .update("status", Status.ACCEPTED.toString())
    }

    // Load pending employees (tbd)
    fun loadPendingEmployees(groupId: String, onResult: (List<Employee>) -> Unit) {
        db.collection("employee_group")
            .whereEqualTo("groupId", groupId)
            .whereEqualTo("status", Status.PENDING.toString())
            .addSnapshotListener { query, error ->
                if (error != null) {
                    Log.e("EmployeeRepository", "Error loading pending employees", error)
                    return@addSnapshotListener
                }
                val employeeIds = query?.documents?.map { it.getString("employeeId") ?: "" } ?: emptyList()
                loadEmployeeDetails(employeeIds,true, onResult)
            }
    }

    fun loadUnlinkedEmployees(groupId: String, onResult: (List<Employee>) -> Unit) {
        db.collection("employee_group")
            .whereEqualTo("groupId", groupId)
            .whereEqualTo("status", Status.UNAUTHORIZED.toString())
            .addSnapshotListener { query, error ->
                if (error != null) {
                    Log.e("EmployeeRepository", "Error loading pending employees", error)
                    return@addSnapshotListener
                }
                val employeeIds = query?.documents?.map { it.getString("employeeId") ?: "" } ?: emptyList()
                loadEmployeeDetails(employeeIds,false, onResult)
            }
    }

    fun getSalaryById(employeeId: String, onSuccess: (Double) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("salaries")
            .whereEqualTo("employeeId", employeeId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Kiểm tra nếu có tài liệu trả về từ truy vấn
                if (querySnapshot.isEmpty) {
                    onFailure(Exception("No salary found for the employee"))
                    return@addOnSuccessListener
                }

                val salary = querySnapshot.documents.first().getDouble("salary") ?: 0.0
                Log.d("EmployeeRepository", "Loaded salary: $salary")
                return@addOnSuccessListener onSuccess(salary)
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

        employees.forEach { employee ->
            val docRef = db.collection("employees").document()
            // Lưu bản sao không có các thuộc tính bị loại trừ
            batch.set(docRef, employee.toMap())

            val employeeId = employee.id.ifEmpty { docRef.id }

            val salaryRef = db.collection("salaries").document()
            val salary = hashMapOf(
                "employeeId" to employeeId,
                "salaryType" to employee.salaryType,
                "salary" to employee.salary,
                "createdAt" to com.google.firebase.Timestamp.now()
            )
            batch.set(salaryRef, salary)

            val employeeGroupRef = db.collection("employee_group").document()
            val employeeGroup = hashMapOf(
                "employeeId" to employeeId,
                "groupId" to groupId,
                "status" to Status.UNAUTHORIZED.toString()
            )
            batch.set(employeeGroupRef, employeeGroup)
        }

        batch.commit()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

}