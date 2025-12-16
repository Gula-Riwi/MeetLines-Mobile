package com.meetline.app.ui.business

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.meetline.app.domain.model.Business
import com.meetline.app.domain.model.BusinessCategory
import com.meetline.app.domain.usecase.GetAllCategoriesUseCase
import com.meetline.app.domain.usecase.GetBusinessListUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Tests unitarios para [BusinessListViewModel].
 *
 * Verifica el comportamiento del ViewModel de lista de negocios, incluyendo:
 * - Carga inicial de negocios y categorías.
 * - Filtrado por categoría.
 * - Navegación con categoría preseleccionada.
 * - Manejo de estados de carga.
 *
 * @see BusinessListViewModel Clase bajo prueba.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BusinessListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var getBusinessListUseCase: GetBusinessListUseCase
    private lateinit var getAllCategoriesUseCase: GetAllCategoriesUseCase
    private lateinit var savedStateHandle: SavedStateHandle
    
    private lateinit var viewModel: BusinessListViewModel

    private val testBusiness = Business(
        id = "biz_1",
        name = "Test Business",
        description = "Test Description",
        category = BusinessCategory.BARBERSHOP,
        imageUrl = "https://example.com/image.jpg",
        rating = 4.5f,
        reviewCount = 100,
        address = "123 Test St",
        distance = "1.0 km",
        isOpen = true,
        openingHours = "9:00 - 18:00",
        professionals = emptyList(),
        services = emptyList()
    )

    private val testCategories = listOf(
        BusinessCategory.BARBERSHOP,
        BusinessCategory.BEAUTY_SALON,
        BusinessCategory.SPA
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        getBusinessListUseCase = mockk()
        getAllCategoriesUseCase = mockk()
        savedStateHandle = mockk()
        
        // Default behavior
        every { savedStateHandle.get<String>("category") } returns null
        every { getAllCategoriesUseCase() } returns testCategories
        coEvery { getBusinessListUseCase(any()) } returns Result.success(listOf(testBusiness))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Verifica que el ViewModel carga negocios al inicializarse.
     */
    @Test
    fun `init loads businesses and categories`() = runTest {
        // When
        viewModel = BusinessListViewModel(
            getBusinessListUseCase,
            getAllCategoriesUseCase,
            savedStateHandle
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(listOf(testBusiness), state.businesses)
            assertEquals(testCategories, state.categories)
            assertNull(state.selectedCategory)
        }
    }

    /**
     * Verifica que se carga con categoría preseleccionada desde navegación.
     */
    @Test
    fun `init with category from navigation loads filtered businesses`() = runTest {
        // Given
        val categoryName = "Barbería"
        val filteredBusinesses = listOf(
            testBusiness.copy(category = BusinessCategory.BARBERSHOP)
        )
        
        every { savedStateHandle.get<String>("category") } returns categoryName
        coEvery { 
            getBusinessListUseCase(BusinessCategory.BARBERSHOP) 
        } returns Result.success(filteredBusinesses)

        // When
        viewModel = BusinessListViewModel(
            getBusinessListUseCase,
            getAllCategoriesUseCase,
            savedStateHandle
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(filteredBusinesses, state.businesses)
            assertEquals(BusinessCategory.BARBERSHOP, state.selectedCategory)
        }
    }

    /**
     * Verifica que filterByCategory filtra correctamente.
     */
    @Test
    fun `filterByCategory filters businesses by category`() = runTest {
        // Given
        val spaBusinesses = listOf(
            testBusiness.copy(id = "biz_2", category = BusinessCategory.SPA)
        )
        
        coEvery { 
            getBusinessListUseCase(BusinessCategory.SPA) 
        } returns Result.success(spaBusinesses)
        
        viewModel = BusinessListViewModel(
            getBusinessListUseCase,
            getAllCategoriesUseCase,
            savedStateHandle
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.filterByCategory(BusinessCategory.SPA)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(spaBusinesses, state.businesses)
            assertEquals(BusinessCategory.SPA, state.selectedCategory)
        }
    }

    /**
     * Verifica que filterByCategory con null muestra todos los negocios.
     */
    @Test
    fun `filterByCategory with null shows all businesses`() = runTest {
        // Given
        val allBusinesses = listOf(
            testBusiness,
            testBusiness.copy(id = "biz_2", category = BusinessCategory.BEAUTY_SALON)
        )
        
        coEvery { getBusinessListUseCase(null) } returns Result.success(allBusinesses)
        
        viewModel = BusinessListViewModel(
            getBusinessListUseCase,
            getAllCategoriesUseCase,
            savedStateHandle
        )
        testDispatcher.scheduler.advanceUntilIdle()
        
        // First filter by category
        viewModel.filterByCategory(BusinessCategory.BARBERSHOP)
        testDispatcher.scheduler.advanceUntilIdle()

        // When - Clear filter
        viewModel.filterByCategory(null)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(allBusinesses, state.businesses)
            assertNull(state.selectedCategory)
        }
    }

    /**
     * Verifica que se muestra loading durante el filtrado.
     */
    @Test
    fun `filterByCategory shows loading state`() = runTest {
        // Given
        coEvery { getBusinessListUseCase(any()) } coAnswers {
            kotlinx.coroutines.delay(100)
            Result.success(listOf(testBusiness))
        }
        
        viewModel = BusinessListViewModel(
            getBusinessListUseCase,
            getAllCategoriesUseCase,
            savedStateHandle
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.uiState.test {
            skipItems(1) // Estado inicial
            
            viewModel.filterByCategory(BusinessCategory.BARBERSHOP)
            
            // Then
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            
            testDispatcher.scheduler.advanceUntilIdle()
            
            val successState = awaitItem()
            assertFalse(successState.isLoading)
        }
    }

    /**
     * Verifica que maneja lista vacía correctamente.
     */
    @Test
    fun `loads empty list when no businesses available`() = runTest {
        // Given
        coEvery { getBusinessListUseCase(any()) } returns Result.success(emptyList())
        
        // When
        viewModel = BusinessListViewModel(
            getBusinessListUseCase,
            getAllCategoriesUseCase,
            savedStateHandle
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertTrue(state.businesses.isEmpty())
        }
    }

    /**
     * Verifica que maneja errores en la carga.
     */
    @Test
    fun `handles error in business loading`() = runTest {
        // Given
        coEvery { getBusinessListUseCase(any()) } returns Result.failure(Exception("Network error"))
        
        // When
        viewModel = BusinessListViewModel(
            getBusinessListUseCase,
            getAllCategoriesUseCase,
            savedStateHandle
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertTrue(state.businesses.isEmpty()) // Returns empty list on error
        }
    }
}
