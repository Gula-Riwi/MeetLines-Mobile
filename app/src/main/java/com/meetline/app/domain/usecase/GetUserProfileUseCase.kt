package com.meetline.app.domain.usecase

import com.meetline.app.domain.model.User
import com.meetline.app.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Caso de uso para obtener el perfil del usuario desde el servidor.
 * 
 * Consulta el endpoint `/api/client/auth/me` para obtener
 * los datos actualizados del usuario autenticado.
 */
class GetUserProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Obtiene el perfil del usuario desde el servidor.
     * 
     * @return Result con los datos actualizados del usuario
     */
    suspend operator fun invoke(): Result<User> {
        return authRepository.getUserProfile()
    }
}
