package com.example.timekeeping.ui.auth

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
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
    viewModel: AuthViewModel = hiltViewModel()
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

                onRegisterSuccess(
                    Employee(
                        userId = state.userId,
                        name = uiState.name,
                        email = uiState.email,
                    )
                )

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
        onLastNameChange = {
            with(uiState) {
                uiState = copy(name = name.copy(lastName = it))
            }
        },
        onFirstNameChange = {
            with(uiState) {
                uiState = copy(name = name.copy(firstName = it))
            }
        },
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
                uiState.name.fullName.isNotEmpty() &&
                uiState.email.isNotEmpty() &&
                uiState.password.isNotEmpty()
            ) {
                viewModel.registerUser(uiState.name.fullName, uiState.email, uiState.password)
            }
        },
        onNavigateToLogin = onNavigateToLogin
    )
}