package com.meetline.app.ui.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.meetline.app.domain.model.User
import com.meetline.app.domain.usecase.RegisterUseCase
import io.mockk.coEvery
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
 * Tests unitarios para [RegisterViewModel].
 *
 * Verifica el comportamiento del ViewModel de registro, incluyendo:
 * - Estados de carga durante el registro.
 * - Manejo de registro exitoso.
 * - Manejo de errores de registro.
 * - Limpieza de errores.
 *
 * Utiliza:
 * - [InstantTaskExecutorRule] para LiveData.
 * - [StandardTestDispatcher] para coroutines.
 * - [Turbine] para testear StateFlow.
 *
 * @see RegisterViewModel Clase bajo prueba.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    /** Regla para ejecutar tareas de Architecture Components de forma síncrona. */
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    /** Dispatcher de test para coroutines. */
    private val testDispatcher = StandardTestDispatcher()
    
    /** Mock del caso de uso de registro. */
    private lateinit var registerUseCase: RegisterUseCase
    
    /** ViewModel bajo prueba. */
    private lateinit var viewModel: RegisterViewModel

    /**
     * Configuración inicial antes de cada test.
     */
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        registerUseCase = mockk()
        viewModel = RegisterViewModel(registerUseCase)
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
            assertNull(state.user)
        }
    }

    /**
     * Verifica que se muestra loading durante el registro.
     */
    @Test
    fun `register shows loading state`() = runTest {
        // Given
        val name = "Test User"
        val email = "test@example.com"
        val phone = "+1234567890"
        val password = "password123"
        val user = User(
            id = "user_1",
            name = name,
            email = email,
            phone = phone
        )

        coEvery { registerUseCase(name, email, phone, password) } coAnswers {
            kotlinx.coroutines.delay(100)
            Result.success(user)
        }

        // When
        viewModel.uiState.test {
            skipItems(1) // Estado inicial
            
            viewModel.register(name, email, phone, password)
            
            // Then - Debe mostrar loading primero
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertFalse(loadingState.isSuccess)
            assertNull(loadingState.error)
            
            testDispatcher.scheduler.advanceUntilIdle()
            
            // Luego estado exitoso
            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertTrue(successState.isSuccess)
            assertNull(successState.error)
            assertEquals(user, successState.user)
        }
    }

    /**
     * Verifica que el registro exitoso actualiza el estado correctamente.
     */
    @Test
    fun `register success updates state correctly`() = runTest {
        // Given
        val name = "Test User"
        val email = "test@example.com"
        val phone = "+1234567890"
        val password = "password123"
        val user = User(
            id = "user_1",
            name = name,
            email = email,
            phone = phone
        )

        coEvery { registerUseCase(name, email, phone, password) } returns Result.success(user)

        // When
        viewModel.register(name, email, phone, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertTrue(state.isSuccess)
            assertNull(state.error)
            assertEquals(user, state.user)
        }
    }

    /**
     * Verifica que el error de registro actualiza el estado correctamente.
     */
    @Test
    fun `register failure updates state with error`() = runTest {
        // Given
        val name = "Test User"
        val email = "test@example.com"
        val phone = "+1234567890"
        val password = "password123"
        val errorMessage = "Email already exists"

        coEvery { 
            registerUseCase(name, email, phone, password) 
        } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.register(name, email, phone, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertFalse(state.isSuccess)
            assertEquals(errorMessage, state.error)
            assertNull(state.user)
        }
    }

    /**
     * Verifica que clearError limpia el mensaje de error.
     */
    @Test
    fun `clearError removes error message`() = runTest {
        // Given
        val name = "Test User"
        val email = "test@example.com"
        val phone = "+1234567890"
        val password = "password123"
        val errorMessage = "Registration failed"

        coEvery { 
            registerUseCase(name, email, phone, password) 
        } returns Result.failure(Exception(errorMessage))

        viewModel.register(name, email, phone, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.clearError()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.error)
        }
    }

    /**
     * Verifica que validación de campos vacíos funciona correctamente.
     */
    @Test
    fun `register with empty fields returns validation error`() = runTest {
        // Given
        val errorMessage = "Name cannot be empty"
        
        coEvery { 
            registerUseCase("", "test@example.com", "+1234567890", "password123") 
        } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.register("", "test@example.com", "+1234567890", "password123")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertFalse(state.isSuccess)
            assertEquals(errorMessage, state.error)
        }
    }

    /**
     * Verifica que validación de email inválido funciona correctamente.
     */
    @Test
    fun `register with invalid email returns validation error`() = runTest {
        // Given
        val errorMessage = "Invalid email format"
        
        coEvery { 
            registerUseCase("Test User", "invalid-email", "+1234567890", "password123") 
        } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.register("Test User", "invalid-email", "+1234567890", "password123")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertFalse(state.isSuccess)
            assertEquals(errorMessage, state.error)
        }
    }

    /**
     * Verifica que validación de contraseña corta funciona correctamente.
     */
    @Test
    fun `register with short password returns validation error`() = runTest {
        // Given
        val errorMessage = "Password must be at least 6 characters"
        
        coEvery { 
            registerUseCase("Test User", "test@example.com", "+1234567890", "123") 
        } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.register("Test User", "test@example.com", "+1234567890", "123")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertFalse(state.isSuccess)
            assertEquals(errorMessage, state.error)
        }
    }
}
