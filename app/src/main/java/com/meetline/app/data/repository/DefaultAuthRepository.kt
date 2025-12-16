package com.meetline.app.data.repository

import com.meetline.app.data.local.SessionManager
import com.meetline.app.data.model.toDomain
import com.meetline.app.data.model.toDto
import com.meetline.app.domain.model.User
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

import com.meetline.app.domain.repository.AuthRepository

/**
 * Repositorio para operaciones de autenticación y gestión de usuarios.
 * 
 * Maneja todas las operaciones relacionadas con:
 * - Login y registro de usuarios
 * - Gestión de sesiones
 * - Actualización de perfiles
 * - Recuperación de contraseñas
 * 
 * Actualmente simula las respuestas del backend con datos mock,
 * pero está diseñado para integrarse fácilmente con una API real.
 * 
 * Utiliza SessionManager para persistir la información de sesión
 * localmente en el dispositivo.
 * 
 * @property sessionManager Gestor de sesión inyectado por Hilt
 */
@Singleton
class DefaultAuthRepository @Inject constructor(
    private val sessionManager: SessionManager,
    private val apiService: com.meetline.app.data.remote.MeetLineApiService
) : AuthRepository {

    /**
     * Intenta iniciar sesión con las credenciales proporcionadas.
     * 
     * Realiza la llamada a la API para autenticar al usuario y
     * guarda la sesión localmente.
     * 
     * @param email Correo electrónico del usuario
     * @param password Contraseña del usuario
     * @return Result con el objeto User si el login es exitoso, o un error si falla
     */
    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val request = com.meetline.app.data.model.AuthRequest(
                email = email,
                password = password
            )
            
            val response = apiService.login(request)
            
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                
                // Extraer el ID del usuario del token JWT (del claim 'sub')
                val userId = extractUserIdFromToken(authResponse.token)
                
                val user = User(
                    id = userId,
                    name = authResponse.fullName,
                    email = authResponse.email,
                    phone = "", // El login no devuelve phone, se puede obtener del perfil después
                    avatarUrl = null
                )
                
                // Guardar sesión con token y refreshToken
                sessionManager.saveSession(
                    user = user,
                    token = authResponse.token,
                    refreshToken = authResponse.refreshToken
                )
                Result.success(user)
            } else {
                Result.failure(Exception("Error de autenticación: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}", e))
        }
    }
    
    /**
     * Extrae el ID del usuario del token JWT.
     * El ID está en el claim 'sub' del payload.
     */
    private fun extractUserIdFromToken(token: String): String {
        return try {
            val parts = token.split(".")
            if (parts.size >= 2) {
                val payload = String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP))
                val json = com.google.gson.Gson().fromJson(payload, Map::class.java)
                json["sub"] as? String ?: "unknown_user"
            } else {
                "unknown_user"
            }
        } catch (e: Exception) {
            "unknown_user"
        }
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * 
     * Crea una nueva cuenta de usuario con la información proporcionada
     * llamando a la API.
     * 
     * @param name Nombre completo del usuario
     * @param email Correo electrónico del usuario
     * @param phone Número de teléfono del usuario
     * @param password Contraseña deseada
     * @return Result con el objeto User si el registro es exitoso, o un error si falla
     */
    override suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<User> {
        return try {
            val request = com.meetline.app.data.model.AuthRequest(
                email = email,
                password = password,
                fullName = name,
                phone = phone
            )
            
            val response = apiService.register(request)
            
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                
                // Extraer el ID del usuario del token JWT
                val userId = extractUserIdFromToken(authResponse.token)
                
                val user = User(
                    id = userId,
                    name = authResponse.fullName,
                    email = authResponse.email,
                    phone = phone,
                    avatarUrl = null
                )
                
                sessionManager.saveSession(user, authResponse.token)
                Result.success(user)
            } else {
                Result.failure(Exception("Error en el registro: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}", e))
        }
    }

    /**
     * Cierra la sesión del usuario actual.
     * 
     * Elimina todos los datos de sesión almacenados localmente.
     * Después de llamar a este método, el usuario deberá iniciar sesión
     * nuevamente para acceder a la aplicación.
     */
    override fun logout() {
        sessionManager.logout()
    }

    /**
     * Verifica si hay una sesión activa.
     * 
     * @return true si el usuario está autenticado, false en caso contrario
     */
    override fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()

    /**
     * Obtiene el usuario actualmente autenticado.
     * 
     * @return Objeto User con los datos del usuario actual, o null si no hay sesión activa
     */
    override fun getCurrentUser(): User? = sessionManager.getCurrentUser()

    /**
     * Obtiene el perfil del usuario desde la API.
     * 
     * Requiere que el usuario esté autenticado (token en el header).
     * 
     * @return Result con el usuario actualizado desde el servidor
     */
    override suspend fun getUserProfile(): Result<User> {
        return try {
            val response = apiService.getCurrentUser()
            
            if (response.isSuccessful && response.body() != null) {
                val userDto = response.body()!!
                val user = userDto.toDomain()
                
                // Actualizar la sesión local con los datos del servidor
                sessionManager.updateUser(user)
                
                Result.success(user)
            } else {
                Result.failure(Exception("Error al obtener perfil: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}", e))
        }
    }
    
    /**
     * Actualiza el perfil del usuario.
     * 
     * Envía los cambios al servidor y actualiza la sesión local.
     * 
     * @param user Objeto User con los datos actualizados
     * @return Result con el usuario actualizado
     */
    override suspend fun updateProfile(user: User): Result<User> {
        return try {
            val response = apiService.updateProfile(user.toDto())
            
            if (response.isSuccessful && response.body() != null) {
                val updatedUser = response.body()!!.toDomain()
                sessionManager.updateUser(updatedUser)
                Result.success(updatedUser)
            } else {
                Result.failure(Exception("Error al actualizar perfil: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}", e))
        }
    }

    /**
     * Solicita recuperación de contraseña.
     * 
     * Envía un correo electrónico al usuario con instrucciones para
     * restablecer su contraseña.
     * 
     * @param email Correo electrónico del usuario que olvidó su contraseña
     * @return Result con true si la solicitud fue exitosa, o un error si falla
     */
    override suspend fun requestPasswordReset(email: String): Result<Boolean> {
        return try {
            val response = apiService.requestPasswordReset(mapOf("email" to email))
            
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Error al solicitar recuperación: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}", e))
        }
    }
    
    /**
     * Restablece la contraseña del usuario usando el token recibido por email.
     * 
     * @param token Token de recuperación recibido en el email
     * @param newPassword Nueva contraseña a establecer
     * @return Result con true si el reset fue exitoso
     */
    override suspend fun resetPassword(token: String, newPassword: String): Result<Boolean> {
        return try {
            val request = com.meetline.app.data.model.ResetPasswordRequest(
                token = token,
                newPassword = newPassword
            )
            
            val response = apiService.resetPassword(request)
            
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Error al restablecer contraseña: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}", e))
        }
    }
}
