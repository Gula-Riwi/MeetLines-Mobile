package com.meetline.app.domain.usecase

import com.meetline.app.domain.model.BusinessCategory
import com.meetline.app.domain.repository.BusinessRepository
import javax.inject.Inject

class GetAllCategoriesUseCase @Inject constructor(
    private val businessRepository: BusinessRepository
) {
    operator fun invoke(): List<BusinessCategory> {
        return businessRepository.getAllCategories()
    }
}
