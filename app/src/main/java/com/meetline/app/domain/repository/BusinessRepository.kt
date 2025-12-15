package com.meetline.app.domain.repository

import com.meetline.app.domain.model.Business
import com.meetline.app.domain.model.BusinessCategory
import com.meetline.app.domain.model.TimeSlot

interface BusinessRepository {
    suspend fun getAllBusinesses(): Result<List<Business>>
    suspend fun getBusinessById(id: String): Result<Business>
    suspend fun getBusinessesByCategory(category: BusinessCategory): Result<List<Business>>
    suspend fun searchBusinesses(query: String): Result<List<Business>>
    suspend fun getFeaturedBusinesses(): Result<List<Business>>
    suspend fun getNearbyBusinesses(latitude: Double? = null, longitude: Double? = null): Result<List<Business>>
    suspend fun getAvailableTimeSlots(businessId: String, professionalId: String, date: Long): Result<List<TimeSlot>>
    fun getAllCategories(): List<BusinessCategory>
}
