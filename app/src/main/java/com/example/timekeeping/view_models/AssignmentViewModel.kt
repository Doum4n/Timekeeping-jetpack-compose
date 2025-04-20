package com.example.timekeeping.view_models

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.timekeeping.models.Assignment
import com.example.timekeeping.repositories.AssignmentRepo
import com.google.firebase.firestore.DocumentReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AssignmentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val assignmentRepo: AssignmentRepo
): ViewModel() {

    fun getAssignments(employeeId: String, callback: (List<Assignment>) -> Unit) {
        assignmentRepo.getAssignments(
            employeeId = employeeId,
            callback = callback
        )
    }

    fun addAssignment(assignment: Assignment) {
        assignmentRepo.addAssignment(assignment = assignment)
    }

    fun updateAssignment(assignmentId: String, assignment: Assignment) {
        assignmentRepo.updateAssignment(
            assignment = assignment,
            assignmentId = assignmentId
        )
    }
}