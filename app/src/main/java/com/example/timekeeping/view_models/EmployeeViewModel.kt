package com.example.timekeeping.view_models

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.example.timekeeping.models.Employee
import com.example.timekeeping.repositories.EmployeeRepository
import com.example.timekeeping.utils.sendNotification

class EmployeeViewModel(
    private val groupId: String = "",
    private val employeeRepository: EmployeeRepository = EmployeeRepository()
) : ViewModel() {

    val employees = mutableStateOf<List<Employee>>(emptyList())
    val pendingEmployees = mutableStateOf<List<Employee>>(emptyList())
    val unlinkedEmployees = mutableStateOf<List<Employee>>(emptyList())

    init {
        load()
    }

    private fun load(){
        loadEmployees(groupId)
        loadPendingEmployees(groupId)
        loadUnlinkedEmployees()
    }

    fun saveEmployee(employee: Employee, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        employeeRepository.saveEmployee(employee, onSuccess, onFailure)
    }

    fun saveEmployees(employees: List<Employee>, groupId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        employeeRepository.saveEmployees(employees, groupId, onSuccess, onFailure)
    }

    // Load accepted employees for the given groupId
    private fun loadEmployees(groupId: String) {
        employeeRepository.loadEmployees(groupId) { employeesList ->
            employees.value = employeesList
        }
    }

    fun requestJoinGroup(groupId: String, employeeId: String, context: Context) {
        employeeRepository.requestJoinGroup(groupId, employeeId)

        //sendNotification(context)
    }

    // Load pending employees (this method can be implemented further)
    private fun loadPendingEmployees(groupId: String) {
        employeeRepository.loadPendingEmployees(groupId) { pendingList ->
           pendingEmployees.value = pendingList
        }
    }

    // Load unlinked employees (this method can be implemented further)
    private fun loadUnlinkedEmployees() {
        employeeRepository.loadUnlinkedEmployees(groupId) { unlinkedList ->
            unlinkedEmployees.value = unlinkedList
        }
    }

    fun acceptJoinGroup(groupId: String, employeeId: String) {
        employeeRepository.acceptJoinGroup(groupId, employeeId)
    }

    fun getSalaryById(employeeId: String,groupId: String, onSuccess: (Double) -> Unit, onFailure: (Exception) -> Unit){
        load()
        employeeRepository.getSalaryById(employeeId, groupId, onSuccess, onFailure)
    }

    fun searchEmployeesByName(searchText: String) {
        if (searchText.isEmpty()) {
            loadEmployees(groupId)
            loadPendingEmployees(groupId)
            loadUnlinkedEmployees()
            return
        }

        employees.value = employees.value.filter{ it.fullName.contains(searchText, ignoreCase = true) }
        pendingEmployees.value = pendingEmployees.value.filter { it.fullName.contains(searchText, ignoreCase = true) }
        unlinkedEmployees.value = unlinkedEmployees.value.filter { it.fullName.contains(searchText, ignoreCase = true) }
    }

    fun loadEmployeeByShiftId(shiftId: String, onSuccess: (List<Employee>) -> Unit){
        employeeRepository.loadEmployeeByShiftId(shiftId, onSuccess)
    }
}
