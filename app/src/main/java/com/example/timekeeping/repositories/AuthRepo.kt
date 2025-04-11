package com.example.timekeeping.repositories

import com.example.timekeeping.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {

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
    suspend fun registerUser(fullName: String, email: String, password: String): Result<String> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user != null) {
                createUser(user.uid, fullName, email)
                Result.success(user.uid)
            } else {
                Result.failure(Exception("Đăng ký thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createUser(
        userId: String,
        fullName: String,
        email: String,
    ){
        val user = User(
            id = userId,
            fullName = fullName,
            email = email,
        )

        FirebaseFirestore.getInstance().collection("users").document(userId).set(user)
    }

    // Đăng xuất người dùng
    fun logoutUser() {
        auth.signOut()
    }
}
