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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timekeeping.view_models.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.loginUiState.collectAsState()

    LoginScreenContent(
        state = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClick = { viewModel.loginUser(uiState.email, uiState.password) },
        onNavigateToRegister = onNavigateToRegister
    )

    // Điều hướng khi login thành công (nếu bạn có trigger logic ở nơi khác)
    if (!uiState.isLoading && uiState.errorMessage == null && uiState.email.isNotEmpty() && uiState.password.isNotEmpty()) {
        onLoginSuccess()
    }
}