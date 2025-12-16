package com.meetline.app.data.repository

import com.meetline.app.domain.model.*
import com.meetline.app.data.model.toDomain
import com.meetline.app.data.model.toDomain
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
class DefaultAppointmentRepository @Inject constructor(
    private val apiService: com.meetline.app.data.remote.MeetLineApiService,
    private val authRepository: com.meetline.app.domain.repository.AuthRepository,
    @javax.inject.Named("AppointmentsUrl") private val appointmentsUrl: String
) : AppointmentRepository {

    /**
     * Lista mutable de citas almacenadas localmente.
     * En producción, esto vendría del backend.
     */
    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    
    /**
     * StateFlow público para observar cambios en las citas.
     * La UI puede suscribirse a este flow para recibir actualizaciones automáticas.
     */
    override val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()


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
     * ordenándolas por fecha descendente (más próximas primero).
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
     * Crea una nueva cita en el sistema usando el backend local.
     */
    override suspend fun createAppointment(
        business: Business,
        professional: Professional,
        service: Service,
        date: Long,
        time: String,
        notes: String?
    ): Result<Appointment> {
        return try {
            // 1. Construir fechas de inicio y fin en formato ISO
            // Convertir timestamp a LocalDate
            val dateInstant = java.time.Instant.ofEpochMilli(date)
            val dateLocal = dateInstant.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
            
            // Parsear hora (HH:mm)
            val timeParts = time.split(":")
            val hour = timeParts[0].toInt()
            val minute = timeParts[1].toInt()
            
            // Crear LocalDateTime de inicio
            val startDateTime = dateLocal.atTime(hour, minute)
            
            // Calcular fin basado en duración
            val endDateTime = startDateTime.plusMinutes(service.duration.toLong())
            
            // Formatear a ISO-8601 con offset (ej: 2025-12-05T10:00:00-05:00)
            val formatter = java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
            val zoneId = java.time.ZoneId.systemDefault()
            
            val startTimeStr = startDateTime.atZone(zoneId).format(formatter)
            val endTimeStr = endDateTime.atZone(zoneId).format(formatter)
            
            // 2. Obtener datos del usuario desde la API
            val userResult = authRepository.getUserProfile()
            if (userResult.isFailure) {
                return Result.failure(Exception("No se pudo obtener el perfil del usuario: ${userResult.exceptionOrNull()?.message}"))
            }
            val user = userResult.getOrNull()!!
            
            // 3. Crear request DTO con los datos reales del usuario
            val request = com.meetline.app.data.model.CreateAppointmentRequest(
                projectId = business.id,
                serviceId = service.id.toIntOrNull() ?: 0, // Convertir String a Int
                employeeId = professional.id,
                startTime = startTimeStr,
                endTime = endTimeStr,
                userNotes = notes,
                clientName = user.name,
                clientEmail = user.email,
                clientPhone = user.phone
            )
            
            // 4. Llamar a la API
            val response = apiService.createAppointment(business.id, request)
            
            if (response.isSuccessful && response.body() != null) {
                val createdDto = response.body()!!

                // Mapear respuesta real del backend (.NET) a dominio
                val newAppointment = createdDto.toDomain().copy(
                    // Preferir los datos ya conocidos en UI si vienen vacíos del backend
                    business = business,
                    professional = professional,
                    service = service
                )

                // Actualizar caché local
                _appointments.value = _appointments.value + newAppointment
                Result.success(newAppointment)
            } else {
                 Result.failure(Exception("Error al crear cita: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red al crear cita: ${e.message}", e))
        }
    }

    /**
     * Cancela una cita existente llamando al endpoint del backend.
     * 
     * Este método requiere autenticación JWT y cancela la cita en el servidor.
     * 
     * @param appointmentId ID de la cita a cancelar
     * @return Result con true si la cancelación fue exitosa
     */
    override suspend fun cancelAppointment(appointmentId: String): Result<Boolean> {
        return try {
            // Construir URL completa del endpoint
            val url = "${appointmentsUrl}api/client/appointments/${appointmentId}"
            val response = apiService.cancelClientAppointment(url)
            
            if (response.isSuccessful) {
                // Actualizar caché local si la cancelación fue exitosa
                _appointments.value = _appointments.value.map { appointment ->
                    if (appointment.id == appointmentId) {
                        appointment.copy(status = AppointmentStatus.CANCELLED)
                    } else {
                        appointment
                    }
                }
                Result.success(true)
            } else if (response.code() == 401) {
                Result.failure(Exception("Error de autorización en servicio de citas (401)"))
            } else {
                Result.failure(Exception("Error al cancelar cita: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red al cancelar cita: ${e.message}", e))
        }
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

    /**
     * Obtiene las citas activas (pendientes) del usuario autenticado desde la API.
     *
     * Este método requiere autenticación JWT. El token se envía automáticamente
     * mediante el AuthInterceptor. Si el token es inválido o expiró, la API
     * responderá con 401 Unauthorized.
     *
     * @return Result con lista de citas pendientes o error si falla la petición.
     */
    override suspend fun getMyActiveAppointments(): Result<List<Appointment>> {
        return try {
            // Construir URL completa - endpoint unificado con pendingOnly=true
            val url = "${appointmentsUrl}api/client/appointments"
            val response = apiService.getClientAppointments(url, pendingOnly = true)
            
            if (response.isSuccessful && response.body() != null) {
                val appointmentDtos = response.body()!!
                val appointments = appointmentDtos.map { it.toDomain() }
                
                // Actualizar caché local con las citas activas
                _appointments.value = appointments
                
                Result.success(appointments)
            } else if (response.code() == 401) {
                // Token inválido para este servicio - NO cerrar sesión global
                Result.failure(Exception("Error de autorización en servicio de citas (401)"))
            } else {
                Result.failure(Exception("Error al obtener citas activas: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}", e))
        }
    }

    /**
     * Obtiene el historial completo de citas del usuario autenticado desde la API.
     *
     * Este método requiere autenticación JWT y devuelve todas las citas del usuario,
     * independientemente de su estado (pendientes, completadas, canceladas).
     *
     * @return Result con lista completa de citas o error si falla la petición.
     */
    override suspend fun getMyAppointmentHistory(): Result<List<Appointment>> {
        return try {
            // Construir URL completa - endpoint unificado con pendingOnly=false
            val url = "${appointmentsUrl}api/client/appointments"
            val response = apiService.getClientAppointments(url, pendingOnly = false)
            
            if (response.isSuccessful && response.body() != null) {
                val appointmentDtos = response.body()!!
                val appointments = appointmentDtos.map { it.toDomain() }
                
                // Actualizar caché local con todas las citas
                _appointments.value = appointments
                
                Result.success(appointments)
            } else if (response.code() == 401) {
                // Token inválido para este servicio - NO cerrar sesión global
                Result.failure(Exception("Error de autorización en servicio de citas (401)"))
            } else {
                Result.failure(Exception("Error al obtener historial de citas: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}", e))
        }
    }
}
