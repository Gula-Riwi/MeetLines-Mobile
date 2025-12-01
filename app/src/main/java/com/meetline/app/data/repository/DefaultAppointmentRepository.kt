package com.meetline.app.data.repository

import com.meetline.app.data.local.MockData
import com.meetline.app.domain.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import java.util.Calendar

import com.meetline.app.domain.repository.AppointmentRepository

/**
 * Repositorio para operaciones relacionadas con citas/agendamientos.
 * 
 * Gestiona todas las operaciones CRUD (Crear, Leer, Actualizar, Eliminar) de citas.
 * Actualmente utiliza datos mock almacenados localmente, pero está diseñado para
 * ser fácilmente adaptable a un backend real.
 * 
 * Utiliza StateFlow para permitir que la UI observe cambios en tiempo real
 * en la lista de citas.
 * 
 * En producción, este repositorio se comunicaría con una API REST o GraphQL
 * para persistir las citas en un servidor.
 */
@Singleton
class DefaultAppointmentRepository @Inject constructor() : AppointmentRepository {

    /**
     * Lista mutable de citas almacenadas localmente.
     * En producción, esto vendría del backend.
     */
    private val _appointments = MutableStateFlow<List<Appointment>>(generateMockAppointments())
    
    /**
     * StateFlow público para observar cambios en las citas.
     * La UI puede suscribirse a este flow para recibir actualizaciones automáticas.
     */
    override val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()

    /**
     * Genera citas de ejemplo para demostración.
     * 
     * Crea citas en diferentes estados y fechas (pasadas y futuras)
     * para mostrar todas las funcionalidades de la aplicación.
     * 
     * @return Lista de citas mock con diferentes estados y fechas
     */
    private fun generateMockAppointments(): List<Appointment> {
        val calendar = Calendar.getInstance()
        val today = calendar.timeInMillis
        
        calendar.add(Calendar.DAY_OF_MONTH, 2)
        val inTwoDays = calendar.timeInMillis
        
        calendar.add(Calendar.DAY_OF_MONTH, 5)
        val inAWeek = calendar.timeInMillis
        
        calendar.time = java.util.Date()
        calendar.add(Calendar.DAY_OF_MONTH, -3)
        val threeDaysAgo = calendar.timeInMillis
        
        calendar.add(Calendar.DAY_OF_MONTH, -7)
        val tenDaysAgo = calendar.timeInMillis

        val barberKing = MockData.businesses[0]
        val zenSpa = MockData.businesses[1]
        val dentist = MockData.businesses[4]

        return listOf(
            Appointment(
                id = "apt_1",
                userId = "user_1",
                business = barberKing,
                professional = barberKing.professionals[0],
                service = barberKing.services[1],
                date = inTwoDays,
                time = "10:00",
                status = AppointmentStatus.CONFIRMED
            ),
            Appointment(
                id = "apt_2",
                userId = "user_1",
                business = zenSpa,
                professional = zenSpa.professionals[0],
                service = zenSpa.services[0],
                date = inAWeek,
                time = "15:30",
                status = AppointmentStatus.PENDING
            ),
            Appointment(
                id = "apt_3",
                userId = "user_1",
                business = dentist,
                professional = dentist.professionals[0],
                service = dentist.services[0],
                date = threeDaysAgo,
                time = "09:00",
                status = AppointmentStatus.COMPLETED
            ),
            Appointment(
                id = "apt_4",
                userId = "user_1",
                business = barberKing,
                professional = barberKing.professionals[1],
                service = barberKing.services[0],
                date = tenDaysAgo,
                time = "11:30",
                status = AppointmentStatus.COMPLETED
            )
        )
    }

    /**
     * Obtiene todas las citas del usuario.
     * 
     * @return Result con la lista completa de citas (pasadas y futuras)
     */
    override suspend fun getAppointments(): Result<List<Appointment>> {
        delay(800)
        return Result.success(_appointments.value)
    }

    /**
     * Obtiene las citas próximas (futuras) del usuario.
     * 
     * Filtra las citas que aún no han ocurrido y no están canceladas,
     * ordenándolas por fecha ascendente (más próximas primero).
     * 
     * @return Result con la lista de citas futuras ordenadas por fecha
     */
    override suspend fun getUpcomingAppointments(): Result<List<Appointment>> {
        delay(600)
        val now = System.currentTimeMillis()
        return Result.success(
            _appointments.value
                .filter { it.date >= now && it.status != AppointmentStatus.CANCELLED }
                .sortedBy { it.date }
        )
    }

    /**
     * Obtiene el historial de citas (pasadas) del usuario.
     * 
     * Filtra las citas que ya ocurrieron o están completadas,
     * ordenándolas por fecha descendente (más recientes primero).
     * 
     * @return Result con la lista de citas pasadas ordenadas por fecha descendente
     */
    override suspend fun getPastAppointments(): Result<List<Appointment>> {
        delay(600)
        val now = System.currentTimeMillis()
        return Result.success(
            _appointments.value
                .filter { it.date < now || it.status == AppointmentStatus.COMPLETED }
                .sortedByDescending { it.date }
        )
    }

    /**
     * Crea una nueva cita en el sistema.
     * 
     * Genera un nuevo objeto Appointment con estado PENDING y lo agrega
     * a la lista de citas. En producción, esto enviaría la cita al backend.
     * 
     * @param business Negocio donde se realizará el servicio
     * @param professional Profesional que realizará el servicio
     * @param service Servicio a realizar
     * @param date Fecha de la cita en formato timestamp
     * @param time Hora de la cita en formato "HH:mm"
     * @param notes Notas adicionales para la cita (opcional)
     * @return Result con la cita creada
     */
    override suspend fun createAppointment(
        business: Business,
        professional: Professional,
        service: Service,
        date: Long,
        time: String,
        notes: String?
    ): Result<Appointment> {
        delay(1000)
        
        val newAppointment = Appointment(
            id = "apt_${System.currentTimeMillis()}",
            userId = "user_1", // En producción vendría de la sesión
            business = business,
            professional = professional,
            service = service,
            date = date,
            time = time,
            status = AppointmentStatus.PENDING,
            notes = notes
        )
        
        _appointments.value = _appointments.value + newAppointment
        return Result.success(newAppointment)
    }

    /**
     * Cancela una cita existente.
     * 
     * Cambia el estado de la cita a CANCELLED. En producción, esto
     * notificaría al negocio y actualizaría el backend.
     * 
     * @param appointmentId ID de la cita a cancelar
     * @return Result con true si la cancelación fue exitosa
     */
    override suspend fun cancelAppointment(appointmentId: String): Result<Boolean> {
        delay(800)
        
        _appointments.value = _appointments.value.map { appointment ->
            if (appointment.id == appointmentId) {
                appointment.copy(status = AppointmentStatus.CANCELLED)
            } else {
                appointment
            }
        }
        
        return Result.success(true)
    }

    /**
     * Obtiene una cita específica por su ID.
     * 
     * @param id Identificador único de la cita
     * @return Result con la cita si existe, o un error si no se encuentra
     */
    override suspend fun getAppointmentById(id: String): Result<Appointment> {
        delay(500)
        val appointment = _appointments.value.find { it.id == id }
        return if (appointment != null) {
            Result.success(appointment)
        } else {
            Result.failure(Exception("Cita no encontrada"))
        }
    }
}
