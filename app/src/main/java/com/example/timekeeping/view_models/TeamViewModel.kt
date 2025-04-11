package com.example.timekeeping.view_models

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.timekeeping.models.Team
import com.example.timekeeping.repositories.TeamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val teamRepository: TeamRepository
) : ViewModel() {

    private val groupId = savedStateHandle.get<String>("groupId") ?: ""

    private val _teams = mutableStateOf<List<Team>>(emptyList())
    val teams = _teams

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

    fun updateTeam(teamId: String, team: Team) {
        teamRepository.updateTeam(teamId, team) {
            loadTeams()
        }
    }

    fun deleteTeam(teamId: String) {
        teamRepository.deleteTeam(teamId) {
            _teams.value = _teams.value.filter { it.id != teamId }
        }
    }
}