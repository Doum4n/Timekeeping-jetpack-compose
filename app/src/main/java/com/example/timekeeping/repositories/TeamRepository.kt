package com.example.timekeeping.repositories

import android.util.Log
import com.example.timekeeping.models.Employee
import com.example.timekeeping.models.Team
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class TeamRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun loadTeams(groupId: String, onSuccess: (List<Team>) -> Unit) {
        firestore.collection("teams")
            .whereEqualTo("groupId", groupId)
            .addSnapshotListener({ snapshot, exception ->
                if (exception != null) {
                    Log.e("TeamRepo", "Error loading teams", exception)
                    return@addSnapshotListener
                }

                val teams = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Team::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                onSuccess(teams)
                Log.d("TeamRepo", "Teams loaded: $teams")
            })
    }

    fun createTeam(team: Team, onSuccess: () -> Unit) {
        firestore.collection("teams").add(team).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { exception ->
            Log.e("TeamRepo", "Error creating team", exception)
            throw exception
        }
    }

    fun updateTeam(teamId: String, team: Team, onSuccess: () -> Unit) {
        firestore.collection("teams").document(teamId).set(team).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { exception ->
            Log.e("TeamRepo", " Error updating team", exception)
            throw exception
        }
    }

    fun deleteTeam(teamId: String, onSuccess: () -> Unit) {
        firestore.collection("teams").document(teamId).delete().addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { exception ->
            Log.e("TeamRepo", "Error deleting team", exception)
            throw exception
        }
    }

    fun getEmployees(teamId: String, callback: (List<Employee>) -> Unit) {
        firestore.collection("teams").document(teamId).get()
            .addOnSuccessListener { document ->
                val members = document.get("members") as? List<DocumentReference> ?: emptyList()

                // Gọi .get() cho từng DocumentReference
                val tasks = members.map { it.get() }

                // Đợi tất cả task thành công
                Tasks.whenAllSuccess<DocumentSnapshot>(tasks)
                    .addOnSuccessListener { documents ->
                        val employees = documents.mapNotNull { doc ->
                            doc.toObject(Employee::class.java)?.apply { id = doc.id }
                        }
                        callback(employees)
                        Log.d("TeamRepo", "Employees loaded: $employees")
                    }
                    .addOnFailureListener { exception ->
                        Log.e("TeamRepo", "Error getting employees", exception)
                        callback(emptyList()) // để không bị crash nếu lỗi
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("TeamRepo", "Error getting team", exception)
                callback(emptyList())
            }
    }

}