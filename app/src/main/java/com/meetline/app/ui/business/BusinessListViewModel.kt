package com.meetline.app.ui.business

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meetline.app.domain.model.*
import com.meetline.app.domain.usecase.GetAllCategoriesUseCase
import com.meetline.app.domain.usecase.GetBusinessListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado de la interfaz de usuario para la pantalla de lista de negocios.
 * 
 * @property isLoading Indica si se están cargando los negocios
 * @property businesses Lista de negocios a mostrar
 * @property selectedCategory Categoría actualmente seleccionada para filtrar
 * @property categories Lista de todas las categorías disponibles
 * @property error Mensaje de error si ocurrió un problema
 */
data class BusinessListUiState(
    val isLoading: Boolean = true,
    val businesses: List<Business> = emptyList(),
    val selectedCategory: BusinessCategory? = null,
    val categories: List<BusinessCategory> = emptyList(),
    val error: String? = null
)

/**
 * ViewModel para la pantalla de lista de negocios.
 * 
 * Muestra una lista de negocios que puede ser filtrada por categoría.
 * Soporta navegación con categoría preseleccionada a través de parámetros.
 * 
 * @property businessRepository Repositorio de negocios inyectado por Hilt
 * @property savedStateHandle Manejador de estado guardado para obtener parámetros de navegación
 */
@HiltViewModel
class BusinessListViewModel @Inject constructor(
    private val getBusinessListUseCase: GetBusinessListUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    /** Nombre de categoría opcional obtenido de los argumentos de navegación */
    private val categoryName: String? = savedStateHandle["category"]
    
    /** Estado mutable interno de la UI */
    private val _uiState = MutableStateFlow(BusinessListUiState())
    
    /** Estado inmutable expuesto a la UI */
    val uiState: StateFlow<BusinessListUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    /**
     * Carga los datos iniciales.
     * 
     * Obtiene todas las categorías y los negocios, aplicando filtro
     * de categoría si se proporcionó en los parámetros de navegación.
     */
    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = BusinessListUiState(isLoading = true)
            
            val categories = getAllCategoriesUseCase()
            val selectedCategory = categoryName?.let { name ->
                categories.find { it.name == name }
            }
            
            val businesses = getBusinessListUseCase(selectedCategory).getOrDefault(emptyList())
            
            _uiState.value = BusinessListUiState(
                isLoading = false,
                businesses = businesses,
                selectedCategory = selectedCategory,
                categories = categories
            )
        }
    }
    
    /**
     * Filtra los negocios por categoría.
     * 
     * Si se proporciona null, muestra todos los negocios sin filtro.
     * 
     * @param category Categoría por la cual filtrar, o null para mostrar todos
     */
    fun filterByCategory(category: BusinessCategory?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val businesses = getBusinessListUseCase(category).getOrDefault(emptyList())
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                businesses = businesses,
                selectedCategory = category
            )
        }
    }
}
