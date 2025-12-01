package com.meetline.app.data.model

import com.google.gson.annotations.SerializedName
import com.meetline.app.domain.model.Appointment
import com.meetline.app.domain.model.AppointmentStatus
import com.meetline.app.domain.model.Business
import com.meetline.app.domain.model.BusinessCategory
import com.meetline.app.domain.model.Professional
import com.meetline.app.domain.model.Service

/**
 * Data Transfer Object (DTO) para representar una cita en las respuestas de la API.
 *
 * Esta clase mapea la estructura JSON de las citas del servidor.
 * Una cita representa una reservación de un servicio con un profesional
 * en un negocio específico.
 *
 * @property id Identificador único de la cita.
 * @property userId ID del usuario que creó la cita.
 * @property business Datos del negocio (anidado).
 * @property professional Datos del profesional (anidado).
 * @property service Datos del servicio (anidado).
 * @property date Fecha de la cita en timestamp (milisegundos).
 * @property time Hora de la cita (formato HH:mm).
 * @property status Estado actual de la cita.
 * @property notes Notas adicionales del usuario (opcional).
 * @property createdAt Timestamp de creación de la cita.
 *
 * @see Appointment Modelo de dominio correspondiente.
 * @see toDomain Función de extensión para convertir a modelo de dominio.
 */
data class AppointmentDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("user_id")
    val userId: String,
    
    @SerializedName("business")
    val business: AppointmentBusinessDto,
    
    @SerializedName("professional")
    val professional: AppointmentProfessionalDto,
    
    @SerializedName("service")
    val service: AppointmentServiceDto,
    
    @SerializedName("date")
    val date: Long,
    
    @SerializedName("time")
    val time: String,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("notes")
    val notes: String?,
    
    @SerializedName("created_at")
    val createdAt: Long?
)

/**
 * DTO simplificado del negocio para las citas.
 *
 * Contiene solo los datos esenciales del negocio necesarios
 * para mostrar en la lista de citas.
 */
data class AppointmentBusinessDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("image_url")
    val imageUrl: String,
    
    @SerializedName("address")
    val address: String,
    
    @SerializedName("category")
    val category: String
)

/**
 * DTO simplificado del profesional para las citas.
 */
data class AppointmentProfessionalDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("role")
    val role: String,
    
    @SerializedName("image_url")
    val imageUrl: String
)

/**
 * DTO simplificado del servicio para las citas.
 */
data class AppointmentServiceDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("price")
    val price: Double,
    
    @SerializedName("duration")
    val duration: Int
)

/**
 * Convierte un [AppointmentDto] a un modelo de dominio [Appointment].
 *
 * Transforma los datos de la API al modelo interno, incluyendo
 * la conversión del estado de la cita a un enum [AppointmentStatus].
 *
 * @return Una instancia de [Appointment] con los datos del DTO.
 */
fun AppointmentDto.toDomain(): Appointment = Appointment(
    id = id,
    userId = userId,
    business = Business(
        id = business.id,
        name = business.name,
        description = "",
        category = BusinessCategory.entries.find { it.name == business.category.uppercase() } 
            ?: BusinessCategory.OTHER,
        imageUrl = business.imageUrl,
        rating = 0f,
        reviewCount = 0,
        address = business.address,
        distance = "",
        isOpen = true,
        openingHours = "",
        professionals = emptyList(),
        services = emptyList()
    ),
    professional = Professional(
        id = professional.id,
        name = professional.name,
        role = professional.role,
        imageUrl = professional.imageUrl,
        rating = 0f,
        reviewCount = 0
    ),
    service = Service(
        id = service.id,
        name = service.name,
        description = "",
        price = service.price,
        duration = service.duration
    ),
    date = date,
    time = time,
    status = AppointmentStatus.entries.find { it.name == status.uppercase() } 
        ?: AppointmentStatus.PENDING,
    notes = notes,
    createdAt = createdAt ?: System.currentTimeMillis()
)
