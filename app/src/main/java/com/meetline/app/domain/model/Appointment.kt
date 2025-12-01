package com.meetline.app.domain.model

/**
 * Modelo de datos que representa una cita/agendamiento en el sistema.
 * 
 * Esta clase encapsula toda la información necesaria para gestionar
 * las reservas de servicios que los usuarios hacen con los negocios.
 * 
 * @property id Identificador único de la cita
 * @property userId Identificador del usuario que realizó la reserva
 * @property business Información del negocio donde se realizará el servicio
 * @property professional Profesional asignado para realizar el servicio
 * @property service Servicio específico que se va a realizar
 * @property date Fecha de la cita en formato timestamp (milisegundos desde epoch)
 * @property time Hora de la cita en formato "HH:mm" (ej: "10:00", "14:30")
 * @property status Estado actual de la cita (pendiente, confirmada, completada, cancelada)
 * @property notes Notas adicionales o instrucciones especiales para la cita (opcional)
 * @property createdAt Timestamp de cuando se creó la cita, por defecto la hora actual
 */
data class Appointment(
    val id: String,
    val userId: String,
    val business: Business,
    val professional: Professional,
    val service: Service,
    val date: Long, // timestamp
    val time: String, // "10:00"
    val status: AppointmentStatus,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Estados posibles de una cita en el sistema.
 * 
 * Cada estado tiene un nombre para mostrar al usuario y un color asociado
 * para facilitar la identificación visual en la interfaz.
 * 
 * @property displayName Nombre del estado en español para mostrar al usuario
 * @property color Color asociado al estado en formato hexadecimal ARGB
 */
enum class AppointmentStatus(val displayName: String, val color: Long) {
    /** Cita creada pero aún no confirmada por el negocio */
    PENDING("Pendiente", 0xFFFFA726),      // Naranja
    
    /** Cita confirmada por el negocio, lista para realizarse */
    CONFIRMED("Confirmada", 0xFF66BB6A),    // Verde
    
    /** Cita que ya se realizó exitosamente */
    COMPLETED("Completada", 0xFF42A5F5),    // Azul
    
    /** Cita cancelada por el usuario o el negocio */
    CANCELLED("Cancelada", 0xFFEF5350)      // Rojo
}

/**
 * Modelo para representar un horario disponible en el sistema de reservas.
 * 
 * Utilizado para mostrar al usuario los horarios en los que puede agendar
 * un servicio con un profesional específico.
 * 
 * @property time Hora en formato "HH:mm" (ej: "09:00", "15:30")
 * @property isAvailable Indica si este horario está disponible para reservar
 */
data class TimeSlot(
    val time: String,
    val isAvailable: Boolean
)

