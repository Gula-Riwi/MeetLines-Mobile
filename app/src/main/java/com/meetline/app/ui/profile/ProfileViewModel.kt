package com.meetline.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meetline.app.domain.model.User
import com.meetline.app.domain.usecase.GetSessionUseCase
import com.meetline.app.domain.usecase.GetUserProfileUseCase
import com.meetline.app.domain.usecase.LogoutUseCase
import com.meetline.app.domain.usecase.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado de la interfaz de usuario para la pantalla de perfil.
 * 
 * @property isLoading Indica si se están cargando datos
 * @property user Datos del usuario actual
 * @property isEditing Indica si el usuario está en modo edición
 * @property isSaving Indica si se está guardando el perfil
 * @property saveSuccess Indica si el guardado fue exitoso
 * @property error Mensaje de error si ocurrió un problema
 */
data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel para la pantalla de perfil de usuario.
 * 
 * Gestiona la visualización y edición del perfil del usuario,
 * así como la funcionalidad de cierre de sesión.
 * 
 * @property authRepository Repositorio de autenticación inyectado por Hilt
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getSessionUseCase: GetSessionUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    
    /** Estado mutable interno de la UI */
    private val _uiState = MutableStateFlow(ProfileUiState())
    
    /** Estado inmutable expuesto a la UI */
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    init {
        loadUserProfile()
    }
    
    /**
     * Carga los datos del usuario desde el servidor.
     */
    private fun loadUserProfile() {
        viewModelScope.launch {
            // Mostrar datos locales inmediatamente
            _uiState.value = ProfileUiState(
                isLoading = true,
                user = getSessionUseCase()
            )
            
            // Luego actualizar desde el servidor
            getUserProfileUseCase()
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        user = user
                    )
                }
                .onFailure {
                    // Mantener datos locales si falla la petición
                    _uiState.value = _uiState.value.copy(
                        isLoading = false
                    )
                }
        }
    }
    
    /**
     * Activa el modo de edición del perfil.
     */
    fun startEditing() {
        _uiState.value = _uiState.value.copy(isEditing = true)
    }
    
    /**
     * Cancela la edición y restaura los valores originales.
     */
    fun cancelEditing() {
        _uiState.value = _uiState.value.copy(isEditing = false)
        loadUserProfile() // Reset to original values
    }
    
    /**
     * Guarda los cambios del perfil del usuario.
     * 
     * @param name Nuevo nombre del usuario
     * @param email Nuevo email del usuario
     * @param phone Nuevo teléfono del usuario
     */
    fun saveProfile(name: String, email: String, phone: String) {
        val currentUser = _uiState.value.user ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            
            val updatedUser = currentUser.copy(
                name = name,
                email = email,
                phone = phone
            )
            
            updateProfileUseCase(updatedUser)
                .onSuccess { user ->
                    _uiState.value = ProfileUiState(
                        user = user,
                        isEditing = false,
                        saveSuccess = true
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = exception.message
                    )
                }
        }
    }
    
    /**
     * Cierra la sesión del usuario actual.
     */
    fun logout() {
        logoutUseCase()
    }
    
    /**
     * Limpia los mensajes de éxito y error.
     */
    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            saveSuccess = false,
            error = null
        )
    }
}
