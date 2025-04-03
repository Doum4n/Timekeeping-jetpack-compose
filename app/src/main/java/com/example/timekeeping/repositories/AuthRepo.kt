package com.example.timekeeping.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthRepository(private val auth: FirebaseAuth = Firebase.auth) {

    // Đăng nhập người dùng
    suspend fun loginUser(email: String, password: String): Result<String> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user != null) {
                Result.success(user.uid)
            } else {
                Result.failure(Exception("Đăng nhập thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Đăng ký người dùng
    suspend fun registerUser(email: String, password: String): Result<String> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user != null) {
                Result.success(user.uid)
            } else {
                Result.failure(Exception("Đăng ký thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Đăng xuất người dùng
    fun logoutUser() {
        auth.signOut()
    }
}
