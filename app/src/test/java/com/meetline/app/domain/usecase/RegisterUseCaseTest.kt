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
 * Tests unitarios para [RegisterUseCase].
 *
 * Verifica el comportamiento del caso de uso de registro, incluyendo:
 * - Validación de campos obligatorios.
 * - Validación de longitud mínima de contraseña.
 * - Delegación correcta al repositorio.
 *
 * @see RegisterUseCase Clase bajo prueba.
 */
class RegisterUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var registerUseCase: RegisterUseCase

    @Before
    fun setUp() {
        authRepository = mockk()
        registerUseCase = RegisterUseCase(authRepository)
    }

    /**
     * Verifica que se retorna error cuando el nombre está vacío.
     */
    @Test
    fun `register with empty name returns failure`() = runTest {
        // Given
        val name = ""
        val email = "test@example.com"
        val phone = "+1234567890"
        val password = "password123"

        // When
        val result = registerUseCase(name, email, phone, password)

        // Then
        assertTrue(result.isFailure)
        assertEquals("El nombre es requerido", result.exceptionOrNull()?.message)
    }

    /**
     * Verifica que se retorna error cuando el email está vacío.
     */
    @Test
    fun `register with empty email returns failure`() = runTest {
        // Given
        val name = "Test User"
        val email = ""
        val phone = "+1234567890"
        val password = "password123"

        // When
        val result = registerUseCase(name, email, phone, password)

        // Then
        assertTrue(result.isFailure)
        assertEquals("El email es requerido", result.exceptionOrNull()?.message)
    }

    /**
     * Verifica que se retorna error cuando la contraseña es muy corta.
     */
    @Test
    fun `register with short password returns failure`() = runTest {
        // Given
        val name = "Test User"
        val email = "test@example.com"
        val phone = "+1234567890"
        val password = "12345" // Menos de 6 caracteres

        // When
        val result = registerUseCase(name, email, phone, password)

        // Then
        assertTrue(result.isFailure)
        assertEquals("La contraseña debe tener al menos 6 caracteres", result.exceptionOrNull()?.message)
    }

    /**
     * Verifica que con datos válidos se delega al repositorio.
     */
    @Test
    fun `register with valid data calls repository and returns user`() = runTest {
        // Given
        val name = "Test User"
        val email = "test@example.com"
        val phone = "+1234567890"
        val password = "password123"
        val expectedUser = User(
            id = "user_1",
            name = name,
            email = email,
            phone = phone
        )
        
        coEvery { authRepository.register(name, email, phone, password) } returns 
            Result.success(expectedUser)

        // When
        val result = registerUseCase(name, email, phone, password)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedUser, result.getOrNull())
        coVerify(exactly = 1) { authRepository.register(name, email, phone, password) }
    }
}
