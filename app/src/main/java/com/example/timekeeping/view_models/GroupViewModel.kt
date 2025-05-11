package com.example.timekeeping.view_models

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.timekeeping.models.Group
import com.example.timekeeping.repositories.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val groupRepository: GroupRepository
) : ViewModel() {

    val groupId: String = savedStateHandle["groupId"] ?: ""

    val joinedGroups = mutableStateOf<List<Group>>(emptyList())
    val createdGroups = mutableStateOf<List<Group>>(emptyList())

    init {
        loadGroups()
    }

    private fun loadGroups() {
        // Load joined groups
        groupRepository.loadCreatedGroups { groups ->
            createdGroups.value = groups
        }

        // Load created groups
        groupRepository.loadJoinedGroups{ groups ->
            joinedGroups.value = groups
        }
    }

    fun searchGroupsByName(name: String) {
        if (name.isEmpty()) {
            loadGroups()
            return
        }

        createdGroups.value = createdGroups.value.filter { it.name.contains(name, ignoreCase = true) }
        joinedGroups.value = joinedGroups.value.filter { it.name.contains(name, ignoreCase = true) }
    }

    fun createGroup(group: Group) {
        groupRepository.createGroup(
            group,
            onSuccess = {}
        ) {
            loadGroups()  // Reload groups after creating a new one
        }
    }

    fun leaveGroup(groupId: String) {
        val currentUserId = groupRepository.auth.currentUser?.uid ?: return
        groupRepository.leaveGroup(groupId, currentUserId) {
            loadGroups()  // Reload groups after leaving
        }
    }

    fun getGroupById(groupId: String, onResult: (Group?) -> Unit) {
        groupRepository.getGroupById(groupId, onResult)
    }

    fun updateGroup(group: Group) {
        groupRepository.updateGroup(group) {
            loadGroups()  // Reload groups after updating
        }
    }

    fun deleteGroup(groupId: String) {
        groupRepository.deleteGroup(groupId) {
            loadGroups()  // Reload groups after deleting
        }
    }
}
