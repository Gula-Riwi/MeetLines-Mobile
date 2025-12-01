package com.meetline.app.domain.usecase

import com.meetline.app.domain.model.Business
import com.meetline.app.domain.model.BusinessCategory
import com.meetline.app.domain.repository.BusinessRepository
import javax.inject.Inject

class GetBusinessListUseCase @Inject constructor(
    private val businessRepository: BusinessRepository
) {
    suspend operator fun invoke(category: BusinessCategory? = null): Result<List<Business>> {
        return if (category != null) {
            businessRepository.getBusinessesByCategory(category)
        } else {
            businessRepository.getAllBusinesses()
        }
    }
}
