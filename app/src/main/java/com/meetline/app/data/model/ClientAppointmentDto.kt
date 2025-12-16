package com.meetline.app.data.model

import com.google.gson.annotations.SerializedName
import com.meetline.app.domain.model.Appointment
import com.meetline.app.domain.model.AppointmentStatus
import com.meetline.app.domain.model.Business
import com.meetline.app.domain.model.BusinessCategory
import com.meetline.app.domain.model.Professional
import com.meetline.app.domain.model.Service
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * DTO para el endpoint .NET:
 * GET /api/client/appointments?pendingOnly={true|false}
 */
data class ClientAppointmentDto(
    @SerializedName("id")
    val id: Long?,

    @SerializedName("projectId")
    val projectId: String?,

    @SerializedName("projectName")
    val projectName: String?,

    @SerializedName("serviceId")
    val serviceId: Long?,

    @SerializedName("serviceName")
    val serviceName: String?,

    @SerializedName("employeeId")
    val employeeId: String?,

    @SerializedName("employeeName")
    val employeeName: String?,

    @SerializedName("startTime")
    val startTime: String?,

    @SerializedName("endTime")
    val endTime: String?,

    @SerializedName("price")
    val price: Double?,

    @SerializedName("currency")
    val currency: String?,

    @SerializedName("status")
    val status: String?,

    @SerializedName("userNotes")
    val userNotes: String?,

    @SerializedName("clientName")
    val clientName: String?,

    @SerializedName("clientEmail")
    val clientEmail: String?,

    @SerializedName("clientPhone")
    val clientPhone: String?,

    @SerializedName("meetingLink")
    val meetingLink: String?,

    @SerializedName("createdAt")
    val createdAt: String?
)

fun ClientAppointmentDto.toDomain(): Appointment {
    val parsedStart = startTime?.let {
        runCatching { OffsetDateTime.parse(it) }.getOrNull()
    }

    val deviceZone = java.time.ZoneId.systemDefault()
    val startInDeviceZone = parsedStart?.atZoneSameInstant(deviceZone)

    val dateMillis = startInDeviceZone?.toInstant()?.toEpochMilli() ?: 0L
    val timeStr = startInDeviceZone?.toLocalTime()?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: ""

    return Appointment(
        id = (id ?: 0L).toString(),
        userId = "",
        business = Business(
            id = projectId ?: "",
            name = projectName ?: "",
            description = "",
            category = BusinessCategory.OTHER,
            imageUrl = "",
            rating = 0f,
            reviewCount = 0,
            address = "",
            distance = "",
            isOpen = true,
            openingHours = "",
            professionals = emptyList(),
            services = emptyList()
        ),
        professional = Professional(
            id = employeeId ?: "",
            name = employeeName ?: "",
            role = "",
            imageUrl = "",
            rating = 0f,
            reviewCount = 0
        ),
        service = Service(
            id = (serviceId ?: 0L).toString(),
            name = serviceName ?: "",
            description = "",
            price = price ?: 0.0,
            duration = 0
        ),
        date = dateMillis,
        time = timeStr,
        status = AppointmentStatus.entries.find { it.name == (status ?: "").uppercase() }
            ?: AppointmentStatus.PENDING,
        notes = userNotes,
        createdAt = createdAt?.let {
            runCatching { OffsetDateTime.parse(it).atZoneSameInstant(deviceZone).toInstant().toEpochMilli() }.getOrNull()
        }
            ?: System.currentTimeMillis()
    )
}
