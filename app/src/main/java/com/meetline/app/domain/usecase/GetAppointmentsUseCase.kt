package com.meetline.app.domain.usecase

import com.meetline.app.domain.model.Appointment
import com.meetline.app.domain.repository.AppointmentRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetAppointmentsUseCase @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) {
    val appointments: StateFlow<List<Appointment>> = appointmentRepository.appointments

    suspend fun getAll(): Result<List<Appointment>> {
        return appointmentRepository.getAppointments()
    }

    suspend fun getUpcoming(): Result<List<Appointment>> {
        return appointmentRepository.getUpcomingAppointments()
    }

    suspend fun getPast(): Result<List<Appointment>> {
        return appointmentRepository.getPastAppointments()
    }
}
