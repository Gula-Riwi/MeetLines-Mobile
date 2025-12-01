package com.meetline.app.domain.usecase

import com.meetline.app.domain.model.TimeSlot
import com.meetline.app.domain.repository.BusinessRepository
import javax.inject.Inject

class GetAvailableTimeSlotsUseCase @Inject constructor(
    private val businessRepository: BusinessRepository
) {
    suspend operator fun invoke(businessId: String, professionalId: String, date: Long): Result<List<TimeSlot>> {
        return businessRepository.getAvailableTimeSlots(businessId, professionalId, date)
    }
}
