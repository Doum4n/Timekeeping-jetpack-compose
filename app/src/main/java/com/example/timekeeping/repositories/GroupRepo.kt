package com.example.timekeeping.repositories

import android.util.Log
import com.example.timekeeping.models.Group
import com.example.timekeeping.models.Group.Companion.fromDocument
import com.example.timekeeping.models.Status
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GroupRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val currentUserId = auth.currentUser?.uid ?: ""

    // Load joined groups (trạng thái ACCEPTED)
    fun loadJoinedGroups(onResult: (List<Group>) -> Unit) {
        db.collection("groups")
            .get()
            .addOnSuccessListener { snapshot ->
                val joinedGroups = mutableListOf<Group>()

                val tasks = snapshot.documents.map { doc ->
                    val employeeRef = doc.reference.collection("employees").document(currentUserId)
                    employeeRef.get().continueWith { task ->
                        if (task.isSuccessful) {
                            val status = task.result?.getString("status")
                            if (status == Status.ACCEPTED.toString()) {
                                fromDocument(doc)?.let { joinedGroups.add(it) }
                            }
                        }
                    }
                }

                // Chờ tất cả các task hoàn thành
                Tasks.whenAllComplete(tasks).addOnSuccessListener {
                    onResult(joinedGroups)
                }
            }
            .addOnFailureListener {
                Log.e("GroupRepository", "Failed to load joined groups", it)
                onResult(emptyList())
            }
    }

    // BỎ
    fun loadCreatedGroups(onResult: (List<Group>) -> Unit) {
//        val currentUserId = auth.currentUser?.uid ?: ""
//        val groupRef = db.collection("groups")
//
//            groupRef.get().addOnSuccessListener { snapshot ->
//
//            }
    }

    // Create a new group
    fun createGroup(group: Group, onSuccess: () -> Unit, onFailure: (Exception) -> Unit = {}) {
        val groupRef = db.collection("groups").document() // Tạo document mới và lấy ID
        val currentUserId = auth.currentUser?.uid ?: ""
        // Gán id cho group nếu cần sử dụng sau này
        group.id = groupRef.id

        groupRef.set(group)
            .addOnSuccessListener {
                // Thêm creator vào subcollection "employees"
                val employeeData = hashMapOf(
                    "status" to Status.ACCEPTED.toString(),
                    "isCreator" to true
                )
                groupRef.collection("employees")
                    .document(currentUserId)
                    .set(employeeData)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { onFailure(it) }
            }
            .addOnFailureListener { onFailure(it) }
    }


    // Accept request
    fun acceptJoinGroup(groupId: String, employeeId: String, onSuccess: () -> Unit) {
        val ref = db.collection("groups")
            .document(groupId)
            .collection("employees")
            .document(employeeId)

        ref.update("status", Status.ACCEPTED.toString())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener {
                Log.e("GroupRepository", "Failed to accept join", it)
            }
    }

    // Leave group
    fun leaveGroup(groupId: String, userId: String, onSuccess: () -> Unit) {
        db.collection("groups")
            .document(groupId)
            .collection("employees")
            .document(userId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                Log.e("GroupRepository", "Failed to leave group", it)
            }
    }
}
