package com.meetline.app.ui.appointments

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.meetline.app.domain.model.Appointment
import com.meetline.app.domain.model.AppointmentStatus
import com.meetline.app.domain.model.Business
import com.meetline.app.domain.model.BusinessCategory
import com.meetline.app.domain.model.Professional
import com.meetline.app.domain.model.Service
import com.meetline.app.domain.usecase.CancelAppointmentUseCase
import com.meetline.app.domain.usecase.GetAppointmentsUseCase
import com.meetline.app.domain.usecase.GetMyActiveAppointmentsUseCase
import com.meetline.app.domain.usecase.GetMyAppointmentHistoryUseCase
import io.mockk.coEvery
import io.mockk.coVerify
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
 * Tests unitarios para [AppointmentsViewModel].
 *
 * Verifica el comportamiento del ViewModel de citas, incluyendo:
 * - Carga de citas activas y historial.
 * - Cancelación de citas.
 * - Manejo de errores y sesiones expiradas.
 * - Navegación entre pestañas.
 *
 * @see AppointmentsViewModel Clase bajo prueba.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AppointmentsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var getMyActiveAppointmentsUseCase: GetMyActiveAppointmentsUseCase
    private lateinit var getMyAppointmentHistoryUseCase: GetMyAppointmentHistoryUseCase
    private lateinit var getAppointmentsUseCase: GetAppointmentsUseCase
    private lateinit var cancelAppointmentUseCase: CancelAppointmentUseCase
    
    private lateinit var viewModel: AppointmentsViewModel

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

    private val testProfessional = Professional(
        id = "prof_1",
        name = "Dr. Test",
        role = "Doctor",
        rating = 4.8f,
        reviewCount = 50,
        imageUrl = "https://example.com/prof.jpg"
    )

    private val testService = Service(
        id = "serv_1",
        name = "Test Service",
        description = "Test service description",
        price = 50.0,
        duration = 60,
        imageUrl = null
    )

    private val testAppointment = Appointment(
        id = "apt_1",
        userId = "user_1",
        business = testBusiness,
        professional = testProfessional,
        service = testService,
        date = System.currentTimeMillis() + 86400000, // Tomorrow
        time = "10:00",
        status = AppointmentStatus.PENDING,
        notes = "Test notes"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        getMyActiveAppointmentsUseCase = mockk()
        getMyAppointmentHistoryUseCase = mockk()
        getAppointmentsUseCase = mockk()
        cancelAppointmentUseCase = mockk()
        
        // Default behavior
        coEvery { getMyActiveAppointmentsUseCase() } returns Result.success(emptyList())
        coEvery { getMyAppointmentHistoryUseCase() } returns Result.success(emptyList())
        coEvery { getAppointmentsUseCase.getUpcoming() } returns Result.success(emptyList())
        coEvery { getAppointmentsUseCase.getPast() } returns Result.success(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Verifica que el ViewModel carga citas activas al inicializarse.
     */
    @Test
    fun `init loads active appointments`() = runTest {
        // Given
        val appointments = listOf(testAppointment)
        coEvery { getMyActiveAppointmentsUseCase() } returns Result.success(appointments)

        // When
        viewModel = AppointmentsViewModel(
            getMyActiveAppointmentsUseCase,
            getMyAppointmentHistoryUseCase,
            getAppointmentsUseCase,
            cancelAppointmentUseCase
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(appointments, state.activeAppointments)
            assertNull(state.error)
        }
    }

    /**
     * Verifica que se muestra loading durante la carga de citas activas.
     */
    @Test
    fun `loadMyActiveAppointments shows loading state`() = runTest {
        // Given
        coEvery { getMyActiveAppointmentsUseCase() } coAnswers {
            kotlinx.coroutines.delay(100)
            Result.success(listOf(testAppointment))
        }

        // When
        viewModel = AppointmentsViewModel(
            getMyActiveAppointmentsUseCase,
            getMyAppointmentHistoryUseCase,
            getAppointmentsUseCase,
            cancelAppointmentUseCase
        )

        viewModel.uiState.test {
            // Loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            
            testDispatcher.scheduler.advanceUntilIdle()
            
            // Success state
            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertEquals(1, successState.activeAppointments.size)
        }
    }

    /**
     * Verifica que loadMyAppointmentHistory carga el historial correctamente.
     */
    @Test
    fun `loadMyAppointmentHistory loads history successfully`() = runTest {
        // Given
        val historyAppointments = listOf(
            testAppointment,
            testAppointment.copy(id = "apt_2", status = AppointmentStatus.COMPLETED)
        )
        coEvery { getMyAppointmentHistoryUseCase() } returns Result.success(historyAppointments)
        
        viewModel = AppointmentsViewModel(
            getMyActiveAppointmentsUseCase,
            getMyAppointmentHistoryUseCase,
            getAppointmentsUseCase,
            cancelAppointmentUseCase
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.loadMyAppointmentHistory()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(historyAppointments, state.historyAppointments)
            assertNull(state.error)
        }
    }

    /**
     * Verifica que los errores se manejan correctamente.
     */
    @Test
    fun `loadMyActiveAppointments handles error correctly`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { getMyActiveAppointmentsUseCase() } returns Result.failure(Exception(errorMessage))

        // When
        viewModel = AppointmentsViewModel(
            getMyActiveAppointmentsUseCase,
            getMyAppointmentHistoryUseCase,
            getAppointmentsUseCase,
            cancelAppointmentUseCase
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(errorMessage, state.error)
            assertFalse(state.sessionExpired)
        }
    }

    /**
     * Verifica que se detecta sesión expirada.
     */
    @Test
    fun `loadMyActiveAppointments detects expired session`() = runTest {
        // Given
        val errorMessage = "Sesión expirada"
        coEvery { getMyActiveAppointmentsUseCase() } returns Result.failure(Exception(errorMessage))

        // When
        viewModel = AppointmentsViewModel(
            getMyActiveAppointmentsUseCase,
            getMyAppointmentHistoryUseCase,
            getAppointmentsUseCase,
            cancelAppointmentUseCase
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertTrue(state.sessionExpired)
            assertEquals(errorMessage, state.error)
        }
    }

    /**
     * Verifica que selectTab actualiza el índice de pestaña seleccionada.
     */
    @Test
    fun `selectTab updates selected tab index`() = runTest {
        // Given
        viewModel = AppointmentsViewModel(
            getMyActiveAppointmentsUseCase,
            getMyAppointmentHistoryUseCase,
            getAppointmentsUseCase,
            cancelAppointmentUseCase
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.selectTab(1)

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.selectedTab)
        }
    }

    /**
     * Verifica que cancelAppointment cancela una cita correctamente.
     */
    @Test
    fun `cancelAppointment cancels appointment successfully`() = runTest {
        // Given
        val appointmentId = "apt_1"
        val updatedAppointments = listOf(testAppointment.copy(status = AppointmentStatus.CANCELLED))
        
        coEvery { cancelAppointmentUseCase(appointmentId) } returns Result.success(true)
        coEvery { getMyActiveAppointmentsUseCase() } returns Result.success(updatedAppointments)
        
        viewModel = AppointmentsViewModel(
            getMyActiveAppointmentsUseCase,
            getMyAppointmentHistoryUseCase,
            getAppointmentsUseCase,
            cancelAppointmentUseCase
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.cancelAppointment(appointmentId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { cancelAppointmentUseCase(appointmentId) }
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            // Las citas activas se recargan después de cancelar
        }
    }

    /**
     * Verifica que cancelAppointment maneja errores correctamente.
     */
    @Test
    fun `cancelAppointment handles error correctly`() = runTest {
        // Given
        val appointmentId = "apt_1"
        val errorMessage = "Cannot cancel appointment"
        
        coEvery { cancelAppointmentUseCase(appointmentId) } returns Result.failure(Exception(errorMessage))
        
        viewModel = AppointmentsViewModel(
            getMyActiveAppointmentsUseCase,
            getMyAppointmentHistoryUseCase,
            getAppointmentsUseCase,
            cancelAppointmentUseCase
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.cancelAppointment(appointmentId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(errorMessage, state.error)
        }
    }

    /**
     * Verifica que clearError limpia el mensaje de error.
     */
    @Test
    fun `clearError removes error message`() = runTest {
        // Given
        val errorMessage = "Test error"
        coEvery { getMyActiveAppointmentsUseCase() } returns Result.failure(Exception(errorMessage))
        
        viewModel = AppointmentsViewModel(
            getMyActiveAppointmentsUseCase,
            getMyAppointmentHistoryUseCase,
            getAppointmentsUseCase,
            cancelAppointmentUseCase
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.clearError()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.error)
        }
    }
}
