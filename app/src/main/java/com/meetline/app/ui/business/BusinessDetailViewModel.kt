package com.meetline.app.ui.business

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meetline.app.domain.model.*
import com.meetline.app.domain.usecase.GetBusinessDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado de la interfaz de usuario para la pantalla de detalle de negocio.
 * 
 * @property isLoading Indica si se está cargando la información del negocio
 * @property business Información completa del negocio
 * @property error Mensaje de error si ocurrió un problema
 */
data class BusinessDetailUiState(
    val isLoading: Boolean = true,
    val business: Business? = null,
    val error: String? = null
)

/**
 * ViewModel para la pantalla de detalle de un negocio.
 * 
 * Carga y muestra la información completa de un negocio específico,
 * incluyendo sus profesionales, servicios, horarios y calificaciones.
 * 
 * El ID del negocio se obtiene de los argumentos de navegación a través
 * de SavedStateHandle.
 * 
 * @property businessRepository Repositorio de negocios inyectado por Hilt
 * @property savedStateHandle Manejador de estado guardado para obtener parámetros de navegación
 */
@HiltViewModel
class BusinessDetailViewModel @Inject constructor(
    private val getBusinessDetailUseCase: GetBusinessDetailUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    /** ID del negocio obtenido de los argumentos de navegación */
    private val businessId: String = checkNotNull(savedStateHandle["businessId"])
    
    /** Estado mutable interno de la UI */
    private val _uiState = MutableStateFlow(BusinessDetailUiState())
    
    /** Estado inmutable expuesto a la UI */
    val uiState: StateFlow<BusinessDetailUiState> = _uiState.asStateFlow()
    
    init {
        loadBusiness()
    }
    
    /**
     * Carga la información completa del negocio.
     * 
     * Obtiene todos los detalles del negocio desde el repositorio
     * usando el businessId proporcionado en la navegación.
     */
    private fun loadBusiness() {
        viewModelScope.launch {
            _uiState.value = BusinessDetailUiState(isLoading = true)
            
            getBusinessDetailUseCase(businessId)
                .onSuccess { business ->
                    _uiState.value = BusinessDetailUiState(
                        isLoading = false,
                        business = business
                    )
                }
                .onFailure { exception ->
                    _uiState.value = BusinessDetailUiState(
                        isLoading = false,
                        error = exception.message
                    )
                }
        }
    }
}
