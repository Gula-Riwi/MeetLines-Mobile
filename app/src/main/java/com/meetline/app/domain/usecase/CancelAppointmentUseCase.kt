package com.meetline.app.domain.usecase

import com.meetline.app.domain.repository.AppointmentRepository
import javax.inject.Inject

class CancelAppointmentUseCase @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) {
    suspend operator fun invoke(appointmentId: String): Result<Boolean> {
        return appointmentRepository.cancelAppointment(appointmentId)
    }
}
