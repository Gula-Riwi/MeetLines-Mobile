package com.meetline.app.data.repository

import com.meetline.app.data.local.SessionManager
import com.meetline.app.domain.model.User
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests unitarios para [DefaultAuthRepository].
 *
 * Verifica el comportamiento del repositorio de autenticación:
 * - Login con credenciales válidas e inválidas.
 * - Registro de nuevos usuarios.
 * - Gestión de sesión (logout, getCurrentUser).
 *
 * @see DefaultAuthRepository Clase bajo prueba.
 */
class DefaultAuthRepositoryTest {

    /** Mock del SessionManager. */
    private lateinit var sessionManager: SessionManager
    /** Mock del ApiService. */
    private lateinit var apiService: com.meetline.app.data.remote.MeetLineApiService
    /** Repositorio bajo prueba. */
    private lateinit var repository: DefaultAuthRepository

    @Before
    fun setUp() {
        sessionManager = mockk(relaxed = true)
        apiService = mockk(relaxed = true)
        repository = DefaultAuthRepository(sessionManager, apiService)
    }

    /**
     * Verifica que login exitoso guarda la sesión.
     */
    @Test
    fun `login with valid credentials saves session and returns user`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"

        // When
        val result = repository.login(email, password)

        // Then
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals(email, user?.email)
        
        verify { sessionManager.saveSession(any(), any(), any(), any()) }
    }

    /**
     * Verifica que login con email vacío retorna error.
     */
    @Test
    fun `login with empty email returns failure`() = runTest {
        // Given
        val email = ""
        val password = "password123"

        // When
        val result = repository.login(email, password)

        // Then
        assertTrue(result.isFailure)
        verify(exactly = 0) { sessionManager.saveSession(any(), any(), any(), any()) }
    }

    /**
     * Verifica que login con contraseña corta retorna error.
     */
    @Test
    fun `login with short password returns failure`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "12345" // Menos de 6 caracteres

        // When
        val result = repository.login(email, password)

        // Then
        assertTrue(result.isFailure)
    }

    /**
     * Verifica que registro exitoso guarda la sesión.
     */
    @Test
    fun `register with valid data saves session and returns user`() = runTest {
        // Given
        val name = "Test User"
        val email = "test@example.com"
        val phone = "+1234567890"
        val password = "password123"

        // When
        val result = repository.register(name, email, phone, password)

        // Then
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals(name, user?.name)
        assertEquals(email, user?.email)
        assertEquals(phone, user?.phone)
        
        verify { sessionManager.saveSession(any(), any(), any(), any()) }
    }

    /**
     * Verifica que registro con datos inválidos retorna error.
     */
    @Test
    fun `register with invalid data returns failure`() = runTest {
        // Given
        val name = ""
        val email = "test@example.com"
        val phone = "+1234567890"
        val password = "password123"

        // When
        val result = repository.register(name, email, phone, password)

        // Then
        assertTrue(result.isFailure)
    }

    /**
     * Verifica que logout llama al SessionManager.
     */
    @Test
    fun `logout calls sessionManager logout`() {
        // When
        repository.logout()

        // Then
        verify { sessionManager.logout() }
    }

    /**
     * Verifica que isLoggedIn delega al SessionManager.
     */
    @Test
    fun `isLoggedIn returns sessionManager value`() {
        // Given
        every { sessionManager.isLoggedIn() } returns true

        // When
        val result = repository.isLoggedIn()

        // Then
        assertTrue(result)
        verify { sessionManager.isLoggedIn() }
    }

    /**
     * Verifica que getCurrentUser delega al SessionManager.
     */
    @Test
    fun `getCurrentUser returns sessionManager user`() {
        // Given
        val expectedUser = User(
            id = "user_1",
            name = "Test",
            email = "test@example.com",
            phone = "+1234567890"
        )
        every { sessionManager.getCurrentUser() } returns expectedUser

        // When
        val result = repository.getCurrentUser()

        // Then
        assertEquals(expectedUser, result)
    }

    /**
     * Verifica que updateProfile actualiza y retorna el usuario.
     */
    @Test
    fun `updateProfile updates user and returns success`() = runTest {
        // Given
        val user = User(
            id = "user_1",
            name = "Updated Name",
            email = "test@example.com",
            phone = "+1234567890"
        )

        // When
        val result = repository.updateProfile(user)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(user, result.getOrNull())
        verify { sessionManager.updateUser(user) }
    }

    /**
     * Verifica que requestPasswordReset retorna éxito con email válido.
     */
    @Test
    fun `requestPasswordReset with valid email returns success`() = runTest {
        // Given
        val email = "test@example.com"

        // When
        val result = repository.requestPasswordReset(email)

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull() == true)
    }

    /**
     * Verifica que requestPasswordReset retorna error con email vacío.
     */
    @Test
    fun `requestPasswordReset with empty email returns failure`() = runTest {
        // Given
        val email = ""

        // When
        val result = repository.requestPasswordReset(email)

        // Then
        assertTrue(result.isFailure)
    }
}
