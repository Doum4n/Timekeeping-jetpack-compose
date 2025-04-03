package com.example.timekeeping.view_models

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.timekeeping.models.Shift
import com.example.timekeeping.repositories.ShiftRepository

class ShiftViewModel(
    groupId: String,
    private val shiftRepository: ShiftRepository = ShiftRepository()
) : ViewModel() {

    private val _shifts = mutableStateOf<List<Shift>>(emptyList())
    val shifts = _shifts

    init {
        loadShifts(groupId)
    }

    private fun loadShifts(groupId: String) {
        shiftRepository.loadShifts(groupId) { shiftsList ->
            _shifts.value = shiftsList // Gán danh sách Shift vào _shifts
        }
    }

    fun create(shift: Shift) {
        shiftRepository.createShift(shift) {
            loadShifts(shift.groupId) // Reload shifts after creation
        }
    }

    fun update(shiftId: String, shift: Shift) {
        shiftRepository.updateShift(shiftId, shift) {
            loadShifts(shift.groupId) // Reload shifts after update
        }
    }

    fun delete(shiftId: String) {
        shiftRepository.deleteShift(shiftId) {
            _shifts.value = _shifts.value.filter { it.id != shiftId } // Remove deleted shift from list
        }
    }

    fun getShiftById(shiftId: String, callback: (Shift) -> Unit) {
        shiftRepository.getShiftById(shiftId) { shift ->
            shift?.let { callback(it) }
        }
    }
}
