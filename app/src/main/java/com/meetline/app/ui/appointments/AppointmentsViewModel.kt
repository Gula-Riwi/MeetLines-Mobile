package com.meetline.app.ui.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meetline.app.domain.model.Appointment
import com.meetline.app.domain.usecase.CancelAppointmentUseCase
import com.meetline.app.domain.usecase.GetAppointmentsUseCase
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
 * @property upcomingAppointments Lista de citas futuras
 * @property pastAppointments Lista de citas pasadas
 * @property selectedTab Índice de la pestaña seleccionada (0=Próximas, 1=Historial)
 * @property error Mensaje de error si ocurrió un problema
 */
data class AppointmentsUiState(
    val isLoading: Boolean = true,
    val upcomingAppointments: List<Appointment> = emptyList(),
    val pastAppointments: List<Appointment> = emptyList(),
    val selectedTab: Int = 0,
    val error: String? = null
)

/**
 * ViewModel para la pantalla de citas del usuario.
 * 
 * Gestiona la visualización de citas próximas y pasadas,
 * así como la funcionalidad de cancelación de citas.
 * 
 * @property appointmentRepository Repositorio de citas inyectado por Hilt
 */
@HiltViewModel
class AppointmentsViewModel @Inject constructor(
    private val getAppointmentsUseCase: GetAppointmentsUseCase,
    private val cancelAppointmentUseCase: CancelAppointmentUseCase
) : ViewModel() {
    
    /** Estado mutable interno de la UI */
    private val _uiState = MutableStateFlow(AppointmentsUiState())
    
    /** Estado inmutable expuesto a la UI */
    val uiState: StateFlow<AppointmentsUiState> = _uiState.asStateFlow()
    
    init {
        loadAppointments()
    }
    
    /**
     * Carga todas las citas del usuario.
     * 
     * Obtiene las citas próximas y pasadas del repositorio
     * y actualiza el estado de la UI.
     */
    fun loadAppointments() {
        viewModelScope.launch {
            _uiState.value = AppointmentsUiState(isLoading = true)
            
            val upcoming = getAppointmentsUseCase.getUpcoming().getOrDefault(emptyList())
            val past = getAppointmentsUseCase.getPast().getOrDefault(emptyList())
            
            _uiState.value = AppointmentsUiState(
                isLoading = false,
                upcomingAppointments = upcoming,
                pastAppointments = past
            )
        }
    }
    
    /**
     * Cambia la pestaña seleccionada.
     * 
     * @param tab Índice de la pestaña (0=Próximas, 1=Historial)
     */
    fun selectTab(tab: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
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
