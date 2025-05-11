package com.example.timekeeping.view_models

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.timekeeping.models.Employee
import com.example.timekeeping.models.Shift
import com.example.timekeeping.repositories.ShiftRepository
import com.example.timekeeping.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ShiftViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val shiftRepository: ShiftRepository
) : ViewModel() {

    val groupId: String = savedStateHandle["groupId"] ?: ""
    val shiftId: String = savedStateHandle["shiftId"] ?: ""

    private val _shifts = mutableStateOf<List<Shift>>(emptyList())
    val shifts = _shifts

    private var _employees = mutableStateOf<List<Employee>>(emptyList())
    val employees = _employees

    init {
        loadShifts()

        Log.d("ShiftViewModel", "groupId: $groupId, shiftId: $shiftId")
    }

    private fun loadShifts() {
        shiftRepository.loadShifts(groupId) { shiftsList ->
            _shifts.value = shiftsList // Gán danh sách Shift vào _shifts
        }
    }

    fun create(shift: Shift) {
        shiftRepository.createShift(shift) {
            loadShifts() // Reload shifts after creation
        }
    }

    fun update(shift: Shift) {
        shiftRepository.updateShift(shiftId, shift) {
            loadShifts() // Reload shifts after update
        }
    }

    fun delete(shiftId: String) {
        shiftRepository.deleteShift(shiftId) {
            _shifts.value = _shifts.value.filter { it.id != shiftId } // Remove deleted shift from list
        }
    }

    fun getShiftById(callback: (Shift) -> Unit) {
        shiftRepository.getShiftById(shiftId) { shift ->
            shift?.let { callback(it) }
        }
    }

    fun getOnGoingShift(callback: (List<Shift>) -> Unit) {
        shiftRepository.getOnGoingShift(SessionManager.getEmployeeId().toString(), groupId) { shift ->
            callback(shift)
        }
    }

    fun loadEmployees(_shiftId: String = "", day: Int) {
//        if(_shiftId == ""){
//            shiftRepository.loadEmployees(shifts.value.first().id) { employeesList ->
//                _employees.value = employeesList
//            }
//        }else{
            shiftRepository.loadEmployees(_shiftId, day) { employeesList ->
                _employees.value = employeesList
            }
//        }
    }
}
