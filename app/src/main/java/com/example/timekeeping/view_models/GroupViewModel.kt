package com.example.timekeeping.view_models

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.timekeeping.models.Group
import com.example.timekeeping.repositories.GroupRepository

class GroupViewModel(
    private val groupRepository: GroupRepository = GroupRepository()
) : ViewModel() {

    val joinedGroups = mutableStateOf<List<Group>>(emptyList())
    val createdGroups = mutableStateOf<List<Group>>(emptyList())

    init {
        loadGroups()
    }

    fun loadGroups() {
        val currentUserId = groupRepository.auth.currentUser?.uid ?: return

        // Load joined groups
        groupRepository.loadCreatedGroups(currentUserId) { groups ->
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
        groupRepository.createGroup(group) {
            loadGroups()  // Reload groups after creating a new one
        }
    }

    fun leaveGroup(groupId: String) {
        val currentUserId = groupRepository.auth.currentUser?.uid ?: return
        groupRepository.leaveGroup(groupId, currentUserId) {
            loadGroups()  // Reload groups after leaving
        }
    }

    // Gọi khi quản lý nhấn chấp nhận yêu cầu tham gia nhóm của nhân viên
    fun joinGroup(groupId: String) {
        val currentUserId = groupRepository.auth.currentUser?.uid ?: return
        groupRepository.joinGroup(groupId, currentUserId) {
            loadGroups()  // Reload groups after joining
        }
    }
}
