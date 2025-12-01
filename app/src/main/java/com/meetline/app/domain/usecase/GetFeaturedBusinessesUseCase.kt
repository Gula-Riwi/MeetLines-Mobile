package com.meetline.app.domain.usecase

import com.meetline.app.domain.model.Business
import com.meetline.app.domain.repository.BusinessRepository
import javax.inject.Inject

class GetFeaturedBusinessesUseCase @Inject constructor(
    private val businessRepository: BusinessRepository
) {
    suspend operator fun invoke(): Result<List<Business>> {
        return businessRepository.getFeaturedBusinesses()
    }
}
