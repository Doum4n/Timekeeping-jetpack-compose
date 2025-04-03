package com.example.timekeeping.view_models

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.example.timekeeping.models.Employee
import com.example.timekeeping.repositories.EmployeeRepository
import com.example.timekeeping.utils.sendNotification

class EmployeeViewModel(
    groupId: String,
    private val employeeRepository: EmployeeRepository = EmployeeRepository()
) : ViewModel() {

    val employees = mutableStateListOf<Employee>()
    val pendingEmployees = mutableStateListOf<Employee>()
    val unlinkedEmployees = mutableStateListOf<Employee>()

    init {
        loadEmployees(groupId)
        loadPendingEmployees(groupId)
        loadUnlinkedEmployees()
    }

    // Load accepted employees for the given groupId
    private fun loadEmployees(groupId: String) {
        employeeRepository.loadEmployees(groupId) { employeesList ->
            employees.clear()
            employees.addAll(employeesList)
        }
    }

    fun requestJoinGroup(groupId: String, employeeId: String, context: Context) {
        employeeRepository.requestJoinGroup(groupId, employeeId)

        //sendNotification(context)
    }

    // Load pending employees (this method can be implemented further)
    fun loadPendingEmployees(groupId: String) {
        employeeRepository.loadPendingEmployees(groupId) { pendingList ->
            pendingEmployees.clear()
            pendingEmployees.addAll(pendingList)
        }
    }

    // Load unlinked employees (this method can be implemented further)
    fun loadUnlinkedEmployees() {
        employeeRepository.loadUnlinkedEmployees { unlinkedList ->
            unlinkedEmployees.clear()
            unlinkedEmployees.addAll(unlinkedList)
        }
    }
}
