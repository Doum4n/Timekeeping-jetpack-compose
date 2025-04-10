package com.example.timekeeping.ui.auth.state

data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val passwordError: Boolean = false,
    val errorMessage: String? = null
)