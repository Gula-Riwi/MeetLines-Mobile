package com.meetline.app.domain.usecase

import com.meetline.app.domain.repository.AppointmentRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests unitarios para [CancelAppointmentUseCase].
 *
 * Verifica que el caso de uso cancela correctamente citas
 * y delega al repositorio con los parámetros adecuados.
 *
 * @see CancelAppointmentUseCase Clase bajo prueba.
 */
class CancelAppointmentUseCaseTest {

    private lateinit var appointmentRepository: AppointmentRepository
    private lateinit var cancelAppointmentUseCase: CancelAppointmentUseCase

    @Before
    fun setUp() {
        appointmentRepository = mockk()
        cancelAppointmentUseCase = CancelAppointmentUseCase(appointmentRepository)
    }

    /**
     * Verifica que se cancela una cita exitosamente.
     */
    @Test
    fun `invoke cancels appointment successfully`() = runTest {
        // Given
        val appointmentId = "apt_123"
        coEvery { 
            appointmentRepository.cancelAppointment(appointmentId) 
        } returns Result.success(true)

        // When
        val result = cancelAppointmentUseCase(appointmentId)

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull() == true)
        coVerify { appointmentRepository.cancelAppointment(appointmentId) }
    }

    /**
     * Verifica que maneja errores del repositorio correctamente.
     */
    @Test
    fun `invoke handles repository error`() = runTest {
        // Given
        val appointmentId = "apt_123"
        val errorMessage = "Cannot cancel appointment"
        
        coEvery { 
            appointmentRepository.cancelAppointment(appointmentId) 
        } returns Result.failure(Exception(errorMessage))

        // When
        val result = cancelAppointmentUseCase(appointmentId)

        // Then
        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }

    /**
     * Verifica que se pasa el ID correcto al repositorio.
     */
    @Test
    fun `invoke passes correct appointment id to repository`() = runTest {
        // Given
        val appointmentId = "apt_456"
        coEvery { 
            appointmentRepository.cancelAppointment(appointmentId) 
        } returns Result.success(true)

        // When
        cancelAppointmentUseCase(appointmentId)

        // Then
        coVerify { appointmentRepository.cancelAppointment(appointmentId) }
    }

    /**
     * Verifica cancelación de múltiples citas.
     */
    @Test
    fun `invoke can cancel multiple appointments`() = runTest {
        // Given
        val appointmentIds = listOf("apt_1", "apt_2", "apt_3")
        
        appointmentIds.forEach { id ->
            coEvery { 
                appointmentRepository.cancelAppointment(id) 
            } returns Result.success(true)
        }

        // When & Then
        appointmentIds.forEach { id ->
            val result = cancelAppointmentUseCase(id)
            assertTrue(result.isSuccess)
            coVerify { appointmentRepository.cancelAppointment(id) }
        }
    }

    /**
     * Verifica que retorna false si la cancelación no fue exitosa.
     */
    @Test
    fun `invoke returns false when cancellation fails`() = runTest {
        // Given
        val appointmentId = "apt_123"
        coEvery { 
            appointmentRepository.cancelAppointment(appointmentId) 
        } returns Result.success(false)

        // When
        val result = cancelAppointmentUseCase(appointmentId)

        // Then
        assertTrue(result.isSuccess)
        assertFalse(result.getOrNull() == true)
    }

    /**
     * Verifica que maneja IDs vacíos correctamente.
     */
    @Test
    fun `invoke handles empty appointment id`() = runTest {
        // Given
        val appointmentId = ""
        coEvery { 
            appointmentRepository.cancelAppointment(appointmentId) 
        } returns Result.failure(Exception("Invalid appointment ID"))

        // When
        val result = cancelAppointmentUseCase(appointmentId)

        // Then
        assertTrue(result.isFailure)
    }
}
