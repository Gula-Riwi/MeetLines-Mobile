package com.meetline.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * Clase genérica para envolver las respuestas de la API.
 *
 * Esta clase proporciona una estructura estándar para todas las respuestas
 * del servidor, facilitando el manejo consistente de datos, errores y metadatos.
 *
 * @param T Tipo de datos contenidos en la respuesta.
 * @property success Indica si la operación fue exitosa.
 * @property data Datos de la respuesta (null si hay error).
 * @property message Mensaje descriptivo de la operación.
 * @property error Información del error si [success] es false.
 * @property meta Metadatos adicionales como paginación.
 *
 * @see ApiError Estructura de errores de la API.
 * @see ApiMeta Metadatos de respuesta.
 */
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("data")
    val data: T?,
    
    @SerializedName("message")
    val message: String?,
    
    @SerializedName("error")
    val error: ApiError?,
    
    @SerializedName("meta")
    val meta: ApiMeta?
)

/**
 * Estructura para representar errores de la API.
 *
 * Contiene información detallada sobre errores que pueden ocurrir
 * durante las operaciones con el servidor.
 *
 * @property code Código de error interno de la API.
 * @property message Mensaje de error legible para el usuario.
 * @property details Detalles adicionales del error (opcional).
 */
data class ApiError(
    @SerializedName("code")
    val code: String,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("details")
    val details: Map<String, String>?
)

/**
 * Metadatos adicionales de las respuestas de la API.
 *
 * Generalmente utilizado para información de paginación
 * en endpoints que devuelven listas de elementos.
 *
 * @property page Número de página actual.
 * @property perPage Elementos por página.
 * @property totalPages Total de páginas disponibles.
 * @property totalItems Total de elementos en todas las páginas.
 */
data class ApiMeta(
    @SerializedName("page")
    val page: Int?,
    
    @SerializedName("per_page")
    val perPage: Int?,
    
    @SerializedName("total_pages")
    val totalPages: Int?,
    
    @SerializedName("total_items")
    val totalItems: Int?
)

/**
 * Clase para solicitudes de autenticación (login/registro).
 *
 * Encapsula las credenciales del usuario para enviar al servidor.
 *
 * @property email Correo electrónico del usuario.
 * @property password Contraseña del usuario.
 * @property name Nombre del usuario (solo para registro).
 * @property phone Teléfono del usuario (solo para registro).
 */
data class AuthRequest(
    @SerializedName("email")
    val email: String,
    
    @SerializedName("password")
    val password: String,
    
    @SerializedName("name")
    val name: String? = null,
    
    @SerializedName("phone")
    val phone: String? = null
)

/**
 * Respuesta de autenticación del servidor.
 *
 * Contiene los datos del usuario autenticado y el token de acceso.
 *
 * @property user Datos del usuario autenticado.
 * @property token Token JWT para autorización de requests.
 * @property refreshToken Token para renovar la sesión (opcional).
 * @property expiresAt Timestamp de expiración del token.
 */
data class AuthResponse(
    @SerializedName("user")
    val user: UserDto,
    
    @SerializedName("token")
    val token: String,
    
    @SerializedName("refresh_token")
    val refreshToken: String?,
    
    @SerializedName("expires_at")
    val expiresAt: Long?
)

/**
 * Request para crear una nueva cita.
 *
 * Contiene todos los datos necesarios para agendar una cita.
 *
 * @property businessId ID del negocio seleccionado.
 * @property professionalId ID del profesional seleccionado.
 * @property serviceId ID del servicio a reservar.
 * @property date Fecha de la cita (formato YYYY-MM-DD).
 * @property time Hora de la cita (formato HH:mm).
 * @property notes Notas adicionales del usuario (opcional).
 */
data class CreateAppointmentRequest(
    @SerializedName("business_id")
    val businessId: String,
    
    @SerializedName("professional_id")
    val professionalId: String,
    
    @SerializedName("service_id")
    val serviceId: String,
    
    @SerializedName("date")
    val date: String,
    
    @SerializedName("time")
    val time: String,
    
    @SerializedName("notes")
    val notes: String?
)
