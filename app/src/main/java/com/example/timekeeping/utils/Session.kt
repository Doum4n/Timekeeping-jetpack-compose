package com.example.timekeeping.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

object SessionManager {

    private const val PREF_NAME = "TimekeepingSession"
    private const val KEY_EMPLOYEE_ID = "employeeId"
    private const val KEY_USER_ID = "userId"
    private const val ROLE = "role"

    private lateinit var pref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private var isInitialized = false

    fun init(context: Context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = pref.edit()
        isInitialized = true
    }

    private fun checkInitialized() {
        if (!isInitialized) {
            throw IllegalStateException("SessionManager must be initialized with context before use. Call SessionManager.init(context).")
        }
    }

    fun createLoginSession(userId: String, employeeId: String) {
        checkInitialized()
        editor.putString(KEY_USER_ID, userId)
        editor.putString(KEY_EMPLOYEE_ID, employeeId)
        editor.apply()

        Log.d("SessionManager", "Login session created: userId=$userId, employeeId=$employeeId")
    }

    fun getEmployeeId(): String? {
        checkInitialized()
        return pref.getString(KEY_EMPLOYEE_ID, null)
    }

    fun getRole(): String? {
        checkInitialized()
        return pref.getString(ROLE, null)
    }

    fun setRole(role: String) {
        checkInitialized()
        editor.putString(ROLE, role)
        editor.apply()
    }

    fun getUserId(): String? {
        checkInitialized()
        return pref.getString(KEY_USER_ID, null)
    }

    fun getEmployeeReferenceByUserId(userId: String, onResult: (DocumentReference?) -> Unit) {
        FirebaseFirestore.getInstance().collection("employees")
            .whereEqualTo("userId", userId)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val doc = querySnapshot.documents.firstOrNull()
                val employeeId = doc?.id
                val ref = doc?.reference

                onResult(ref)
            }
            .addOnFailureListener {
                Log.e("SessionManager", "Failed to fetch employeeRef", it)
                onResult(null)
            }
    }
}
