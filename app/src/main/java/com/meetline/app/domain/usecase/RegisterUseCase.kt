package com.meetline.app.domain.usecase

import com.meetline.app.domain.model.User
import com.meetline.app.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Caso de uso para el registro de nuevos usuarios.
 *
 * Encapsula la lógica de negocio para registrar un nuevo usuario en el sistema.
 * Realiza validaciones antes de delegar al repositorio:
 * - Nombre no puede estar vacío.
 * - Email no puede estar vacío.
 * - Contraseña debe tener al menos 6 caracteres.
 *
 * ## Ejemplo de uso
 * ```kotlin
 * val result = registerUseCase(name, email, phone, password)
 * result.onSuccess { user ->
 *     // Registro exitoso
 * }.onFailure { error ->
 *     // Manejar error de validación o registro
 * }
 * ```
 *
 * @property authRepository Repositorio de autenticación inyectado por Hilt.
 *
 * @see AuthRepository Repositorio que maneja la lógica de registro.
 * @see User Modelo de usuario retornado en caso de éxito.
 */
class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Ejecuta el registro de un nuevo usuario.
     *
     * Valida los parámetros de entrada antes de delegar al repositorio.
     * Si alguna validación falla, retorna un [Result.failure] con el mensaje
     * de error correspondiente.
     *
     * @param name Nombre completo del usuario.
     * @param email Dirección de correo electrónico.
     * @param phone Número de teléfono de contacto.
     * @param password Contraseña deseada (mínimo 6 caracteres).
     * @return [Result.success] con el [User] registrado, o [Result.failure] con el error.
     */
    suspend operator fun invoke(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<User> {
        // Validación del nombre
        if (name.isBlank()) {
            return Result.failure(Exception("El nombre es requerido"))
        }
        
        // Validación del email
        if (email.isBlank()) {
            return Result.failure(Exception("El email es requerido"))
        }
        
        // Validación de la contraseña
        if (password.length < 6) {
            return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
        }
        
        return authRepository.register(name, email, phone, password)
    }
}
