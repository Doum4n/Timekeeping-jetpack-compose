package com.example.timekeeping.repositories

import android.util.Log
import com.example.timekeeping.models.Team
import com.google.firebase.firestore.FirebaseFirestore

class TeamRepo(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
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

}