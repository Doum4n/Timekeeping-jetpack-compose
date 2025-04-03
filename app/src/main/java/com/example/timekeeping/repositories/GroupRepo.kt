package com.example.timekeeping.repositories

import com.example.timekeeping.models.Group
import com.example.timekeeping.models.Group.Companion.fromDocument
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldPath

class GroupRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val currentUserId = auth.currentUser?.uid ?: ""

    // Load joined groups
    fun loadJoinedGroups(onResult: (List<Group>) -> Unit) {
        db.collection("employee_group").whereEqualTo("employeeId", currentUserId)
            .get()
            .addOnSuccessListener { query ->
                val groupIds = query.documents.mapNotNull { it.getString("groupId") }
                loadGroupDetails(groupIds, onResult)
            }
    }

    // Unified method to load group details
    fun loadGroupDetails(groupIds: List<String>, onResult: (List<Group>) -> Unit) {
        if (groupIds.isEmpty()) {
            onResult(emptyList())
            return
        }

        db.collection("groups")
            .whereIn(FieldPath.documentId(), groupIds)
            .get()
            .addOnSuccessListener { document ->
                val groups = document.documents.mapNotNull { doc ->
                    doc.toObject(Group::class.java)?.apply { id = doc.id }
                }
                onResult(groups)
            }
            .addOnFailureListener { exception ->
                onResult(emptyList())  // Handle error by returning empty list
            }
    }

    // Load created groups
    fun loadCreatedGroups(userId: String, onResult: (List<Group>) -> Unit) {
        db.collection("groups")
            .whereEqualTo("creatorId", userId)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                val newGroups = snapshots?.documents?.mapNotNull { doc ->
                    fromDocument(doc)
                } ?: emptyList()
                onResult(newGroups)
            }
    }

    // Create a new group
    fun createGroup(group: Group, onSuccess: () -> Unit) {
        db.collection("groups").add(group)
            .addOnSuccessListener {
                onSuccess()
            }
    }

    // Join group
    fun joinGroup(groupId: String, userId: String, onSuccess: () -> Unit) {
        val employeeGroup = hashMapOf(
            "employeeId" to userId,
            "groupId" to groupId
        )
        db.collection("employee_group").add(employeeGroup)
            .addOnSuccessListener {
                onSuccess()
            }
    }

    // Leave group
    fun leaveGroup(groupId: String, userId: String, onSuccess: () -> Unit) {
        db.collection("employee_group")
            .whereEqualTo("employeeId", userId)
            .whereEqualTo("groupId", groupId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    return@addOnSuccessListener
                }
                for (document in documents) {
                    document.reference.delete()
                }
                onSuccess()
            }
    }

    // Search groups by name (For both joined and created)
    fun searchGroupsByName(name: String, onResult: (List<Group>) -> Unit) {

    }
}
