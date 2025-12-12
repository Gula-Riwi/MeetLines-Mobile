package com.meetline.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO para la solicitud de creación de una cita.
 */
data class CreateAppointmentRequest(
    @SerializedName("projectId")
    val projectId: String,
    
    @SerializedName("userId")
    val userId: String,
    
    @SerializedName("serviceId")
    val serviceId: Int, // El backend espera un entero según la documentación de la API
    
    @SerializedName("employeeId")
    val employeeId: String?,
    
    @SerializedName("startTime")
    val startTime: String,
    
    @SerializedName("endTime")
    val endTime: String,
    
    @SerializedName("price")
    val price: Double,
    
    @SerializedName("currency")
    val currency: String,
    
    @SerializedName("userNotes")
    val userNotes: String?
)

/**
 * DTO para la respuesta de creación de cita exitosa.
 * Mapea la estructura específica retornada por el endpoint POST.
 */
data class AppointmentCreatedResponseDto(
    @SerializedName("id")
    val id: Any, // Puede ser Int o String
    
    @SerializedName("projectId")
    val projectId: String,
    
    @SerializedName("userId")
    val userId: String,
    
    @SerializedName("serviceId")
    val serviceId: Int,
    
    @SerializedName("employeeId")
    val employeeId: String?,
    
    @SerializedName("startTime")
    val startTime: String,
    
    @SerializedName("endTime")
    val endTime: String,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("price")
    val price: Double,
    
    @SerializedName("currency")
    val currency: String,
    
    @SerializedName("userNotes")
    val userNotes: String?,
    
    @SerializedName("createdAt")
    val createdAt: String
)
