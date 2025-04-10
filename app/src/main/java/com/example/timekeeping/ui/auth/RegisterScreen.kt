package com.example.timekeeping.ui.auth

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timekeeping.models.Employee
import com.example.timekeeping.models.Status
import com.example.timekeeping.ui.auth.state.RegisterUiState
import com.example.timekeeping.view_models.AuthViewModel
import com.example.timekeeping.view_models.RegisterState

@Composable
fun RegisterScreen(
    onRegisterSuccess: (Employee) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val registerState by viewModel.registerState.collectAsState()

    var uiState by remember {
        mutableStateOf(RegisterUiState())
    }

    // Handle UI events from ViewModel
    when (val state = registerState) {
        is RegisterState.Error -> {
            uiState = uiState.copy(errorMessage = state.message)
            LaunchedEffect(Unit) {
                //viewModel.resetState()
            }
        }

        is RegisterState.Success -> {
            LaunchedEffect(Unit) {
                onRegisterSuccess(
                    Employee(
                        userId = state.userId,
                        fullName = uiState.fullName,
                        email = uiState.email,
                        status = Status.PENDING,
                        isCreator = false
                    )
                )
            }
        }

        is RegisterState.Loading -> {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
        }

        else -> {
            uiState = uiState.copy(isLoading = false, errorMessage = null)
        }
    }

    RegisterScreenContent(
        state = uiState,
        onFullNameChange = { uiState = uiState.copy(fullName = it) },
        onEmailChange = { uiState = uiState.copy(email = it) },
        onPasswordChange = {
            uiState = uiState.copy(
                password = it,
                passwordError = uiState.confirmPassword.isNotEmpty() && it != uiState.confirmPassword
            )
        },
        onConfirmPasswordChange = {
            uiState = uiState.copy(
                confirmPassword = it,
                passwordError = uiState.password.isNotEmpty() && it != uiState.password
            )
        },
        onRegisterClick = {
            if (!uiState.passwordError &&
                uiState.fullName.isNotEmpty() &&
                uiState.email.isNotEmpty() &&
                uiState.password.isNotEmpty()
            ) {
                viewModel.registerUser(uiState.fullName, uiState.email, uiState.password)
            }
        },
        onNavigateToLogin = onNavigateToLogin
    )
}