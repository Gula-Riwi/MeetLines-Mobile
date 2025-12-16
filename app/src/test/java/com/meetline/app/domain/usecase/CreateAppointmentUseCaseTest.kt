package com.meetline.app.domain.usecase

import com.meetline.app.domain.model.*
import com.meetline.app.domain.repository.AppointmentRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests unitarios para [CreateAppointmentUseCase].
 *
 * Verifica que el caso de uso crea correctamente citas y
 * delega al repositorio con los parámetros adecuados.
 *
 * @see CreateAppointmentUseCase Clase bajo prueba.
 */
class CreateAppointmentUseCaseTest {

    private lateinit var appointmentRepository: AppointmentRepository
    private lateinit var createAppointmentUseCase: CreateAppointmentUseCase

    private val testBusiness = Business(
        id = "biz_1",
        name = "Test Business",
        description = "Description",
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
        name = "John Doe",
        role = "Barber",
        rating = 4.8f,
        reviewCount = 50,
        imageUrl = "https://example.com/prof.jpg"
    )

    private val testService = Service(
        id = "serv_1",
        name = "Haircut",
        description = "Professional haircut",
        price = 25.0,
        duration = 30,
        imageUrl = null
    )

    @Before
    fun setUp() {
        appointmentRepository = mockk()
        createAppointmentUseCase = CreateAppointmentUseCase(appointmentRepository)
    }

    /**
     * Verifica que se crea una cita exitosamente.
     */
    @Test
    fun `invoke creates appointment successfully`() = runTest {
        // Given
        val date = System.currentTimeMillis()
        val time = "10:00"
        val notes = "Please be on time"
        
        val expectedAppointment = Appointment(
            id = "apt_1",
            userId = "user_1",
            business = testBusiness,
            professional = testProfessional,
            service = testService,
            date = date,
            time = time,
            status = AppointmentStatus.PENDING,
            notes = notes
        )
        
        coEvery { 
            appointmentRepository.createAppointment(
                testBusiness, 
                testProfessional, 
                testService, 
                date, 
                time, 
                notes
            ) 
        } returns Result.success(expectedAppointment)

        // When
        val result = createAppointmentUseCase(
            testBusiness,
            testProfessional,
            testService,
            date,
            time,
            notes
        )

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedAppointment, result.getOrNull())
        coVerify { 
            appointmentRepository.createAppointment(
                testBusiness, 
                testProfessional, 
                testService, 
                date, 
                time, 
                notes
            ) 
        }
    }

    /**
     * Verifica que se puede crear una cita sin notas.
     */
    @Test
    fun `invoke creates appointment without notes`() = runTest {
        // Given
        val date = System.currentTimeMillis()
        val time = "10:00"
        
        val expectedAppointment = Appointment(
            id = "apt_1",
            userId = "user_1",
            business = testBusiness,
            professional = testProfessional,
            service = testService,
            date = date,
            time = time,
            status = AppointmentStatus.PENDING,
            notes = null
        )
        
        coEvery { 
            appointmentRepository.createAppointment(
                testBusiness, 
                testProfessional, 
                testService, 
                date, 
                time, 
                null
            ) 
        } returns Result.success(expectedAppointment)

        // When
        val result = createAppointmentUseCase(
            testBusiness,
            testProfessional,
            testService,
            date,
            time,
            null
        )

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedAppointment, result.getOrNull())
        assertNull(result.getOrNull()?.notes)
    }

    /**
     * Verifica que maneja errores del repositorio correctamente.
     */
    @Test
    fun `invoke handles repository error`() = runTest {
        // Given
        val date = System.currentTimeMillis()
        val time = "10:00"
        val errorMessage = "Failed to create appointment"
        
        coEvery { 
            appointmentRepository.createAppointment(any(), any(), any(), any(), any(), any()) 
        } returns Result.failure(Exception(errorMessage))

        // When
        val result = createAppointmentUseCase(
            testBusiness,
            testProfessional,
            testService,
            date,
            time,
            null
        )

        // Then
        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }

    /**
     * Verifica que se pasan todos los parámetros correctamente al repositorio.
     */
    @Test
    fun `invoke passes all parameters to repository`() = runTest {
        // Given
        val date = 1234567890L
        val time = "14:30"
        val notes = "Important notes"
        
        val appointment = Appointment(
            id = "apt_1",
            userId = "user_1",
            business = testBusiness,
            professional = testProfessional,
            service = testService,
            date = date,
            time = time,
            status = AppointmentStatus.PENDING,
            notes = notes
        )
        
        coEvery { 
            appointmentRepository.createAppointment(
                testBusiness, 
                testProfessional, 
                testService, 
                date, 
                time, 
                notes
            ) 
        } returns Result.success(appointment)

        // When
        createAppointmentUseCase(
            testBusiness,
            testProfessional,
            testService,
            date,
            time,
            notes
        )

        // Then
        coVerify { 
            appointmentRepository.createAppointment(
                testBusiness, 
                testProfessional, 
                testService, 
                date, 
                time, 
                notes
            ) 
        }
    }

    /**
     * Verifica la creación de cita con diferentes horarios.
     */
    @Test
    fun `invoke creates appointments with different times`() = runTest {
        // Given
        val times = listOf("09:00", "12:30", "15:45", "18:00")
        val date = System.currentTimeMillis()
        
        times.forEach { time ->
            val appointment = Appointment(
                id = "apt_${time.replace(":", "")}",
                userId = "user_1",
                business = testBusiness,
                professional = testProfessional,
                service = testService,
                date = date,
                time = time,
                status = AppointmentStatus.PENDING,
                notes = null
            )
            
            coEvery { 
                appointmentRepository.createAppointment(
                    testBusiness, 
                    testProfessional, 
                    testService, 
                    date, 
                    time, 
                    null
                ) 
            } returns Result.success(appointment)

            // When
            val result = createAppointmentUseCase(
                testBusiness,
                testProfessional,
                testService,
                date,
                time,
                null
            )

            // Then
            assertTrue(result.isSuccess)
        }
    }
}
