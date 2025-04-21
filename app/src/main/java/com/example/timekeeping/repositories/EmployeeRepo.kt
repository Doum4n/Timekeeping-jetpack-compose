package com.example.timekeeping.repositories

import android.util.Log
import com.example.timekeeping.models.Employee
import com.example.timekeeping.models.Employee_Group
import com.example.timekeeping.models.Group
import com.example.timekeeping.models.Role
import com.example.timekeeping.models.Salary
import com.example.timekeeping.models.Status
import com.example.timekeeping.ui.employees.form.TypeAllowance
import com.example.timekeeping.ui.employees.form.TypeDeduct
import com.example.timekeeping.utils.convertToReference
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.DateTime
import javax.inject.Inject

class EmployeeRepository @Inject constructor (
    private val db: FirebaseFirestore
) {

    val salaryTypes = listOf("Giờ", "Ca", "Tháng")

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
    ) {
        if (groupId.isBlank()) {
            Log.e("EmployeeRepository", "Invalid groupId: empty or null")
            return
        }

        db.collection("employee_group")
            .whereEqualTo("groupId", db.collection("groups").document(groupId))
            .whereEqualTo("status", status.toString())
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("EmployeeRepository", "Error loading employees", exception)
                    return@addSnapshotListener
                }

                val tasks = snapshot?.documents?.mapNotNull {
                    it.getDocumentReference("employeeId")?.get()
                }

                if (tasks != null) {
                    com.google.android.gms.tasks.Tasks.whenAllSuccess<com.google.firebase.firestore.DocumentSnapshot>(tasks)
                        .addOnSuccessListener { documents ->
                            val employees = documents.mapNotNull { doc ->
                                Log.d("EmployeeRepository", "Loaded employee: ${doc.id}")
                                doc.toObject(Employee::class.java)?.apply {
                                    id = doc.id
                                }
                            }
                            onResult(employees)
                        }
                        .addOnFailureListener { exception ->
                            Log.e("EmployeeRepository", "Error loading employees", exception)
                        }
                }
            }
    }

    fun saveEmployee(employee: Employee, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("employees").add(employee)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun loadEmployeeByShiftId(shiftId: String, onResult: (List<Employee>) -> Unit) {
        db.collection("assignments").whereEqualTo("shiftId", shiftId).get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    Log.e("EmployeeRepository", "No employee found for shiftId: $shiftId")
                    return@addOnSuccessListener
                }
                val employeeId = querySnapshot.documents.map {
                    it.getString("employeeId")
                }.toList()
                loadEmployeeById(employeeId, onResult)
            }.addOnFailureListener { exception ->
                Log.e("EmployeeRepository", "Error loading employee by shiftId", exception)
            }
    }

    private fun loadEmployeeById(employeesId: List<String?>, onResult: (List<Employee>) -> Unit) {
        db.collection("employees").whereIn(FieldPath.documentId(), employeesId).get()
            .addOnSuccessListener { document ->
                val employee = document.toObjects(Employee::class.java).toList()
                onResult(employee)
            }.addOnFailureListener { exception ->
                Log.e("EmployeeRepository", "Error loading employee by id", exception)
            }
    }

    fun requestJoinGroup(groupId: String, employeeId: String) {

    }

    fun acceptJoinGroup(groupId: String, employeeId: String) {

    }

    fun getSalaryById(
        employeeId: String,
        groupId: String,
        onSuccess: (Salary) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("salaries").whereEqualTo("employeeId", employeeId)
            .whereEqualTo("groupId", groupId).get()
            .addOnSuccessListener { document ->
                document.toObjects(Salary::class.java).forEach {
                    if(it.salaryType in salaryTypes) {
                        onSuccess(it)
                    }
                }
            }.addOnFailureListener { exception ->
                onFailure(exception)
                Log.e("EmployeeRepository", "Error loading salary", exception)
                exception.printStackTrace()
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
            val employeeGroupRef = db.collection("employee_group").document()
            val employeeId = employee.id.ifEmpty { db.collection("employees").document().id }
            val employeeRef = db.collection("employees").document(employeeId)

            val employeeData = Employee_Group(
//                employeeId = employeeRef,
//                groupId = groupId.convertToReference("groups"),
                status = Status.UNAUTHORIZED,
                isCreator = false,
                dayJoined = Timestamp.now().toDate(),
                role = Role.MEMBER
            )
            batch.set(employeeGroupRef, employeeData)

            val salaryRef = db.collection("salaries").document()
            val salaryData = hashMapOf(
                "employeeId" to employeeId,
                "groupId" to groupId,
                "salaryType" to employee.salaryType,
                "salary" to employee.salary,
                "createdAt" to Timestamp.now(),
                "approveAt" to Timestamp.now()
            )
            batch.set(salaryRef, salaryData)
            batch.set(employeeRef, employee.toMap())
        }

        batch.commit()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getEmployeeById(
        employeeId: String,
        onSuccess: (Employee) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("employees").document(employeeId).get()
            .addOnSuccessListener { document ->
                val employee = document.toObject(Employee::class.java)
                if (employee != null) {
                    employee.id = document.id
                    onSuccess(employee)
                    Log.d("EmployeeRepository", "Loaded employee: $employee")
                } else {
                    onFailure(Exception("Employee not found"))
                }
            }
    }

    fun updateEmployee(employee: Employee, salary: Salary) {
        db.collection("employees").document(employee.id).set(employee.toMap())
        db.collection("salaries").document(salary.id).set(salary)

    }

    fun getTotalOutstanding(employeeId: String, groupId: String, onSuccess: (Int) -> Unit, onFailure: (Exception) -> Unit){

        var totalOutstanding = 0;

        db.collection("salaries")
            .whereEqualTo("employeeId", employeeId)
            .whereEqualTo("groupId", groupId)
            .get()
            .addOnSuccessListener { document ->
                document.toObjects(Salary::class.java).forEach({salary ->
                    TypeDeduct.entries.forEach( { type ->
                        if (salary.salaryType == type.label) {
                            totalOutstanding += salary.salary
                        }
                    } )
                    TypeAllowance.entries.forEach({type ->
                        if (salary.salaryType == type.label) {
                            totalOutstanding += salary.salary
                        }
                    })
                })
                Log.d("EmployeeRepository", "Total outstanding: $totalOutstanding")
                onSuccess(totalOutstanding)
            }.addOnFailureListener { exception ->
                onFailure(exception)
                Log.e("EmployeeRepository", "Error loading salary", exception)
                exception.printStackTrace()
            }
    }
}