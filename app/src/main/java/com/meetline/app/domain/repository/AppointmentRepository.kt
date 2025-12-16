package com.meetline.app.domain.repository

import com.meetline.app.domain.model.Appointment
import com.meetline.app.domain.model.Business
import com.meetline.app.domain.model.Professional
import com.meetline.app.domain.model.Service
import kotlinx.coroutines.flow.StateFlow

interface AppointmentRepository {
    val appointments: StateFlow<List<Appointment>>
    suspend fun getAppointments(): Result<List<Appointment>>
    suspend fun getUpcomingAppointments(): Result<List<Appointment>>
    suspend fun getPastAppointments(): Result<List<Appointment>>
    suspend fun createAppointment(business: Business, professional: Professional, service: Service, date: Long, time: String, notes: String?): Result<Appointment>
    suspend fun cancelAppointment(appointmentId: String): Result<Boolean>
    suspend fun getAppointmentById(id: String): Result<Appointment>
    
    // Métodos protegidos con JWT (requieren autenticación)
    suspend fun getMyActiveAppointments(): Result<List<Appointment>>
    suspend fun getMyAppointmentHistory(): Result<List<Appointment>>
}
