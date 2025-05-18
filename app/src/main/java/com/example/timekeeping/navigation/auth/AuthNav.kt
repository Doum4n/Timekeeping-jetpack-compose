package com.example.timekeeping.navigation.auth

import android.widget.Toast
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.timekeeping.ui.auth.LoginScreen
import com.example.timekeeping.ui.auth.RegisterScreen
import com.example.timekeeping.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

fun NavGraphBuilder.addAuthScreens(navController: NavHostController) {
    composable(Screen.Login.route) {
        LoginScreen(
            onLoginSuccess = {
                navController.navigate(Screen.Home.route)
            },
            onNavigateToRegister = { navController.navigate(Screen.Register.route) },
            onForgotPasswordClick = {
                FirebaseAuth.getInstance().sendPasswordResetEmail(it)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(navController.context, "Đã gửi email đặt lại mật khẩu", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(navController.context, "Không thể gửi email đặt lại mật khẩu", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        )
    }

    composable(Screen.Register.route) {
        val db = FirebaseFirestore.getInstance()
        RegisterScreen(
            onRegisterSuccess = { employee ->
                db.collection("employees").document().set(employee.toMap())
                    .addOnSuccessListener { navController.navigate(Screen.Login.route) }
                    .addOnFailureListener { }
            },
            onNavigateToLogin = { navController.navigate(Screen.Login.route) }
        )
    }
}