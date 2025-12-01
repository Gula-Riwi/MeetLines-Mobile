package com.meetline.app.domain.usecase

import com.meetline.app.domain.model.User
import com.meetline.app.domain.repository.AuthRepository
import javax.inject.Inject

class GetSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): User? {
        return if (authRepository.isLoggedIn()) {
            authRepository.getCurrentUser()
        } else {
            null
        }
    }
}
