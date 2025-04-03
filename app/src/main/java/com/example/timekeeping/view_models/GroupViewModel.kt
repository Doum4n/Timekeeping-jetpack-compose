package com.example.timekeeping.view_models

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.timekeeping.models.Group
import com.example.timekeeping.models.Group.Companion.fromDocument
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore

// HomeViewModel.kt
class GroupViewModel(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    val joinedGroups = mutableStateListOf<Group>()
    val createdGroups = mutableStateListOf<Group>()

    init {
        loadGroups()
    }

    fun loadGroups() {

        val currentUserId = auth.currentUser?.uid ?: return

        // TODO sửa lại
        // Load joined groups
        db.collection("employee_group").whereEqualTo("employeeId", currentUserId)
            .get()
            .addOnSuccessListener { query ->
                val groupIds = query.documents.map { it.getString("groupId") ?: "" }
                loadGroupDetails(groupIds, joinedGroups)
            }

        // Load created groups
        db.collection("groups")
            .whereEqualTo("creator_id", currentUserId)
            .get()
            .addOnSuccessListener { query ->
                createdGroups.clear()
                for (doc in query.documents) {
                    val group = doc.toObject(Group::class.java)
                    fromDocument(doc)?.let { createdGroups.add(it) }
                }
            }
    }

    private fun loadGroupDetails(ids: List<String>, targetList: MutableList<Group>) {
        db.collection("groups")
            .whereIn(FieldPath.documentId(), ids)
            .get()
            .addOnSuccessListener { query ->
                targetList.clear()
                for (doc in query.documents) {
                    val group = doc.toObject(Group::class.java)
                    group?.id = doc.id
                    fromDocument(doc)?.let { targetList.add(it) }
                }
            }
    }

    // TODO
    fun leaveGroup(groupId: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        db.collection("employee_group")
            .whereEqualTo("employeeId", currentUserId)
            .whereEqualTo("groupId", groupId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    return@addOnSuccessListener
                }
                for (document in documents) {
                    document.reference.delete()
                }
                loadGroups()
            }.addOnFailureListener { e ->

            }
    }

    fun createGroup(group: Group) {
        db.collection("groups").add(group)
            .addOnSuccessListener { v ->
                run {
                    group.id = v.id
                    createdGroups.add(group)
                    JoinGroup(group.id, auth.currentUser?.uid ?: "")
                }
            }
    }

    private fun JoinGroup(groupId: String, userId: String) {
        val employeeGroup = hashMapOf(
            "employeeId" to userId,
            "groupId" to groupId,
            "status" to "accepted"
        )
        db.collection("employee_group").add(employeeGroup)
            .addOnSuccessListener {
                loadGroups()
            }.addOnFailureListener { e ->

            }
    }
}