package com.example.timekeeping.repositories

import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.example.timekeeping.models.Group
import com.example.timekeeping.models.Group.Companion.fromDocument
import com.example.timekeeping.models.Status
import com.example.timekeeping.utils.SessionManager
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import javax.inject.Singleton
import javax.inject.Inject

class GroupRepository @Inject constructor (
    val db: FirebaseFirestore,
    val auth: FirebaseAuth,
) {
    val currentUserId = auth.currentUser?.uid ?: ""

    fun loadJoinedGroups(onResult: (List<Group>) -> Unit) {
        SessionManager.getEmployeeReferenceByUserId(currentUserId) { employeeRef ->
            if (employeeRef != null) {
                db.collection("employee_group")
                    .whereEqualTo("employeeId", employeeRef)
                    .whereEqualTo("status", Status.ACCEPTED.toString())
                    .get()
                    .addOnSuccessListener { snapshot ->
                        processSnapshot(snapshot, onResult)
                    }
                    .addOnFailureListener {
                        Log.e("GroupRepository", "Failed to load joined groups", it)
                        onResult(emptyList())
                        it.printStackTrace()
                    }
            } else {
                Log.e("TAG", "Không tìm thấy employee với userId=$currentUserId")
            }
        }
    }

    private fun processSnapshot(snapshot: QuerySnapshot, onResult: (List<Group>) -> Unit) {
        val groupRefs = snapshot.documents.mapNotNull { it.getDocumentReference("groupId") }

        if (groupRefs.isEmpty()) {
            onResult(emptyList())
            return
        }

        val tasks = groupRefs.map { it.get() }

        Tasks.whenAllSuccess<com.google.firebase.firestore.DocumentSnapshot>(tasks)
            .addOnSuccessListener { documents ->
                val groups = documents.mapNotNull { doc ->
                    doc.toObject(Group::class.java)?.apply { id = doc.id }
                }
                onResult(groups)
                Log.d("GroupRepository", "Loaded ${groups.size} joined groups.")
            }
            .addOnFailureListener {
                Log.e("GroupRepository", "Failed to get group details", it)
                onResult(emptyList())
            }
    }


    // BỎ
    fun loadCreatedGroups(onResult: (List<Group>) -> Unit) {

    }

    // Create a new group
    fun createGroup(group: Group, onSuccess: () -> Unit, onFailure: (Exception) -> Unit = {}) {
        val newGroupRef = db.collection("groups").document()  // <-- tạo document trước để lấy ID

        newGroupRef.set(group)                               // <-- lưu group
            .addOnSuccessListener {
                // Sau khi lưu group thành công, thêm vào bảng liên kết
                db.collection("employee_group").add(
                    mapOf(
                        "employeeId" to currentUserId,
                        "groupId" to newGroupRef,                // <-- dùng ID ở đây
                        "status" to Status.ACCEPTED.toString()
                    )
                ).addOnSuccessListener {
                    onSuccess()
                }.addOnFailureListener {
                    onFailure(it)
                }
            }
            .addOnFailureListener {
                onFailure(it)
            }
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

    fun getGroupById(groupId: String, onResult: (Group?) -> Unit) {
        db.collection("groups").document(groupId).get()
            .addOnSuccessListener { document ->
                val group = document.toObject(Group::class.java)?.apply { id = document.id }
                onResult(group)
            }.addOnFailureListener {
                Log.e("GroupRepository", "Failed to get group", it)
                onResult(null)
            }
    }

    // Leave group
    fun leaveGroup(groupId: String, userId: String, onSuccess: () -> Unit) {

    }

    fun updateGroup(group: Group, function: () -> Unit) {
        db.collection("groups").document(group.id).set(group)
            .addOnSuccessListener { function() }
            .addOnFailureListener {
                Log.e("GroupRepository", "Failed to update group", it)
            }
    }

    fun deleteGroup(groupId: String, function: () -> Unit) {
        db.collection("groups").document(groupId).delete()
            .addOnSuccessListener { function() }
            .addOnFailureListener {
                Log.e("GroupRepository", "Failed to delete group", it)
            }
    }
}
