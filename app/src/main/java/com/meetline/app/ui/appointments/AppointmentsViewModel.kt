package com.meetline.app.ui.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meetline.app.domain.model.Appointment
import com.meetline.app.domain.usecase.CancelAppointmentUseCase
import com.meetline.app.domain.usecase.GetAppointmentsUseCase
import com.meetline.app.domain.usecase.GetMyActiveAppointmentsUseCase
import com.meetline.app.domain.usecase.GetMyAppointmentHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado de la interfaz de usuario para la pantalla de citas.
 * 
 * @property isLoading Indica si se están cargando las citas
 * @property activeAppointments Lista de citas activas (pendientes) del usuario autenticado
 * @property historyAppointments Lista completa de citas del historial (todas)
 * @property upcomingAppointments Lista de citas futuras (método legacy)
 * @property pastAppointments Lista de citas pasadas (método legacy)
 * @property selectedTab Índice de la pestaña seleccionada (0=Activas, 1=Historial)
 * @property error Mensaje de error si ocurrió un problema
 * @property sessionExpired Indica si la sesión expiró (token inválido)
 */
data class AppointmentsUiState(
    val isLoading: Boolean = true,
    val activeAppointments: List<Appointment> = emptyList(),
    val historyAppointments: List<Appointment> = emptyList(),
    val upcomingAppointments: List<Appointment> = emptyList(),
    val pastAppointments: List<Appointment> = emptyList(),
    val selectedTab: Int = 0,
    val error: String? = null,
    val sessionExpired: Boolean = false
)

/**
 * ViewModel para la pantalla de citas del usuario.
 * 
 * Gestiona la visualización de citas activas (pendientes) e historial completo,
 * utilizando autenticación JWT para obtener los datos del usuario autenticado.
 * 
 * @property getMyActiveAppointmentsUseCase Caso de uso para obtener citas activas
 * @property getMyAppointmentHistoryUseCase Caso de uso para obtener historial completo
 * @property getAppointmentsUseCase Caso de uso legacy para citas locales
 * @property cancelAppointmentUseCase Caso de uso para cancelar citas
 */
@HiltViewModel
class AppointmentsViewModel @Inject constructor(
    private val getMyActiveAppointmentsUseCase: GetMyActiveAppointmentsUseCase,
    private val getMyAppointmentHistoryUseCase: GetMyAppointmentHistoryUseCase,
    private val getAppointmentsUseCase: GetAppointmentsUseCase,
    private val cancelAppointmentUseCase: CancelAppointmentUseCase
) : ViewModel() {
    
    /** Estado mutable interno de la UI */
    private val _uiState = MutableStateFlow(AppointmentsUiState())
    
    /** Estado inmutable expuesto a la UI */
    val uiState: StateFlow<AppointmentsUiState> = _uiState.asStateFlow()
    
    init {
        loadMyActiveAppointments()
    }
    
    /**
     * Carga las citas activas del usuario autenticado desde la API.
     *
     * Requiere autenticación JWT. Si el token es inválido o expiró,
     * se marca sessionExpired = true para redirigir al login.
     */
    fun loadMyActiveAppointments() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                sessionExpired = false
            )
            
            val result = getMyActiveAppointmentsUseCase()
            
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    activeAppointments = result.getOrNull() ?: emptyList(),
                    error = null
                )
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
                // Ya no forzamos logout automático por errores del servicio de citas
                // Solo si el mensaje explícitamente es "Sesión expirada" (legacy/main auth)
                val isSessionExpired = errorMessage.contains("Sesión expirada", ignoreCase = true)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = errorMessage,
                    sessionExpired = isSessionExpired
                )
            }
        }
    }
    
    /**
     * Carga el historial completo de citas del usuario autenticado desde la API.
     *
     * Requiere autenticación JWT. Incluye todas las citas (pendientes, completadas, canceladas).
     */
    fun loadMyAppointmentHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                sessionExpired = false
            )
            
            val result = getMyAppointmentHistoryUseCase()
            
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    historyAppointments = result.getOrNull() ?: emptyList(),
                    error = null
                )
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
                // Ya no forzamos logout automático por errores del servicio de citas
                // Solo si el mensaje explícitamente es "Sesión expirada" (legacy/main auth)
                val isSessionExpired = errorMessage.contains("Sesión expirada", ignoreCase = true)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = errorMessage,
                    sessionExpired = isSessionExpired
                )
            }
        }
    }
    
    /**
     * Carga todas las citas del usuario (método legacy).
     * 
     * Obtiene las citas próximas y pasadas del repositorio local
     * y actualiza el estado de la UI.
     */
    fun loadAppointments() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val upcoming = getAppointmentsUseCase.getUpcoming().getOrDefault(emptyList())
            val past = getAppointmentsUseCase.getPast().getOrDefault(emptyList())
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                upcomingAppointments = upcoming,
                pastAppointments = past
            )
        }
    }
    
    /**
     * Cambia la pestaña seleccionada.
     * 
     * @param tab Índice de la pestaña (0=Activas, 1=Historial)
     */
    fun selectTab(tab: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
        
        // Cargar datos según la pestaña seleccionada
        when (tab) {
            0 -> loadMyActiveAppointments()
            1 -> loadMyAppointmentHistory()
        }
    }
    
    /**
     * Limpia el error actual.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    /**
     * Cancela una cita existente.
     * 
     * Después de cancelar, recarga la lista de citas para reflejar el cambio.
     * 
     * @param appointmentId ID de la cita a cancelar
     */
    fun cancelAppointment(appointmentId: String) {
        viewModelScope.launch {
            cancelAppointmentUseCase(appointmentId)
            loadAppointments() // Reload
        }
    }
}
