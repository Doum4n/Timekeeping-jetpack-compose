package com.example.timekeeping.repositories

import android.util.Log
import com.example.timekeeping.models.Adjustment
import com.example.timekeeping.models.Employee
import com.example.timekeeping.models.Employee_Group
import com.example.timekeeping.models.Group
import com.example.timekeeping.models.Name
import com.example.timekeeping.models.Role
import com.example.timekeeping.models.Salary
import com.example.timekeeping.models.Status
import com.example.timekeeping.models.Team
import com.example.timekeeping.utils.convertToReference
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.getField
import com.google.type.DateTime
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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

    fun deleteEmployee(groupId: String, employeeId: String) {
        val batch = db.batch()

//        val employeeRef = db.collection("employees").document(employeeId)
//        val salaryRef = db.collection("salaries").document(salaryDocId(groupId, employeeId))
        val empGroupRef = db.collection("employee_group").document("$groupId-$employeeId")

//        batch.delete(employeeRef)
        batch.delete(empGroupRef)
//        batch.delete(salaryRef)

//        val assignmentsTask = db.collection("assignments")
//            .whereEqualTo("employeeId", employeeId.convertToReference("employees"))
//            .get()

        val attendancesTask = db.collection("attendances")
            .whereEqualTo("employeeId", employeeId.convertToReference("employees"))
            .get()

        Tasks.whenAllSuccess<QuerySnapshot>(attendancesTask)
            .addOnSuccessListener { results ->
                val assignments = results[0].documents
                val attendances = results[1].documents

                for (doc in assignments) {
                    batch.delete(doc.reference)
                }

                for (doc in attendances) {
                    batch.delete(doc.reference)
                }

                batch.commit()
                    .addOnSuccessListener {
                        Log.d("Firestore", "Employee and related data deleted successfully.")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error committing batch", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching documents for delete", e)
            }
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
                    Tasks.whenAllSuccess<DocumentSnapshot>(tasks)
                        .addOnSuccessListener { documents ->
                            val employees = documents.mapNotNull { doc ->
                                Log.d("EmployeeRepository", "Loaded employee: $doc")
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
        db.collection("employee_group").document(employeeGroupDocId(groupId, employeeId))
            .set(
                mapOf(
                    "employeeId" to employeeId.convertToReference("employees"),
                    "groupId" to groupId.convertToReference("groups"),
                    "status" to Status.PENDING.toString(),
                    "isCreator" to false,
                )
            ).addOnSuccessListener {
                Log.d("EmployeeRepository_requestJoinGroup", "Request join group success")
            }.addOnFailureListener { e ->
                Log.e("EmployeeRepository_requestJoinGroup", "Request join group failed", e)
            }
    }

    fun acceptJoinGroup(groupId: String, employeeId: String) {
        db.collection("employee_group").document(employeeGroupDocId(groupId, employeeId))
            .update("status", Status.ACCEPTED.toString())
            .addOnSuccessListener {
                Log.d("EmployeeRepository_acceptJoinGroup", "Accept join group success")
            }.addOnFailureListener {
                Log.e("EmployeeRepository_acceptJoinGroup", "Accept join group failed", it)
            }
    }

    fun getSalaryById(
        employeeId: String,
        groupId: String,
        onSuccess: (Salary) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("salaries")
            .whereEqualTo("employeeId", employeeId)
            .whereEqualTo("groupId", groupId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.first()
                    val salary = doc.toObject(Salary::class.java)
                    if (salary.salaryType in salaryTypes) {
                        salary.id = doc.id
                        onSuccess(salary)
                    } else {
                        onFailure(Exception("Invalid salary type"))
                    }
                } else {
                    val salary = Salary(
                        employeeId = employeeId,
                        groupId = groupId,
                    )
                    onSuccess(salary)
                    onFailure(NoSuchElementException("No salary found for employeeId=$employeeId and groupId=$groupId"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
                Log.e("EmployeeRepository", "Error loading salary", exception)
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
            val employeeId = db.collection("employees").document().id
            val employeeGroupRef = db.collection("employee_group").document(employeeGroupDocId(groupId, employeeId))
            val employeeRef = db.collection("employees").document(employeeId)

            val employeeData = Employee_Group(
                employeeId = employeeRef,
                groupId = groupId.convertToReference("groups"),
                status = Status.UNAUTHORIZED,
                isCreator = false,
                dayJoined = Timestamp.now().toDate(),
                role = Role.EMPLOYEE
            )
            batch.set(employeeGroupRef, employeeData.toMap())

            val salaryRef = db.collection("salaries").document(salaryDocId(groupId, employeeId))
            val salaryData = hashMapOf(
                "salaryType" to employee.salaryType,
                "employeeId" to employeeId,
                "groupId" to groupId,
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
                    Log.d("EmployeeRepository_getEmployeeById", "Loaded employee: $employee")
                } else {
                    onFailure(Exception("Employee not found"))
                }
            }
    }

    fun updateEmployee(employee: Employee, groupId: String, salary: Salary) {
        Log.d("EmployeeRepository_updateEmployee", "Updating employee: $employee, salary: $salary")
        db.collection("employees").document(employee.id).set(employee.toMap())
        if(salary.id == ""){
            db.collection("salaries").document(salaryDocId(groupId, employee.id)).set(salary)
        }else{
            db.collection("salaries").document(salary.id).set(salary)
        }
    }

    fun updateEmployee(employee: Employee) {
        Log.d("EmployeeRepository_updateEmployee", "Updating employee: $employee")
        db.collection("employees").document(employee.id).set(employee.toMap())
    }

    private fun employeeGroupDocId(groupId: String, employeeId: String): String {
        return "$groupId-$employeeId"
    }

    fun grantPermission(groupId: String, employeeId: String, scannedResult: String?) {

        val batch = db.batch()

        val employeeGroupRef = db.collection("employee_group").document(employeeGroupDocId(groupId, employeeId))
        val employeeRef = db.collection("employees").document(employeeId)

        batch.update(employeeGroupRef, "status", Status.ACCEPTED.toString())
        batch.update(employeeRef, "userId", scannedResult)
        batch.commit()
            .addOnSuccessListener {
                Log.d("EmployeeRepository_grantPermission", "Batch update success")
            }
            .addOnFailureListener { e ->
                Log.e("EmployeeRepository_grantPermission", "Batch update failed", e)
            }


        Log.d("EmployeeRepository_grantPermission", "Granted permission for employee: $employeeId")
    }

    fun getName(employeeId: String, onSuccess: (String) -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        db.collection("employees")
            .document(employeeId)
            .get()
            .addOnSuccessListener({
                it.getField<Name>("name")?.let { it1 -> onSuccess(it1.fullName) }
            }).addOnFailureListener({
                onFailure(it)
            })
    }

    fun loadTeamById(teamId: String, result: (Team) -> Unit) {
        db.collection("teams").document(teamId).get()
            .addOnSuccessListener { document ->
                val team = document.toObject(Team::class.java)
                if (team != null) {
                    team.id = document.id
                    result(team)
                    Log.d("EmployeeRepository_loadTeamById", "Loaded team: $team")
                } else {
                    Log.e("EmployeeRepository_loadTeamById", "Team not found")
                }
            }
    }

    fun deleteTeam(teamId: String) {
        db.collection("teams").document(teamId).delete()
    }

    fun getRoleByUserId(employeeId: String, groupId: String, onResult: (String) -> Unit) {
        db.collection("employee_group")
            .whereEqualTo("groupId", groupId.convertToReference("groups"))
            .whereEqualTo("employeeId", employeeId.convertToReference("employees"))
            .get()
            .addOnSuccessListener { querySnapshot ->
                val role = querySnapshot.documents.firstOrNull()?.getString("role")
                onResult(role.toString())
            }.addOnFailureListener { exception ->
                Log.e("EmployeeRepository_getRoleByUserId", "Error getting role by userId", exception)
            }

    }

    fun acceptEmployee(employeeId: String, groupId: String, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        db.collection("employee_group")
            .whereEqualTo("groupId", groupId.convertToReference("groups"))
            .whereEqualTo("employeeId", employeeId.convertToReference("employees"))
            .get()
            .addOnSuccessListener { querySnapshot ->
                val document = querySnapshot.documents.firstOrNull()
                document?.reference?.update("status", Status.ACCEPTED.toString())
                onSuccess()
            }.addOnFailureListener { exception ->
                Log.e("EmployeeRepository_acceptEmployee", "Error accepting employee", exception)
                onFailure(exception)
            }
    }

    fun rejectEmployee(employeeId: kotlin.String, groupId: kotlin.String, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        db.collection("employee_group")
            .whereEqualTo("groupId", groupId.convertToReference("groups"))
            .whereEqualTo("employeeId", employeeId.convertToReference("employees"))
            .get()
            .addOnSuccessListener { querySnapshot ->
                val document = querySnapshot.documents.firstOrNull()
                document?.reference?.update("status", Status.REJECTED.toString())
                onSuccess()
            }.addOnFailureListener { exception ->
                Log.e("EmployeeRepository_rejectEmployee", "Error rejecting employee", exception)
                exception.printStackTrace()
                onFailure(exception)
            }
    }
}