package com.auto_care_test.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auto_care_test.data.firebase.auth.AuthDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class AuthViewModel(
    private val authDataSource: AuthDataSource = AuthDataSource()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkAuthState()
    }

    fun checkAuthState() {
        _uiState.update { it.copy(isLoggedIn = authDataSource.isUserLoggedIn()) }
    }

    fun register(email: String, password: String, nombre: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            try {
                val result = authDataSource.registerUser(email, password, nombre)
                result.onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            successMessage = "Cuenta creada correctamente"
                        )
                    }
                }.onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "No se pudo registrar el usuario")
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error desconocido") }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            try {
                val result = authDataSource.loginUser(email, password)
                result.onSuccess {
                    _uiState.update {
                        it.copy(isLoading = false, isLoggedIn = true, successMessage = "Sesión iniciada")
                    }
                }.onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Credenciales inválidas")
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error desconocido") }
            }
        }
    }

    fun logout() {
        authDataSource.logout()
        _uiState.update {
            AuthUiState(isLoggedIn = false)
        }
    }
}
