package com.example.timekeeping.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timekeeping.repositories.AuthRepository
import com.example.timekeeping.ui.auth.state.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Initial)
    val registerState: StateFlow<RegisterState> = _registerState

    // Đăng nhập
    fun loginUser(email: String, password: String) = viewModelScope.launch {
        _loginUiState.value = _loginUiState.value.copy(
            isLoading = true,
            errorMessage = null
        )

        val result = authRepository.loginUser(email, password)
        if (result.isSuccess) {
            _loginUiState.value = _loginUiState.value.copy(
                isLoading = false
            )
            // Có thể trigger 1 event riêng nếu cần
        } else {
            _loginUiState.value = _loginUiState.value.copy(
                isLoading = false,
                errorMessage = result.exceptionOrNull()?.message ?: "Đã xảy ra lỗi"
            )
        }
    }

    // Đăng ký
    fun registerUser(fullName: String, email: String, password: String) = viewModelScope.launch {
        _registerState.value = RegisterState.Loading
        val result = authRepository.registerUser(fullName, email, password)
        _registerState.value = if (result.isSuccess) {
            RegisterState.Success(result.getOrDefault(""))
        } else {
            RegisterState.Error(result.exceptionOrNull()?.message ?: "Đã xảy ra lỗi")
        }
    }

    fun onEmailChange(newEmail: String) {
        _loginUiState.value = _loginUiState.value.copy(email = newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        _loginUiState.value = _loginUiState.value.copy(password = newPassword)
    }

    fun resetLoginError() {
        _loginUiState.value = _loginUiState.value.copy(errorMessage = null)
    }

    fun logoutUser() {
        authRepository.logoutUser()
    }
}

sealed class RegisterState {
    object Initial : RegisterState()
    object Loading : RegisterState()
    data class Success(val userId: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
}
