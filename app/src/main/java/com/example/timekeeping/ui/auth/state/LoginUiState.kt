package com.example.timekeeping.ui.auth.state

data class LoginUiState(
    val email: String = "pensonic1986@gmail.com",
    val password: String = "123443215",
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
