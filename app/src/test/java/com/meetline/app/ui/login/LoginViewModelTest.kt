package com.meetline.app.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.meetline.app.domain.model.User
import com.meetline.app.domain.usecase.GetSessionUseCase
import com.meetline.app.domain.usecase.LoginUseCase
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
 * Tests unitarios para [LoginViewModel].
 *
 * Verifica el comportamiento del ViewModel de login, incluyendo:
 * - Estados de carga durante el login.
 * - Manejo de login exitoso.
 * - Manejo de errores de login.
 * - Limpieza de errores.
 *
 * Utiliza:
 * - [InstantTaskExecutorRule] para LiveData.
 * - [StandardTestDispatcher] para coroutines.
 * - [Turbine] para testear StateFlow.
 *
 * @see LoginViewModel Clase bajo prueba.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    /** Regla para ejecutar tareas de Architecture Components de forma síncrona. */
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    /** Dispatcher de test para coroutines. */
    private val testDispatcher = StandardTestDispatcher()
    
    /** Mock del caso de uso de login. */
    private lateinit var loginUseCase: LoginUseCase
    
    /** Mock del caso de uso de sesión. */
    private lateinit var getSessionUseCase: GetSessionUseCase
    
    /** ViewModel bajo prueba. */
    private lateinit var viewModel: LoginViewModel

    /**
     * Configuración inicial antes de cada test.
     */
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        loginUseCase = mockk()
        getSessionUseCase = mockk()
        
        // Por defecto, no hay sesión activa
        every { getSessionUseCase() } returns null
        
        viewModel = LoginViewModel(loginUseCase, getSessionUseCase)
    }

    /**
     * Limpieza después de cada test.
     */
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Verifica el estado inicial del ViewModel.
     */
    @Test
    fun `initial state is correct`() = runTest {
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertFalse(state.isSuccess)
            assertNull(state.error)
        }
    }

    /**
     * Verifica que se muestra loading durante el login.
     */
    @Test
    fun `login shows loading state`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val user = User(
            id = "user_1",
            name = "Test",
            email = email,
            phone = "+1234567890"
        )
        
        coEvery { loginUseCase(email, password) } returns Result.success(user)

        // When & Then
        viewModel.uiState.test {
            // Estado inicial
            val initialState = awaitItem()
            assertFalse(initialState.isLoading)
            
            // Ejecutar login
            viewModel.login(email, password)
            
            // Estado de carga
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            
            // Avanzar el dispatcher
            testDispatcher.scheduler.advanceUntilIdle()
            
            // Estado de éxito
            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertTrue(successState.isSuccess)
        }
    }

    /**
     * Verifica que un login exitoso actualiza el estado correctamente.
     */
    @Test
    fun `successful login updates state to success`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val user = User(
            id = "user_1",
            name = "Test User",
            email = email,
            phone = "+1234567890"
        )
        
        coEvery { loginUseCase(email, password) } returns Result.success(user)

        // When
        viewModel.login(email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isSuccess)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }
    }

    /**
     * Verifica que un login fallido muestra el error.
     */
    @Test
    fun `failed login shows error message`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "wrongpassword"
        val errorMessage = "Credenciales inválidas"
        
        coEvery { loginUseCase(email, password) } returns 
            Result.failure(Exception(errorMessage))

        // When
        viewModel.login(email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isSuccess)
            assertFalse(state.isLoading)
            assertEquals(errorMessage, state.error)
        }
    }

    /**
     * Verifica que clearError limpia el mensaje de error.
     */
    @Test
    fun `clearError clears error message`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "wrongpassword"
        
        coEvery { loginUseCase(email, password) } returns 
            Result.failure(Exception("Error"))

        // Primero provocamos un error
        viewModel.login(email, password)
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
