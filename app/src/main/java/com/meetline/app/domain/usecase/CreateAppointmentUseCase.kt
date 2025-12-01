package com.meetline.app.domain.usecase

import com.meetline.app.domain.model.Appointment
import com.meetline.app.domain.model.Business
import com.meetline.app.domain.model.Professional
import com.meetline.app.domain.model.Service
import com.meetline.app.domain.repository.AppointmentRepository
import javax.inject.Inject

class CreateAppointmentUseCase @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) {
    suspend operator fun invoke(
        business: Business,
        professional: Professional,
        service: Service,
        date: Long,
        time: String,
        notes: String? = null
    ): Result<Appointment> {
        return appointmentRepository.createAppointment(business, professional, service, date, time, notes)
    }
}
