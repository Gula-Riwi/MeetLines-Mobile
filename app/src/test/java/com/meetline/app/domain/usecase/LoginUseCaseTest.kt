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
 * Tests unitarios para [LoginUseCase].
 *
 * Verifica el comportamiento del caso de uso de login, incluyendo:
 * - Validación de campos vacíos.
 * - Delegación correcta al repositorio.
 * - Manejo de respuestas exitosas y fallidas.
 *
 * Utiliza MockK para simular el [AuthRepository].
 *
 * @see LoginUseCase Clase bajo prueba.
 */
class LoginUseCaseTest {

    /** Mock del repositorio de autenticación. */
    private lateinit var authRepository: AuthRepository
    
    /** Instancia del caso de uso bajo prueba. */
    private lateinit var loginUseCase: LoginUseCase

    /**
     * Configuración inicial antes de cada test.
     * Crea el mock del repositorio e instancia el caso de uso.
     */
    @Before
    fun setUp() {
        authRepository = mockk()
        loginUseCase = LoginUseCase(authRepository)
    }

    /**
     * Verifica que se retorna error cuando el email está vacío.
     *
     * **Given**: Email vacío y contraseña válida.
     * **When**: Se ejecuta el login.
     * **Then**: Retorna failure con mensaje "El email es requerido".
     */
    @Test
    fun `login with empty email returns failure`() = runTest {
        // Given
        val email = ""
        val password = "password123"

        // When
        val result = loginUseCase(email, password)

        // Then
        assertTrue(result.isFailure)
        assertEquals("El email es requerido", result.exceptionOrNull()?.message)
    }

    /**
     * Verifica que se retorna error cuando el email solo tiene espacios.
     *
     * **Given**: Email con solo espacios en blanco.
     * **When**: Se ejecuta el login.
     * **Then**: Retorna failure con mensaje apropiado.
     */
    @Test
    fun `login with blank email returns failure`() = runTest {
        // Given
        val email = "   "
        val password = "password123"

        // When
        val result = loginUseCase(email, password)

        // Then
        assertTrue(result.isFailure)
        assertEquals("El email es requerido", result.exceptionOrNull()?.message)
    }

    /**
     * Verifica que se retorna error cuando la contraseña está vacía.
     *
     * **Given**: Email válido y contraseña vacía.
     * **When**: Se ejecuta el login.
     * **Then**: Retorna failure con mensaje "La contraseña es requerida".
     */
    @Test
    fun `login with empty password returns failure`() = runTest {
        // Given
        val email = "test@example.com"
        val password = ""

        // When
        val result = loginUseCase(email, password)

        // Then
        assertTrue(result.isFailure)
        assertEquals("La contraseña es requerida", result.exceptionOrNull()?.message)
    }

    /**
     * Verifica que con credenciales válidas se delega al repositorio.
     *
     * **Given**: Email y contraseña válidos, repositorio retorna éxito.
     * **When**: Se ejecuta el login.
     * **Then**: Se llama al repositorio y retorna el usuario.
     */
    @Test
    fun `login with valid credentials calls repository and returns user`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val expectedUser = User(
            id = "user_1",
            name = "Test User",
            email = email,
            phone = "+1234567890"
        )
        
        coEvery { authRepository.login(email, password) } returns Result.success(expectedUser)

        // When
        val result = loginUseCase(email, password)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedUser, result.getOrNull())
        coVerify(exactly = 1) { authRepository.login(email, password) }
    }

    /**
     * Verifica que se propaga el error del repositorio.
     *
     * **Given**: Credenciales válidas, repositorio retorna error.
     * **When**: Se ejecuta el login.
     * **Then**: Se propaga el error del repositorio.
     */
    @Test
    fun `login propagates repository error`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val errorMessage = "Credenciales inválidas"
        
        coEvery { authRepository.login(email, password) } returns 
            Result.failure(Exception(errorMessage))

        // When
        val result = loginUseCase(email, password)

        // Then
        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }
}
