package com.meetline.app.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.meetline.app.domain.model.*
import com.meetline.app.domain.usecase.*
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
 * Tests unitarios para [HomeViewModel].
 *
 * Verifica el comportamiento del ViewModel de la pantalla principal:
 * - Carga inicial de datos.
 * - Funcionalidad de búsqueda.
 * - Manejo de estados de carga.
 *
 * @see HomeViewModel Clase bajo prueba.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    // Mocks de los casos de uso
    private lateinit var getSessionUseCase: GetSessionUseCase
    private lateinit var getAllCategoriesUseCase: GetAllCategoriesUseCase
    private lateinit var getFeaturedBusinessesUseCase: GetFeaturedBusinessesUseCase
    private lateinit var getNearbyBusinessesUseCase: GetNearbyBusinessesUseCase
    private lateinit var getAppointmentsUseCase: GetAppointmentsUseCase
    private lateinit var searchBusinessesUseCase: SearchBusinessesUseCase
    private lateinit var locationManager: com.meetline.app.data.location.LocationManager

    private lateinit var viewModel: HomeViewModel

    /** Usuario de prueba. */
    private val testUser = User(
        id = "user_1",
        name = "Test User",
        email = "test@example.com",
        phone = "+1234567890"
    )

    /** Negocio de prueba. */
    private val testBusiness = Business(
        id = "biz_1",
        name = "Test Business",
        description = "Description",
        category = BusinessCategory.BARBERSHOP,
        imageUrl = "https://example.com/image.jpg",
        rating = 4.5f,
        reviewCount = 100,
        address = "Test Address",
        distance = "1.0 km",
        isOpen = true,
        openingHours = "9:00 - 18:00",
        professionals = emptyList(),
        services = emptyList()
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getSessionUseCase = mockk()
        getAllCategoriesUseCase = mockk()
        getFeaturedBusinessesUseCase = mockk()
        getNearbyBusinessesUseCase = mockk()
        getAppointmentsUseCase = mockk()
        searchBusinessesUseCase = mockk()
        locationManager = mockk(relaxed = true)

        // Configurar comportamiento por defecto de los mocks
        every { getSessionUseCase() } returns testUser
        every { getAllCategoriesUseCase() } returns BusinessCategory.entries
        coEvery { getFeaturedBusinessesUseCase() } returns Result.success(listOf(testBusiness))
        coEvery { getNearbyBusinessesUseCase() } returns Result.success(listOf(testBusiness))
        coEvery { getAppointmentsUseCase.getUpcoming() } returns Result.success(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Crea el ViewModel después de configurar los mocks.
     * Necesario porque el ViewModel carga datos en init.
     */
    private fun createViewModel() {
        viewModel = HomeViewModel(
            getSessionUseCase = getSessionUseCase,
            getAllCategoriesUseCase = getAllCategoriesUseCase,
            getFeaturedBusinessesUseCase = getFeaturedBusinessesUseCase,
            getNearbyBusinessesUseCase = getNearbyBusinessesUseCase,
            getAppointmentsUseCase = getAppointmentsUseCase,
            searchBusinessesUseCase = searchBusinessesUseCase,
            locationManager = locationManager
        )
    }

    /**
     * Verifica que la búsqueda actualiza el estado correctamente.
     */
    @Test
    fun `search updates search query and results`() = runTest {
        // Given
        val searchQuery = "barber"
        val searchResults = listOf(testBusiness)
        
        coEvery { searchBusinessesUseCase(searchQuery) } returns Result.success(searchResults)
        
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.search(searchQuery)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(searchQuery, state.searchQuery)
            assertEquals(searchResults, state.searchResults)
            assertFalse(state.isSearching)
        }
    }

    /**
     * Verifica que limpiar búsqueda resetea los resultados.
     */
    @Test
    fun `clearSearch resets search state`() = runTest {
        // Given
        val searchQuery = "barber"
        coEvery { searchBusinessesUseCase(searchQuery) } returns Result.success(listOf(testBusiness))
        
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.search(searchQuery)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.clearSearch()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("", state.searchQuery)
            assertTrue(state.searchResults.isEmpty())
            assertFalse(state.isSearching)
        }
    }

    /**
     * Verifica que búsqueda vacía no llama al caso de uso.
     */
    @Test
    fun `empty search query clears results without calling use case`() = runTest {
        // Given
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.search("")

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("", state.searchQuery)
            assertTrue(state.searchResults.isEmpty())
            assertFalse(state.isSearching)
        }
    }

    /**
     * Verifica que refresh recarga todos los datos.
     */
    @Test
    fun `refresh reloads all data`() = runTest {
        // Given
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val newBusiness = testBusiness.copy(id = "biz_2", name = "New Business")
        coEvery { getFeaturedBusinessesUseCase() } returns Result.success(listOf(newBusiness))
        coEvery { getNearbyBusinessesUseCase() } returns Result.success(listOf(newBusiness))

        // When
        viewModel.refresh()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("New Business", state.featuredBusinesses.first().name)
        }
    }
}
