package com.meetline.app.domain.model

/**
 * Modelo de datos que representa un usuario de la aplicación.
 * 
 * Contiene la información básica del perfil de usuario necesaria
 * para la autenticación y personalización de la experiencia.
 * 
 * @property id Identificador único del usuario en el sistema
 * @property name Nombre completo del usuario
 * @property email Dirección de correo electrónico (utilizada para login)
 * @property phone Número de teléfono de contacto
 * @property avatarUrl URL de la foto de perfil del usuario (opcional)
 * @property createdAt Timestamp de cuando se creó la cuenta, por defecto la hora actual
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val avatarUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

