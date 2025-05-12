package com.example.timekeeping.repositories

import android.util.Log
import com.example.timekeeping.models.Request
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class RequestRepo @Inject constructor(
    val firestore: FirebaseFirestore
){

    fun createRequest(request: Request, onResult: () -> Unit){
        firestore.collection("requests")
            .add(request)
            .addOnSuccessListener {
                onResult()
            }.addOnFailureListener {
                onResult()
            }
    }

    fun getRequestByGroupId(groupId: String, onResult: (List<Request>) -> Unit){
        firestore.collection("requests")
            .whereEqualTo("groupId", groupId)
            .get()
            .addOnSuccessListener {
                val list = it.documents.mapNotNull {
                    it.toObject(Request::class.java)?.copy(id = it.id)
                }
                onResult(list)
            }.addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun getRequestByEmployeeId(employeeId: String, groupId: String, onResult: (List<Request>) -> Unit){
        Log.d("RequestRepo", "getRequestByEmployeeId: $employeeId, $groupId")
        firestore.collection("requests")
            .whereEqualTo("employeeId", employeeId)
            .whereEqualTo("groupId", groupId)
            .get()
            .addOnSuccessListener {
                val list = it.documents.mapNotNull {
                    it.toObject(Request::class.java)?.copy(id = it.id)
                }
                Log.d("RequestRepo", "getRequestByEmployeeId: $list")
                onResult(list)
            }.addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun getAllRequest(onResult: (List<Request>) -> Unit){
        firestore.collection("requests")
            .get()
            .addOnSuccessListener {
                val list = it.documents.mapNotNull {
                    it.toObject(Request::class.java)?.copy(id = it.id)
                }
                onResult(list)
            }.addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun updateStatusRequest(requestId: String, status: String, onResult: () -> Unit){
        firestore.collection("requests").document(requestId)
            .update("status", status)
            .addOnSuccessListener {
                onResult()
            }.addOnFailureListener {
                onResult()
            }
    }

    fun deleteRequest(requestId: String, onResult: () -> Unit, onError: (Exception) -> Unit){
        firestore.collection("requests").document(requestId)
            .delete()
            .addOnSuccessListener {
                onResult()
            }.addOnFailureListener {
                onError(it)
            }
    }
}