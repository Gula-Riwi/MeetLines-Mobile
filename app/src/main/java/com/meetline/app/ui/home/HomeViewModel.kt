package com.meetline.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meetline.app.domain.model.Appointment
import com.meetline.app.domain.model.Business
import com.meetline.app.domain.model.BusinessCategory
import com.meetline.app.domain.model.User
import com.meetline.app.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Representa el estado de la interfaz de usuario de la pantalla principal (Home).
 * 
 * Esta clase de datos encapsula todos los elementos necesarios para renderizar
 * la pantalla principal de la aplicación.
 *
 * @property isLoading Indica si los datos están siendo cargados actualmente
 * @property user El usuario actualmente autenticado, o null si no hay sesión
 * @property featuredBusinesses Lista de negocios destacados para mostrar en la pantalla principal
 * @property nearbyBusinesses Lista de negocios cercanos a la ubicación del usuario
 * @property upcomingAppointments Lista de próximas citas del usuario
 * @property categories Lista de categorías de negocios disponibles
 * @property searchQuery La consulta de búsqueda actual ingresada por el usuario
 * @property searchResults Lista de negocios que coinciden con la búsqueda actual
 * @property isSearching Indica si una búsqueda está en progreso
 * @property error Mensaje de error si ocurrió algún problema, o null si no hay errores
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val featuredBusinesses: List<Business> = emptyList(),
    val nearbyBusinesses: List<Business> = emptyList(),
    val upcomingAppointments: List<Appointment> = emptyList(),
    val categories: List<BusinessCategory> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<Business> = emptyList(),
    val isSearching: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel para la pantalla principal (Home) de la aplicación.
 * 
 * Este ViewModel gestiona el estado y la lógica de negocio de la pantalla principal,
 * incluyendo la carga de datos iniciales, búsqueda de negocios, y gestión de citas.
 * Utiliza inyección de dependencias con Hilt para obtener los repositorios necesarios.
 *
 * @property authRepository Repositorio para operaciones de autenticación
 * @property businessRepository Repositorio para operaciones relacionadas con negocios
 * @property appointmentRepository Repositorio para operaciones relacionadas con citas
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSessionUseCase: GetSessionUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val getFeaturedBusinessesUseCase: GetFeaturedBusinessesUseCase,
    private val getNearbyBusinessesUseCase: GetNearbyBusinessesUseCase,
    private val getAppointmentsUseCase: GetAppointmentsUseCase,
    private val searchBusinessesUseCase: SearchBusinessesUseCase,
    private val locationManager: com.meetline.app.data.location.LocationManager
) : ViewModel() {
    
    /**
     * Estado mutable interno de la UI. Solo accesible dentro del ViewModel.
     */
    private val _uiState = MutableStateFlow(HomeUiState())
    
    /**
     * Estado inmutable de la UI expuesto a la vista.
     * Las vistas pueden observar este StateFlow para reaccionar a cambios de estado.
     */
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        // No cargar datos aquí - esperar a que HomeScreen solicite permisos primero
        // para evitar peticiones duplicadas
        loadInitialData()
    }
    
    /**
     * Carga los datos iniciales necesarios para la pantalla principal.
     * 
     * Esta función se ejecuta en una corrutina y realiza las siguientes operaciones:
     * - Obtiene el usuario actual
     * - Carga todas las categorías de negocios
     * - Obtiene los negocios destacados
     * - Obtiene la ubicación GPS del usuario
     * - Obtiene los negocios cercanos usando las coordenadas GPS
     * - Obtiene las próximas citas (limitadas a 2)
     * 
     * Actualiza el estado de la UI con los datos obtenidos.
     */
    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val user = getSessionUseCase()
            val categories = getAllCategoriesUseCase()
            
            val featured = getFeaturedBusinessesUseCase().getOrDefault(emptyList())
            
            // Obtener ubicación GPS del usuario
            var latitude: Double? = null
            var longitude: Double? = null
            
            if (locationManager.hasLocationPermission()) {
                val locationResult = locationManager.getCurrentLocation()
                if (locationResult.isSuccess) {
                    val location = locationResult.getOrNull()
                    latitude = location?.latitude
                    longitude = location?.longitude
                } else {
                    android.util.Log.e("HomeViewModel", "Error obteniendo ubicación: ${locationResult.exceptionOrNull()?.message}")
                }
            } else {
                android.util.Log.w("HomeViewModel", "No tiene permisos de ubicación")
            }
            
            // Obtener negocios cercanos con las coordenadas (o sin ellas si no están disponibles)
            val nearby = getNearbyBusinessesUseCase(latitude, longitude).getOrDefault(emptyList())
            val upcoming = getAppointmentsUseCase.getUpcoming().getOrDefault(emptyList())
            
            _uiState.value = HomeUiState(
                isLoading = false,
                user = user,
                featuredBusinesses = featured,
                nearbyBusinesses = nearby,
                upcomingAppointments = upcoming.take(2),
                categories = categories
            )
        }
    }
    
    /**
     * Realiza una búsqueda de negocios basada en la consulta proporcionada.
     * 
     * Si la consulta está vacía, limpia los resultados de búsqueda.
     * Si la consulta contiene texto, ejecuta la búsqueda de forma asíncrona
     * y actualiza el estado con los resultados.
     *
     * @param query La consulta de búsqueda ingresada por el usuario
     */
    fun search(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(
                searchResults = emptyList(),
                isSearching = false
            )
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true)
            
            searchBusinessesUseCase(query)
                .onSuccess { results ->
                    _uiState.value = _uiState.value.copy(
                        searchResults = results,
                        isSearching = false
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isSearching = false)
                }
        }
    }
    
    /**
     * Limpia la búsqueda actual.
     * 
     * Restablece la consulta de búsqueda, los resultados y el estado de búsqueda
     * a sus valores predeterminados.
     */
    fun clearSearch() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            searchResults = emptyList(),
            isSearching = false
        )
    }
    
    /**
     * Recarga todos los datos de la pantalla principal.
     * 
     * Útil para implementar funcionalidad de "pull-to-refresh" o cuando
     * se necesita actualizar los datos después de cambios externos.
     */
    fun refresh() {
        loadInitialData()
    }
}
