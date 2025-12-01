package com.meetline.app.domain.usecase

import com.meetline.app.domain.model.User
import com.meetline.app.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (email.isBlank()) {
            return Result.failure(Exception("El email es requerido"))
        }
        if (password.isBlank()) {
            return Result.failure(Exception("La contraseña es requerida"))
        }
        return authRepository.login(email, password)
    }
}
