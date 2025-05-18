package com.example.timekeeping.ui.auth

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.view_models.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onForgotPasswordClick: (String) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.loginUiState.collectAsState()
    var isSessionChecked by remember { mutableStateOf(false) }

    // Nếu login thành công từ flow bình thường
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.createLoginSession()
            onLoginSuccess()
            viewModel.resetLoginState()
        }
    }

    // Nếu đã đăng nhập sẵn (không cần login lại), thì tạo session
    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            viewModel.createLoginSession() // Gọi một lần
            onLoginSuccess()
        }
        isSessionChecked = true
    }

    if (!isSessionChecked) {
        // Có thể show Loading tại đây
        // CircularProgressIndicator()
        return
    }

    // Nếu chưa đăng nhập
    LoginScreenContent(
        state = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClick = { viewModel.loginUser(uiState.email, uiState.password) },
        onNavigateToRegister = onNavigateToRegister,
        onForgotPasswordClick = { onForgotPasswordClick(uiState.email) }
    )
}