package com.meetline.app.data.model

import com.google.gson.annotations.SerializedName
import com.meetline.app.domain.model.Business
import com.meetline.app.domain.model.BusinessCategory
import com.meetline.app.domain.model.Professional
import com.meetline.app.domain.model.Service

/**
 * Data Transfer Object (DTO) para representar un negocio en las respuestas de la API.
 *
 * Esta clase mapea la estructura JSON del servidor al formato de Kotlin.
 * Incluye toda la información del negocio: datos básicos, profesionales y servicios.
 *
 * @property id Identificador único del negocio.
 * @property name Nombre comercial del negocio.
 * @property description Descripción detallada del negocio y sus servicios.
 * @property category Categoría del negocio (ej: BARBERSHOP, SPA, etc.).
 * @property imageUrl URL de la imagen principal del negocio.
 * @property rating Calificación promedio del negocio (0.0 - 5.0).
 * @property reviewCount Número total de reseñas recibidas.
 * @property address Dirección física del establecimiento.
 * @property distance Distancia desde la ubicación del usuario.
 * @property isOpen Indica si el negocio está abierto actualmente.
 * @property openingHours Horario de atención del negocio.
 * @property professionals Lista de profesionales que trabajan en el negocio.
 * @property services Lista de servicios ofrecidos.
 * @property isFavorite Indica si el usuario ha marcado el negocio como favorito.
 *
 * @see Business Modelo de dominio correspondiente.
 * @see toDomain Función de extensión para convertir a modelo de dominio.
 */
data class BusinessDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("category")
    val category: String,
    
    @SerializedName("image_url")
    val imageUrl: String,
    
    @SerializedName("rating")
    val rating: Float,
    
    @SerializedName("review_count")
    val reviewCount: Int,
    
    @SerializedName("address")
    val address: String,
    
    @SerializedName("distance")
    val distance: String,
    
    @SerializedName("is_open")
    val isOpen: Boolean,
    
    @SerializedName("opening_hours")
    val openingHours: String,
    
    @SerializedName("professionals")
    val professionals: List<ProfessionalDto>?,
    
    @SerializedName("services")
    val services: List<ServiceDto>?,
    
    @SerializedName("is_favorite")
    val isFavorite: Boolean?
)

/**
 * DTO para representar un profesional del negocio.
 *
 * @property id Identificador único del profesional.
 * @property name Nombre completo del profesional.
 * @property role Cargo o especialidad del profesional.
 * @property imageUrl URL de la foto del profesional.
 * @property rating Calificación promedio del profesional.
 * @property reviewCount Número de reseñas del profesional.
 * @property isAvailable Disponibilidad actual del profesional.
 */
data class ProfessionalDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("role")
    val role: String,
    
    @SerializedName("image_url")
    val imageUrl: String,
    
    @SerializedName("rating")
    val rating: Float,
    
    @SerializedName("review_count")
    val reviewCount: Int,
    
    @SerializedName("is_available")
    val isAvailable: Boolean?
)

/**
 * DTO para representar un servicio ofrecido por el negocio.
 *
 * @property id Identificador único del servicio.
 * @property name Nombre del servicio.
 * @property description Descripción detallada del servicio.
 * @property price Precio del servicio.
 * @property duration Duración estimada en minutos.
 * @property imageUrl URL de la imagen del servicio (opcional).
 */
data class ServiceDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("price")
    val price: Double,
    
    @SerializedName("duration")
    val duration: Int,
    
    @SerializedName("image_url")
    val imageUrl: String?
)

/**
 * Convierte un [BusinessDto] a un modelo de dominio [Business].
 *
 * Transforma los datos de la API al modelo interno de la aplicación,
 * incluyendo la conversión de categorías, profesionales y servicios.
 *
 * @return Una instancia de [Business] con los datos del DTO.
 */
fun BusinessDto.toDomain(): Business = Business(
    id = id,
    name = name,
    description = description,
    category = BusinessCategory.entries.find { it.name == category.uppercase() } 
        ?: BusinessCategory.OTHER,
    imageUrl = imageUrl,
    rating = rating,
    reviewCount = reviewCount,
    address = address,
    distance = distance,
    isOpen = isOpen,
    openingHours = openingHours,
    professionals = professionals?.map { it.toDomain() } ?: emptyList(),
    services = services?.map { it.toDomain() } ?: emptyList(),
    isFavorite = isFavorite ?: false
)

/**
 * Convierte un [ProfessionalDto] a un modelo de dominio [Professional].
 *
 * @return Una instancia de [Professional] con los datos del DTO.
 */
fun ProfessionalDto.toDomain(): Professional = Professional(
    id = id,
    name = name,
    role = role,
    imageUrl = imageUrl,
    rating = rating,
    reviewCount = reviewCount,
    isAvailable = isAvailable ?: true
)

/**
 * Convierte un [ServiceDto] a un modelo de dominio [Service].
 *
 * @return Una instancia de [Service] con los datos del DTO.
 */
fun ServiceDto.toDomain(): Service = Service(
    id = id,
    name = name,
    description = description,
    price = price,
    duration = duration,
    imageUrl = imageUrl
)
