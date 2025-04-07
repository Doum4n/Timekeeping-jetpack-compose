package com.example.timekeeping.view_models

import androidx.lifecycle.ViewModel
import com.example.timekeeping.models.Assignment
import com.example.timekeeping.repositories.AssignmentRepo

class AssignmentViewModel(
    private val employeeId: String,
    private val assignmentRepo: AssignmentRepo
): ViewModel() {

    fun getAssignments() {
        assignmentRepo.getAssignments(employeeId = employeeId)
    }

    fun addAssignment(assignment: Assignment) {
        assignmentRepo.addAssignment(assignment = assignment)
    }

}