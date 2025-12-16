package com.meetline.app.ui.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.meetline.app.domain.model.User
import com.meetline.app.domain.usecase.GetSessionUseCase
import com.meetline.app.domain.usecase.GetUserProfileUseCase
import com.meetline.app.domain.usecase.LogoutUseCase
import com.meetline.app.domain.usecase.UpdateProfileUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
 * Tests unitarios para [ProfileViewModel].
 *
 * Verifica el comportamiento del ViewModel de perfil, incluyendo:
 * - Carga inicial de datos del perfil.
 * - Modo de edición y cancelación.
 * - Guardado de cambios en el perfil.
 * - Cierre de sesión.
 * - Manejo de errores.
 *
 * @see ProfileViewModel Clase bajo prueba.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var getSessionUseCase: GetSessionUseCase
    private lateinit var getUserProfileUseCase: GetUserProfileUseCase
    private lateinit var updateProfileUseCase: UpdateProfileUseCase
    private lateinit var logoutUseCase: LogoutUseCase
    
    private lateinit var viewModel: ProfileViewModel

    private val testUser = User(
        id = "user_1",
        name = "Test User",
        email = "test@example.com",
        phone = "+1234567890"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        getSessionUseCase = mockk()
        getUserProfileUseCase = mockk()
        updateProfileUseCase = mockk()
        logoutUseCase = mockk(relaxed = true)
        
        // Setup default behavior
        every { getSessionUseCase() } returns testUser
        coEvery { getUserProfileUseCase() } returns Result.success(testUser)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Verifica que el ViewModel carga el perfil del usuario al inicializarse.
     */
    @Test
    fun `init loads user profile`() = runTest {
        // When
        viewModel = ProfileViewModel(
            getSessionUseCase,
            getUserProfileUseCase,
            updateProfileUseCase,
            logoutUseCase
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(testUser, state.user)
            assertFalse(state.isEditing)
        }
    }

    /**
     * Verifica que se muestra el usuario de sesión local durante la carga.
     */
    @Test
    fun `profile loads local user first then updates from server`() = runTest {
        // Given
        val serverUser = testUser.copy(name = "Updated Name")
        coEvery { getUserProfileUseCase() } coAnswers {
            kotlinx.coroutines.delay(100)
            Result.success(serverUser)
        }

        // When
        viewModel = ProfileViewModel(
            getSessionUseCase,
            getUserProfileUseCase,
            updateProfileUseCase,
            logoutUseCase
        )

        // Then
        viewModel.uiState.test {
            // Estado inicial con datos locales
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertEquals(testUser, loadingState.user)
            
            testDispatcher.scheduler.advanceUntilIdle()
            
            // Estado final con datos del servidor
            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
            assertEquals(serverUser, finalState.user)
        }
    }

    /**
     * Verifica que startEditing activa el modo de edición.
     */
    @Test
    fun `startEditing enables editing mode`() = runTest {
        // Given
        viewModel = ProfileViewModel(
            getSessionUseCase,
            getUserProfileUseCase,
            updateProfileUseCase,
            logoutUseCase
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.startEditing()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isEditing)
        }
    }

    /**
     * Verifica que cancelEditing desactiva el modo de edición.
     */
    @Test
    fun `cancelEditing disables editing mode and reloads profile`() = runTest {
        // Given
        viewModel = ProfileViewModel(
            getSessionUseCase,
            getUserProfileUseCase,
            updateProfileUseCase,
            logoutUseCase
        )
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.startEditing()

        // When
        viewModel.cancelEditing()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isEditing)
        }
    }

    /**
     * Verifica que saveProfile actualiza correctamente el perfil.
     */
    @Test
    fun `saveProfile updates profile successfully`() = runTest {
        // Given
        val updatedUser = testUser.copy(
            name = "Updated Name",
            email = "updated@example.com",
            phone = "+9876543210"
        )
        
        coEvery { updateProfileUseCase(updatedUser) } returns Result.success(updatedUser)
        
        viewModel = ProfileViewModel(
            getSessionUseCase,
            getUserProfileUseCase,
            updateProfileUseCase,
            logoutUseCase
        )
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.startEditing()

        // When
        viewModel.saveProfile(
            name = "Updated Name",
            email = "updated@example.com",
            phone = "+9876543210"
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isSaving)
            assertFalse(state.isEditing)
            assertTrue(state.saveSuccess)
            assertEquals(updatedUser, state.user)
            assertNull(state.error)
        }
    }

    /**
     * Verifica que saveProfile muestra estado de carga.
     */
    @Test
    fun `saveProfile shows saving state`() = runTest {
        // Given
        val updatedUser = testUser.copy(name = "Updated Name")
        
        coEvery { updateProfileUseCase(any()) } coAnswers {
            kotlinx.coroutines.delay(100)
            Result.success(updatedUser)
        }
        
        viewModel = ProfileViewModel(
            getSessionUseCase,
            getUserProfileUseCase,
            updateProfileUseCase,
            logoutUseCase
        )
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.startEditing()

        // When
        viewModel.uiState.test {
            skipItems(1) // Estado actual
            
            viewModel.saveProfile("Updated Name", testUser.email, testUser.phone)
            
            // Then
            val savingState = awaitItem()
            assertTrue(savingState.isSaving)
            
            testDispatcher.scheduler.advanceUntilIdle()
            
            val successState = awaitItem()
            assertFalse(successState.isSaving)
            assertTrue(successState.saveSuccess)
        }
    }

    /**
     * Verifica que saveProfile maneja errores correctamente.
     */
    @Test
    fun `saveProfile handles error correctly`() = runTest {
        // Given
        val errorMessage = "Failed to update profile"
        coEvery { updateProfileUseCase(any()) } returns Result.failure(Exception(errorMessage))
        
        viewModel = ProfileViewModel(
            getSessionUseCase,
            getUserProfileUseCase,
            updateProfileUseCase,
            logoutUseCase
        )
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.startEditing()

        // When
        viewModel.saveProfile("New Name", testUser.email, testUser.phone)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isSaving)
            assertTrue(state.isEditing) // Sigue en modo edición
            assertEquals(errorMessage, state.error)
        }
    }

    /**
     * Verifica que logout llama al caso de uso.
     */
    @Test
    fun `logout calls logout use case`() = runTest {
        // Given
        viewModel = ProfileViewModel(
            getSessionUseCase,
            getUserProfileUseCase,
            updateProfileUseCase,
            logoutUseCase
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.logout()

        // Then
        verify { logoutUseCase() }
    }

    /**
     * Verifica que clearMessages limpia los mensajes de éxito y error.
     */
    @Test
    fun `clearMessages clears success and error messages`() = runTest {
        // Given
        val errorMessage = "Test error"
        coEvery { updateProfileUseCase(any()) } returns Result.failure(Exception(errorMessage))
        
        viewModel = ProfileViewModel(
            getSessionUseCase,
            getUserProfileUseCase,
            updateProfileUseCase,
            logoutUseCase
        )
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.startEditing()
        viewModel.saveProfile("Name", testUser.email, testUser.phone)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.clearMessages()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.error)
            assertFalse(state.saveSuccess)
        }
    }

    /**
     * Verifica que el perfil se mantiene si falla la carga del servidor.
     */
    @Test
    fun `profile keeps local data if server fetch fails`() = runTest {
        // Given
        coEvery { getUserProfileUseCase() } returns Result.failure(Exception("Network error"))

        // When
        viewModel = ProfileViewModel(
            getSessionUseCase,
            getUserProfileUseCase,
            updateProfileUseCase,
            logoutUseCase
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(testUser, state.user) // Mantiene usuario local
        }
    }
}
