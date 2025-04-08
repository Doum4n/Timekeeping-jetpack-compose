package com.example.timekeeping.view_models

import androidx.lifecycle.ViewModel
import com.example.timekeeping.models.Assignment
import com.example.timekeeping.repositories.AssignmentRepo

class AssignmentViewModel(
    private val employeeId: String,
    private val assignmentRepo: AssignmentRepo = AssignmentRepo()
): ViewModel() {

    fun getAssignments(callback: (List<Assignment>) -> Unit) {
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