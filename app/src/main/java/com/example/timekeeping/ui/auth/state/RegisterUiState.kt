package com.example.timekeeping.ui.auth.state

import com.example.timekeeping.models.Name

data class RegisterUiState(
    val name: Name = Name(),
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val passwordError: Boolean = false,
    val errorMessage: String? = null
)