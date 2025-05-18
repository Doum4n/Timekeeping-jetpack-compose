package com.example.timekeeping.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timekeeping.repositories.AuthRepository
import com.example.timekeeping.ui.auth.state.LoginUiState
import com.example.timekeeping.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Initial)
    val registerState: StateFlow<RegisterState> = _registerState

    fun createLoginSession() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        SessionManager.getEmployeeReferenceByUserId(userId.toString()) { employeeRef ->
            val employeeId = employeeRef?.id

            if (employeeId != null) {
                SessionManager.createLoginSession(
                    userId.toString(),
                    employeeId,
                )
            } else {
                Log.e("Login", "employeeId is null, cannot create session")
            }

            _loginUiState.value = _loginUiState.value.copy(
                isLoading = false,
                isSuccess = true
            )
        }
    }

    // Đăng nhập
    fun loginUser(email: String, password: String) = viewModelScope.launch {
        _loginUiState.value = _loginUiState.value.copy(
            isLoading = true,
            errorMessage = null,
        )

        val result = authRepository.loginUser(email, password)
        if (result.isSuccess) {
            _loginUiState.value = _loginUiState.value.copy(
                isLoading = false,
                isSuccess = true
            )
        }
        else {
            _loginUiState.value = _loginUiState.value.copy(
                isLoading = false,
                errorMessage = result.exceptionOrNull()?.message ?: "Đã xảy ra lỗi"
            )
        }
    }

    fun registerUser(fullName: String, email: String, password: String) = viewModelScope.launch {
        _registerState.value = RegisterState.Loading
        val result = authRepository.registerUser(fullName, email, password)

        if (result.isSuccess) {
            val userId = result.getOrNull() ?: return@launch
            _registerState.value = RegisterState.Success(userId)
        } else {
            _registerState.value = RegisterState.Error(result.exceptionOrNull()?.message ?: "Đã xảy ra lỗi")
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

    fun resetLoginState() {
        _loginUiState.update {
            it.copy(isSuccess = false, isLoading = false, errorMessage = null)
        }
    }
}

sealed class RegisterState {
    object Initial : RegisterState()
    object Loading : RegisterState()
    data class Success(val userId: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
}
