package com.example.timekeeping.view_models

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.timekeeping.models.Shift
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore

class ShiftViewModel(
    groupId: String,
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {
    val shifts = mutableStateListOf<Shift>()

    init {
        loadShifts(groupId)
    }

    private fun loadShifts(groupId: String) {
        db.collection("shifts").where(Filter.equalTo("groupId", groupId)).get()
            .addOnSuccessListener { query ->
                shifts.clear()
                for (doc in query.documents) {
                    val shift = doc.toObject(Shift::class.java)
                    shift?.id = doc.id
                    shift?.let { shifts.add(it) }
                }
            }
    }

    fun create(shift: Shift) {
        db.collection("shifts").add(shift).addOnSuccessListener {
            shift.id = it.id
            loadShifts(shift.groupId)
            //onSuccess(id)
        }
    }

    fun update(shiftId: String, shift: Shift) {
        db.collection("shifts").document(shiftId).set(shift).addOnSuccessListener {
            loadShifts(shift.groupId)
        }
    }

    fun getShiftById(shiftId: String, callback: (Shift) -> Unit){
        db.collection("shifts").document(shiftId).get().addOnSuccessListener {
            val shift = it.toObject(Shift::class.java)
            shift?.let { callback(it) }
        }
    }
}