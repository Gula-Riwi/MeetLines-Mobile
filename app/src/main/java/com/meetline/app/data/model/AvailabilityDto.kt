package com.meetline.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO para la respuesta de slots disponibles.
 */
data class AvailabilityResponseDto(
    @SerializedName("date")
    val date: String,
    
    @SerializedName("availableSlots")
    val availableSlots: List<SlotDto>
)

/**
 * DTO para un slot de tiempo individual.
 */
data class SlotDto(
    @SerializedName("startTime")
    val startTime: String,
    
    @SerializedName("endTime")
    val endTime: String
)
