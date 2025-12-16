package com.meetline.app.domain.usecase

import com.meetline.app.domain.model.User
import com.meetline.app.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests unitarios para [UpdateProfileUseCase].
 *
 * Verifica que el caso de uso actualiza correctamente el perfil
 * y delega al repositorio con los parámetros adecuados.
 *
 * @see UpdateProfileUseCase Clase bajo prueba.
 */
class UpdateProfileUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var updateProfileUseCase: UpdateProfileUseCase

    private val testUser = User(
        id = "user_1",
        name = "John Doe",
        email = "john@example.com",
        phone = "+1234567890"
    )

    @Before
    fun setUp() {
        authRepository = mockk()
        updateProfileUseCase = UpdateProfileUseCase(authRepository)
    }

    /**
     * Verifica que se actualiza el perfil exitosamente.
     */
    @Test
    fun `invoke updates profile successfully`() = runTest {
        // Given
        val updatedUser = testUser.copy(name = "Jane Doe")
        coEvery { 
            authRepository.updateProfile(updatedUser) 
        } returns Result.success(updatedUser)

        // When
        val result = updateProfileUseCase(updatedUser)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(updatedUser, result.getOrNull())
        coVerify { authRepository.updateProfile(updatedUser) }
    }

    /**
     * Verifica que maneja errores del repositorio correctamente.
     */
    @Test
    fun `invoke handles repository error`() = runTest {
        // Given
        val updatedUser = testUser.copy(email = "newemail@example.com")
        val errorMessage = "Failed to update profile"
        
        coEvery { 
            authRepository.updateProfile(updatedUser) 
        } returns Result.failure(Exception(errorMessage))

        // When
        val result = updateProfileUseCase(updatedUser)

        // Then
        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }

    /**
     * Verifica actualización de nombre.
     */
    @Test
    fun `invoke updates user name`() = runTest {
        // Given
        val updatedUser = testUser.copy(name = "New Name")
        coEvery { 
            authRepository.updateProfile(updatedUser) 
        } returns Result.success(updatedUser)

        // When
        val result = updateProfileUseCase(updatedUser)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("New Name", result.getOrNull()?.name)
    }

    /**
     * Verifica actualización de email.
     */
    @Test
    fun `invoke updates user email`() = runTest {
        // Given
        val updatedUser = testUser.copy(email = "newemail@example.com")
        coEvery { 
            authRepository.updateProfile(updatedUser) 
        } returns Result.success(updatedUser)

        // When
        val result = updateProfileUseCase(updatedUser)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("newemail@example.com", result.getOrNull()?.email)
    }

    /**
     * Verifica actualización de teléfono.
     */
    @Test
    fun `invoke updates user phone`() = runTest {
        // Given
        val updatedUser = testUser.copy(phone = "+9876543210")
        coEvery { 
            authRepository.updateProfile(updatedUser) 
        } returns Result.success(updatedUser)

        // When
        val result = updateProfileUseCase(updatedUser)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("+9876543210", result.getOrNull()?.phone)
    }

    /**
     * Verifica actualización de múltiples campos.
     */
    @Test
    fun `invoke updates multiple fields at once`() = runTest {
        // Given
        val updatedUser = testUser.copy(
            name = "Updated Name",
            email = "updated@example.com",
            phone = "+9999999999"
        )
        
        coEvery { 
            authRepository.updateProfile(updatedUser) 
        } returns Result.success(updatedUser)

        // When
        val result = updateProfileUseCase(updatedUser)

        // Then
        assertTrue(result.isSuccess)
        val resultUser = result.getOrNull()
        assertEquals("Updated Name", resultUser?.name)
        assertEquals("updated@example.com", resultUser?.email)
        assertEquals("+9999999999", resultUser?.phone)
    }

    /**
     * Verifica que se pasa el usuario correcto al repositorio.
     */
    @Test
    fun `invoke passes correct user to repository`() = runTest {
        // Given
        val updatedUser = testUser.copy(name = "Test Name")
        coEvery { 
            authRepository.updateProfile(updatedUser) 
        } returns Result.success(updatedUser)

        // When
        updateProfileUseCase(updatedUser)

        // Then
        coVerify { authRepository.updateProfile(updatedUser) }
    }

    /**
     * Verifica que mantiene el ID del usuario sin cambios.
     */
    @Test
    fun `invoke maintains user id unchanged`() = runTest {
        // Given
        val originalId = "user_original_123"
        val user = testUser.copy(id = originalId, name = "Updated Name")
        
        coEvery { 
            authRepository.updateProfile(user) 
        } returns Result.success(user)

        // When
        val result = updateProfileUseCase(user)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(originalId, result.getOrNull()?.id)
    }
}
