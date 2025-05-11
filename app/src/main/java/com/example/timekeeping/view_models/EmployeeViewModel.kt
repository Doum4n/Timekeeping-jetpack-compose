package com.example.timekeeping.view_models

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.timekeeping.models.Employee
import com.example.timekeeping.models.Salary
import com.example.timekeeping.models.Team
import com.example.timekeeping.repositories.EmployeeRepository
import com.example.timekeeping.utils.sendNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val employeeRepository: EmployeeRepository
) : ViewModel() {

    val groupId: String = savedStateHandle.get<String>("groupId") ?: ""

    val employees = mutableStateOf<List<Employee>>(emptyList())
    val pendingEmployees = mutableStateOf<List<Employee>>(emptyList())
    val unlinkedEmployees = mutableStateOf<List<Employee>>(emptyList())

    init {
        load()
    }

    private fun load(){
        loadEmployees()
        loadPendingEmployees()
        loadUnlinkedEmployees()
    }

    fun getEmployeeById(employeeId: String, onSuccess: (Employee) -> Unit, onFailure: (Exception) -> Unit) {
        employeeRepository.getEmployeeById(employeeId, onSuccess, onFailure)
    }

    fun saveEmployee(employee: Employee, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        employeeRepository.saveEmployee(employee, onSuccess, onFailure)
    }

    fun saveEmployees(employees: List<Employee>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        employeeRepository.saveEmployees(employees, groupId, onSuccess, onFailure)
    }

    fun deleteEmployee(groupId: String, employeeId: String) {
        employeeRepository.deleteEmployee(groupId, employeeId)
    }

    fun getRole(employeeId: String, groupId: String, onResult: (String) -> Unit) {
        employeeRepository.getRoleByUserId(employeeId, groupId, onResult)
    }

    // Load accepted employees for the given groupId
    private fun loadEmployees() {
        employeeRepository.loadEmployees(groupId) { employeesList ->
            employees.value = employeesList
        }
    }

    fun requestJoinGroup(employeeId: String, groupId: String) {
        employeeRepository.requestJoinGroup(groupId, employeeId)

        //sendNotification(context)
    }

    // Load pending employees (this method can be implemented further)
    private fun loadPendingEmployees() {
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

    fun getSalaryById(employeeId: String,groupId: String, onSuccess: (Salary) -> Unit, onFailure: (Exception) -> Unit){
        load()
        employeeRepository.getSalaryById(employeeId, groupId, onSuccess, onFailure)
    }

    fun searchEmployeesByName(searchText: String) {
        if (searchText.isEmpty()) {
            loadEmployees()
            loadPendingEmployees()
            loadUnlinkedEmployees()
            return
        }

        employees.value = employees.value.filter{ it.name.fullName.contains(searchText, ignoreCase = true) }
        pendingEmployees.value = pendingEmployees.value.filter { it.name.fullName.contains(searchText, ignoreCase = true) }
        unlinkedEmployees.value = unlinkedEmployees.value.filter { it.name.fullName.contains(searchText, ignoreCase = true) }
    }

    fun loadEmployeeByShiftId(shiftId: String, onSuccess: (List<Employee>) -> Unit){
        employeeRepository.loadEmployeeByShiftId(shiftId, onSuccess)
    }

    fun updateEmployee(employee: Employee, salary: Salary) {
        employeeRepository.updateEmployee(employee, salary)
    }

    fun updateEmployee(employee: Employee) {
        employeeRepository.updateEmployee(employee)
    }

    fun grantPermission(groupId: String, employeeId: String, scannedResult: String?) {
        employeeRepository.grantPermission(groupId, employeeId, scannedResult)
    }

    fun getName(employeeId: String, onSuccess: (String) -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        employeeRepository.getName(employeeId, onSuccess, onFailure)
    }

    fun loadTeamById(teamId: String, result: (Team) -> Unit ) {
        employeeRepository.loadTeamById(teamId, result)
    }

    fun deleteTeam(teamId: String) {
        employeeRepository.deleteTeam(teamId)
    }
}
