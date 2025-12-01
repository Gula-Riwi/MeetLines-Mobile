package com.meetline.app.domain.usecase

import com.meetline.app.domain.model.User
import com.meetline.app.domain.repository.AuthRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(user: User): Result<User> {
        return authRepository.updateProfile(user)
    }
}
