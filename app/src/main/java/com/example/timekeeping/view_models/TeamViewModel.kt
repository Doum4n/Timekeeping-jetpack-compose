package com.example.timekeeping.view_models

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.timekeeping.models.Employee
import com.example.timekeeping.models.Team
import com.example.timekeeping.repositories.TeamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val teamRepository: TeamRepository
) : ViewModel() {

    private val groupId = savedStateHandle.get<String>("groupId") ?: ""

    private val _teams = mutableStateOf<List<Team>>(emptyList())
    val teams = _teams

    private val _employees = MutableStateFlow<List<Employee>>(emptyList())
    val employees: StateFlow<List<Employee>> = _employees

    init {
        loadTeams()
    }

    fun loadTeams() {
        teamRepository.loadTeams(
            groupId = groupId,
            onSuccess = { _teams.value = it }
        )
    }

    fun createTeam(team: Team) {
        teamRepository.createTeam(team) {
            loadTeams()
        }
    }

    fun updateTeam(team: Team) {
        teamRepository.updateTeam(team) {
            loadTeams()
        }
    }

    fun deleteTeam(teamId: String, onSuccess: () -> Unit) {
        teamRepository.deleteTeam(teamId) {
            onSuccess()
            _teams.value = _teams.value.filter { it.id != teamId }
        }
    }

    // TODO: Lấy nhân viên theo team
    fun getEmployees(teamId: String? = "") {
        teamId?.let {
            teamRepository.getEmployees(it) { result ->
                _employees.value = result
            }
        } ?: run {
            _employees.value = emptyList() // Không có team → không có nhân viên
        }
    }
}