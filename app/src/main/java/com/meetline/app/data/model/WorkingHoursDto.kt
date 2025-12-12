package com.meetline.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO para la respuesta del endpoint de horarios de trabajo de un proyecto.
 */
data class WorkingHoursDto(
    @SerializedName("date")
    val date: String,
    
    @SerializedName("openingTime")
    val openingTime: String, // "09:00:00"
    
    @SerializedName("closingTime")
    val closingTime: String, // "18:00:00"
    
    @SerializedName("isOpen")
    val isOpen: Boolean
)

/**
 * Formatea los horarios de apertura y cierre en un string legible.
 * Ejemplo: "09:00 - 18:00"
 */
fun WorkingHoursDto.formatWorkingHours(): String {
    return if (isOpen) {
        val opening = openingTime.substring(0, 5) // "09:00:00" -> "09:00"
        val closing = closingTime.substring(0, 5)
        "$opening - $closing"
    } else {
        "Cerrado"
    }
}
