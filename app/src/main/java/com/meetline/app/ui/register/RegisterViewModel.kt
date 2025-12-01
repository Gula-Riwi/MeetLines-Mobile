package com.meetline.app.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meetline.app.domain.model.User
import com.meetline.app.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado de la interfaz de usuario para la pantalla de registro.
 * 
 * Encapsula todos los posibles estados durante el proceso de registro de usuario.
 * 
 * @property isLoading Indica si se está procesando una solicitud de registro
 * @property isSuccess Indica si el registro fue exitoso
 * @property error Mensaje de error si el registro falló, null si no hay error
 * @property user Datos del usuario recién registrado, null si aún no se ha completado
 */
data class RegisterUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val user: User? = null
)

/**
 * ViewModel para la pantalla de registro de nuevos usuarios.
 * 
 * Gestiona la lógica de registro y el estado de la UI durante el proceso.
 * Al completar exitosamente el registro, crea automáticamente una sesión
 * para el nuevo usuario.
 * 
 * Responsabilidades:
 * - Procesar solicitudes de registro con validación
 * - Gestionar estados de carga y errores
 * - Crear sesión automáticamente tras registro exitoso
 * - Comunicar resultados a la UI mediante StateFlow
 * 
 * @property authRepository Repositorio de autenticación inyectado por Hilt
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    
    /**
     * Estado mutable interno de la UI.
     */
    private val _uiState = MutableStateFlow(RegisterUiState())
    
    /**
     * Estado inmutable expuesto a la UI para observación.
     */
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()
    
    /**
     * Registra un nuevo usuario en el sistema.
     * 
     * Actualiza el estado a "cargando" mientras se procesa la solicitud,
     * luego actualiza a "exitoso" o "error" según el resultado.
     * Si el registro es exitoso, el usuario queda automáticamente autenticado.
     * 
     * @param name Nombre completo del usuario
     * @param email Correo electrónico del usuario
     * @param phone Número de teléfono del usuario
     * @param password Contraseña deseada
     */
    fun register(name: String, email: String, phone: String, password: String) {
        viewModelScope.launch {
            _uiState.value = RegisterUiState(isLoading = true)
            
            registerUseCase(name, email, phone, password)
                .onSuccess { user ->
                    _uiState.value = RegisterUiState(isSuccess = true, user = user)
                }
                .onFailure { exception ->
                    _uiState.value = RegisterUiState(error = exception.message)
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
