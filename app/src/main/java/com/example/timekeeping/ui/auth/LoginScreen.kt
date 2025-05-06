package com.example.timekeeping.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timekeeping.view_models.AuthViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.loginUiState.collectAsState()

    // Sử dụng LaunchedEffect để xử lý side effect
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onLoginSuccess()
            viewModel.resetLoginState() // Thêm hàm reset state trong ViewModel
        }
    }

    if(FirebaseAuth.getInstance().currentUser != null) {
        onLoginSuccess()
    }else {
        LoginScreenContent(
            state = uiState,
            onEmailChange = viewModel::onEmailChange,
            onPasswordChange = viewModel::onPasswordChange,
            onLoginClick = { viewModel.loginUser(uiState.email, uiState.password) },
            onNavigateToRegister = onNavigateToRegister
        )
    }
}