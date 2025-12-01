package com.meetline.app.ui.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meetline.app.domain.model.*
import com.meetline.app.domain.usecase.CreateAppointmentUseCase
import com.meetline.app.domain.usecase.GetAvailableTimeSlotsUseCase
import com.meetline.app.domain.usecase.GetBusinessDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado de la interfaz de usuario para el flujo de reserva de citas.
 * 
 * @property isLoading Indica si se está cargando la información inicial
 * @property business Información del negocio donde se realizará la reserva
 * @property selectedProfessional Profesional seleccionado para la cita
 * @property selectedService Servicio seleccionado para la cita
 * @property selectedDate Fecha seleccionada en formato timestamp
 * @property selectedTime Hora seleccionada en formato "HH:mm"
 * @property availableTimeSlots Horarios disponibles para la fecha seleccionada
 * @property isBooking Indica si se está procesando la reserva
 * @property bookingSuccess Indica si la reserva se completó exitosamente
 * @property error Mensaje de error si ocurrió un problema
 */
data class BookingUiState(
    val isLoading: Boolean = true,
    val business: Business? = null,
    val selectedProfessional: Professional? = null,
    val selectedService: Service? = null,
    val selectedDate: Long? = null,
    val selectedTime: String? = null,
    val availableTimeSlots: List<TimeSlot> = emptyList(),
    val isBooking: Boolean = false,
    val bookingSuccess: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel para el flujo completo de reserva de citas.
 * 
 * Gestiona un proceso de múltiples pasos donde el usuario selecciona:
 * 1. Profesional
 * 2. Servicio
 * 3. Fecha
 * 4. Hora
 * 5. Confirmación final
 * 
 * Puede recibir parámetros opcionales de navegación para preseleccionar
 * el profesional o servicio.
 * 
 * @property businessRepository Repositorio de negocios inyectado por Hilt
 * @property appointmentRepository Repositorio de citas inyectado por Hilt
 * @property savedStateHandle Manejador de estado guardado para obtener parámetros de navegación
 */
@HiltViewModel
class BookingViewModel @Inject constructor(
    private val getBusinessDetailUseCase: GetBusinessDetailUseCase,
    private val getAvailableTimeSlotsUseCase: GetAvailableTimeSlotsUseCase,
    private val createAppointmentUseCase: CreateAppointmentUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    /** ID del negocio (obligatorio) */
    private val businessId: String = checkNotNull(savedStateHandle["businessId"])
    
    /** ID del profesional preseleccionado (opcional) */
    private val professionalId: String? = savedStateHandle["professionalId"]
    
    /** ID del servicio preseleccionado (opcional) */
    private val serviceId: String? = savedStateHandle["serviceId"]
    
    /** Estado mutable interno de la UI */
    private val _uiState = MutableStateFlow(BookingUiState())
    
    /** Estado inmutable expuesto a la UI */
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    /**
     * Carga los datos iniciales del negocio.
     * 
     * Obtiene la información del negocio y preselecciona el profesional
     * y servicio si se proporcionaron en los parámetros de navegación.
     */
    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = BookingUiState(isLoading = true)
            
            getBusinessDetailUseCase(businessId)
                .onSuccess { business ->
                    val professional = professionalId?.let { id ->
                        business.professionals.find { it.id == id }
                    } ?: business.professionals.firstOrNull()
                    
                    val service = serviceId?.let { id ->
                        business.services.find { it.id == id }
                    }
                    
                    _uiState.value = BookingUiState(
                        isLoading = false,
                        business = business,
                        selectedProfessional = professional,
                        selectedService = service
                    )
                }
                .onFailure { exception ->
                    _uiState.value = BookingUiState(
                        isLoading = false,
                        error = exception.message
                    )
                }
        }
    }
    
    /**
     * Selecciona un profesional para la cita.
     * 
     * Al cambiar de profesional, resetea la hora seleccionada y recarga
     * los horarios disponibles si ya hay una fecha seleccionada.
     * 
     * @param professional Profesional seleccionado
     */
    fun selectProfessional(professional: Professional) {
        _uiState.value = _uiState.value.copy(
            selectedProfessional = professional,
            selectedTime = null // Reset time when professional changes
        )
        // Reload time slots
        _uiState.value.selectedDate?.let { loadTimeSlots(it) }
    }
    
    /**
     * Selecciona un servicio para la cita.
     * 
     * @param service Servicio seleccionado
     */
    fun selectService(service: Service) {
        _uiState.value = _uiState.value.copy(selectedService = service)
    }
    
    /**
     * Selecciona una fecha para la cita.
     * 
     * Al cambiar de fecha, resetea la hora seleccionada y carga
     * los horarios disponibles para la nueva fecha.
     * 
     * @param date Fecha seleccionada en formato timestamp
     */
    fun selectDate(date: Long) {
        _uiState.value = _uiState.value.copy(
            selectedDate = date,
            selectedTime = null
        )
        loadTimeSlots(date)
    }
    
    /**
     * Carga los horarios disponibles para una fecha específica.
     * 
     * Consulta al repositorio los slots de tiempo disponibles para
     * el profesional y fecha seleccionados.
     * 
     * @param date Fecha para la cual consultar disponibilidad
     */
    private fun loadTimeSlots(date: Long) {
        viewModelScope.launch {
            val professional = _uiState.value.selectedProfessional ?: return@launch
            val business = _uiState.value.business ?: return@launch
            
            getAvailableTimeSlotsUseCase(
                businessId = business.id,
                professionalId = professional.id,
                date = date
            ).onSuccess { slots ->
                _uiState.value = _uiState.value.copy(availableTimeSlots = slots)
            }
        }
    }
    
    /**
     * Selecciona una hora para la cita.
     * 
     * @param time Hora seleccionada en formato "HH:mm"
     */
    fun selectTime(time: String) {
        _uiState.value = _uiState.value.copy(selectedTime = time)
    }
    
    /**
     * Confirma y crea la reserva de la cita.
     * 
     * Valida que todos los datos necesarios estén seleccionados
     * y envía la solicitud de creación de cita al repositorio.
     * 
     * Si alguno de los datos requeridos falta, la función retorna sin hacer nada.
     */
    fun confirmBooking() {
        val state = _uiState.value
        val business = state.business ?: return
        val professional = state.selectedProfessional ?: return
        val service = state.selectedService ?: return
        val date = state.selectedDate ?: return
        val time = state.selectedTime ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isBooking = true)
            
            createAppointmentUseCase(
                business = business,
                professional = professional,
                service = service,
                date = date,
                time = time
            ).onSuccess {
                _uiState.value = _uiState.value.copy(
                    isBooking = false,
                    bookingSuccess = true
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isBooking = false,
                    error = exception.message
                )
            }
        }
    }
    
    /**
     * Verifica si se puede proceder con la reserva.
     * 
     * Valida que todos los pasos del flujo de reserva estén completos:
     * profesional, servicio, fecha y hora seleccionados.
     * 
     * @return true si todos los datos necesarios están seleccionados, false en caso contrario
     */
    fun canProceed(): Boolean {
        val state = _uiState.value
        return state.selectedProfessional != null &&
               state.selectedService != null &&
               state.selectedDate != null &&
               state.selectedTime != null
    }
}
