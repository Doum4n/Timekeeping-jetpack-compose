package com.example.timekeeping.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timekeeping.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> = _loginState

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Initial)
    val registerState: StateFlow<RegisterState> = _registerState

    // Đăng nhập người dùng
    fun loginUser(email: String, password: String) = viewModelScope.launch {
        _loginState.value = LoginState.Loading
        val result = authRepository.loginUser(email, password)
        if (result.isSuccess) {
            _loginState.value = LoginState.Success(result.getOrDefault(""))
        } else {
            _loginState.value = LoginState.Error(result.exceptionOrNull()?.message ?: "Đã xảy ra lỗi")
        }
    }

    // Đăng ký người dùng
    fun registerUser(fullName: String, email: String, password: String) = viewModelScope.launch {
        _registerState.value = RegisterState.Loading
        val result = authRepository.registerUser(fullName, email, password)
        if (result.isSuccess) {
            _registerState.value = RegisterState.Success(result.getOrDefault(""))
        } else {
            _registerState.value = RegisterState.Error(result.exceptionOrNull()?.message ?: "Đã xảy ra lỗi")
        }
    }

    // Đăng xuất người dùng
    fun logoutUser() {
        authRepository.logoutUser()
    }

    // Reset lại trạng thái
    fun resetState() {
        _loginState.value = LoginState.Initial
        _registerState.value = RegisterState.Initial
    }
}

sealed class LoginState {
    object Initial : LoginState()
    object Loading : LoginState()
    data class Success(val userId: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

sealed class RegisterState {
    object Initial : RegisterState()
    object Loading : RegisterState()
    data class Success(val userId: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
}