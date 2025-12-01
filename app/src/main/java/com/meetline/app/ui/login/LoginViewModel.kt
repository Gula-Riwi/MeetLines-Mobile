package com.meetline.app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meetline.app.domain.model.User
import com.meetline.app.domain.usecase.GetSessionUseCase
import com.meetline.app.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado de la interfaz de usuario para la pantalla de login.
 * 
 * Encapsula todos los posibles estados durante el proceso de autenticación.
 * 
 * @property isLoading Indica si se está procesando una solicitud de login
 * @property isSuccess Indica si el login fue exitoso
 * @property error Mensaje de error si el login falló, null si no hay error
 * @property user Datos del usuario autenticado, null si aún no se ha autenticado
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val user: User? = null
)

/**
 * ViewModel para la pantalla de inicio de sesión.
 * 
 * Gestiona la lógica de autenticación y el estado de la UI durante el login.
 * Al inicializarse, verifica si ya existe una sesión activa para permitir
 * el auto-login.
 * 
 * Responsabilidades:
 * - Verificar sesiones existentes al iniciar
 * - Procesar solicitudes de login
 * - Gestionar estados de carga y errores
 * - Comunicar resultados a la UI mediante StateFlow
 * 
 * @property authRepository Repositorio de autenticación inyectado por Hilt
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val getSessionUseCase: GetSessionUseCase
) : ViewModel() {
    
    /**
     * Estado mutable interno de la UI.
     */
    private val _uiState = MutableStateFlow(LoginUiState())
    
    /**
     * Estado inmutable expuesto a la UI para observación.
     */
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    init {
        checkSession()
    }
    
    /**
     * Verifica si existe una sesión activa al inicializar el ViewModel.
     * 
     * Si encuentra una sesión activa, actualiza el estado a exitoso
     * permitiendo que la UI navegue automáticamente a la pantalla principal.
     */
    private fun checkSession() {
        val user = getSessionUseCase()
        if (user != null) {
            _uiState.value = LoginUiState(
                isSuccess = true,
                user = user
            )
        }
    }
    
    /**
     * Intenta iniciar sesión con las credenciales proporcionadas.
     * 
     * Actualiza el estado a "cargando" mientras se procesa la solicitud,
     * luego actualiza a "exitoso" o "error" según el resultado.
     * 
     * @param email Correo electrónico del usuario
     * @param password Contraseña del usuario
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            
            loginUseCase(email, password)
                .onSuccess { user ->
                    _uiState.value = LoginUiState(isSuccess = true, user = user)
                }
                .onFailure { exception ->
                    _uiState.value = LoginUiState(error = exception.message)
                }
        }
    }
    
    /**
     * Limpia el mensaje de error del estado.
     * 
     * Útil para cerrar diálogos de error o limpiar mensajes
     * después de que el usuario los haya leído.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
