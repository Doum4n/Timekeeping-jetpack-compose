package com.example.timekeeping.navigation.auth

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.timekeeping.ui.auth.LoginScreen
import com.example.timekeeping.ui.auth.RegisterScreen
import com.example.timekeeping.navigation.Screen
import com.example.timekeeping.view_models.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore

fun NavGraphBuilder.addAuthScreens(navController: NavHostController) {
    composable(Screen.Login.route) {
        val viewModel: AuthViewModel = viewModel()
        LoginScreen(
            viewModel = viewModel,
            onLoginSuccess = { navController.navigate(Screen.Home.route) },
            onNavigateToRegister = { navController.navigate(Screen.Register.route) }
        )
    }

    composable(Screen.Register.route) {
        val viewModel: AuthViewModel = viewModel()
        val db = FirebaseFirestore.getInstance()
        RegisterScreen(
            viewModel = viewModel,
            onRegisterSuccess = { employee ->
                db.collection("employees").document().set(employee)
                    .addOnSuccessListener { navController.navigate(Screen.Login.route) }
                    .addOnFailureListener { }
            },
            onNavigateToLogin = { navController.navigate(Screen.Login.route) }
        )
    }
}