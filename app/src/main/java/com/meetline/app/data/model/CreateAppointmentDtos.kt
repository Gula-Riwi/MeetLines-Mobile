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
    val serviceId: String, // User example showed int, but typically IDs are strings or ints. Keeping as String or Int? User example: 1. Let's use Any or specific if known. Service ID "service-test-1" is string. User example 1 is int. Let's try String first as our domain uses String.
    
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
    val serviceId: Any,
    
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
