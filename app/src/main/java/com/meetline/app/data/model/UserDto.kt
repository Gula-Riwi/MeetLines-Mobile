package com.meetline.app.data.model

import com.google.gson.annotations.SerializedName
import com.meetline.app.domain.model.User

/**
 * Data Transfer Object (DTO) para representar un usuario en las respuestas de la API.
 *
 * Esta clase se utiliza para mapear las respuestas JSON del servidor a objetos Kotlin.
 * Separa la representación de datos de la API del modelo de dominio [User].
 *
 * @property id Identificador único del usuario en el backend.
 * @property name Nombre completo del usuario.
 * @property email Dirección de correo electrónico del usuario.
 * @property phone Número de teléfono del usuario.
 * @property avatarUrl URL de la imagen de perfil del usuario (puede ser null).
 * @property createdAt Timestamp de creación de la cuenta en formato Unix.
 *
 * @see User Modelo de dominio correspondiente.
 * @see toDomain Función de extensión para convertir a modelo de dominio.
 */
data class UserDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("phone")
    val phone: String,
    
    @SerializedName("avatar_url")
    val avatarUrl: String?,
    
    @SerializedName("created_at")
    val createdAt: Long?
)

/**
 * Convierte un [UserDto] a un modelo de dominio [User].
 *
 * Esta función de extensión transforma los datos recibidos de la API
 * al modelo utilizado internamente en la aplicación.
 *
 * @return Una instancia de [User] con los datos del DTO.
 */
fun UserDto.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    phone = phone,
    avatarUrl = avatarUrl,
    createdAt = createdAt ?: System.currentTimeMillis()
)

/**
 * Convierte un modelo de dominio [User] a un [UserDto].
 *
 * Esta función de extensión es útil para enviar datos del usuario
 * al servidor en operaciones como actualización de perfil.
 *
 * @return Una instancia de [UserDto] con los datos del modelo de dominio.
 */
fun User.toDto(): UserDto = UserDto(
    id = id,
    name = name,
    email = email,
    phone = phone,
    avatarUrl = avatarUrl,
    createdAt = createdAt
)
