package com.meetline.app.domain.model

/**
 * Modelo de datos que representa un negocio/establecimiento en la plataforma.
 * 
 * Contiene toda la información necesaria para mostrar y gestionar los negocios
 * que ofrecen servicios a través de la aplicación.
 * 
 * @property id Identificador único del negocio
 * @property name Nombre comercial del negocio
 * @property description Descripción detallada de los servicios y características del negocio
 * @property category Categoría a la que pertenece el negocio
 * @property imageUrl URL de la imagen principal del negocio
 * @property rating Calificación promedio del negocio (0.0 a 5.0)
 * @property reviewCount Número total de reseñas recibidas
 * @property address Dirección física del establecimiento
 * @property distance Distancia desde la ubicación del usuario (ej: "2.5 km")
 * @property distanceKm Distancia numérica en kilómetros (null si no se puede calcular)
 * @property latitude Latitud de la ubicación del negocio
 * @property longitude Longitud de la ubicación del negocio
 * @property isOpen Indica si el negocio está abierto en este momento
 * @property openingHours Horario de atención (ej: "Lun-Vie: 9:00-18:00")
 * @property professionals Lista de profesionales que trabajan en el negocio
 * @property services Lista de servicios ofrecidos por el negocio
 * @property contactChannels Lista de canales de contacto (WhatsApp, redes sociales, etc.)
 * @property isFavorite Indica si el usuario ha marcado este negocio como favorito
 */
data class Business(
    val id: String,
    val name: String,
    val description: String,
    val category: BusinessCategory,
    val imageUrl: String,
    val rating: Float,
    val reviewCount: Int,
    val address: String,
    val distance: String,
    val distanceKm: Double? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isOpen: Boolean,
    val openingHours: String,
    val professionals: List<Professional> = emptyList(),
    val services: List<Service> = emptyList(),
    val contactChannels: List<ContactChannel> = emptyList(),
    val isFavorite: Boolean = false
)

/**
 * Categorías de negocios disponibles en la plataforma.
 * 
 * Define los diferentes tipos de servicios que pueden ofrecer los negocios
 * registrados en la aplicación. Cada categoría tiene un nombre para mostrar
 * y una imagen representativa.
 * 
 * @property displayName Nombre de la categoría en español para mostrar al usuario
 * @property imageUrl URL de la imagen representativa de la categoría
 */
enum class BusinessCategory(
    val displayName: String,
    val imageUrl: String
) {
    /** Servicios de barbería y corte de cabello para hombres */
    BARBERSHOP(
        "Barbería",
        "https://images.unsplash.com/photo-1585747860715-2ba37e788b70?w=400"
    ),
    
    /** Servicios de spa, masajes y tratamientos de relajación */
    SPA(
        "Spa & Bienestar",
        "https://images.unsplash.com/photo-1544161515-4ab6ce6db874?w=400"
    ),
    
    /** Servicios de peluquería, manicura, pedicura y estética */
    BEAUTY_SALON(
        "Salón de Belleza",
        "https://images.unsplash.com/photo-1560066984-138dadb4c035?w=400"
    ),
    
    /** Servicios legales y asesoría jurídica */
    LAWYER(
        "Abogado",
        "https://images.unsplash.com/photo-1589829545856-d10d557cf95f?w=400"
    ),
    
    /** Servicios odontológicos y cuidado dental */
    DENTIST(
        "Dentista",
        "https://images.unsplash.com/photo-1629909613654-28e377c37b09?w=400"
    ),
    
    /** Servicios médicos generales y consultas */
    DOCTOR(
        "Médico",
        "https://images.unsplash.com/photo-1631217868264-e5b90bb7e133?w=400"
    ),
    
    /** Gimnasios y centros de entrenamiento físico */
    GYM(
        "Gimnasio",
        "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=400"
    ),
    
    /** Restaurantes y servicios de alimentación */
    RESTAURANT(
        "Restaurante",
        "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=400"
    ),
    
    /** Clínicas veterinarias y cuidado de mascotas */
    VETERINARY(
        "Veterinaria",
        "https://images.unsplash.com/photo-1628009368231-7bb7cfcb0def?w=400"
    ),
    
    /** Otros servicios no clasificados en las categorías anteriores */
    OTHER(
        "Otros",
        "https://images.unsplash.com/photo-1497366216548-37526070297c?w=400"
    )
}

/**
 * Modelo de datos que representa un profesional/empleado del negocio.
 * 
 * Los profesionales son las personas que brindan los servicios dentro
 * de un negocio y con quienes los usuarios pueden agendar citas.
 * 
 * @property id Identificador único del profesional
 * @property name Nombre completo del profesional
 * @property role Cargo o especialidad (ej: "Barbero Senior", "Estilista")
 * @property imageUrl URL de la foto del profesional
 * @property rating Calificación promedio del profesional (0.0 a 5.0)
 * @property reviewCount Número total de reseñas recibidas
 * @property isAvailable Indica si el profesional está disponible para agendar citas
 */
data class Professional(
    val id: String,
    val name: String,
    val role: String,
    val imageUrl: String,
    val rating: Float,
    val reviewCount: Int,
    val isAvailable: Boolean = true
)

/**
 * Modelo de datos que representa un servicio ofrecido por el negocio.
 * 
 * Los servicios son las actividades específicas que un negocio ofrece
 * y que los usuarios pueden reservar.
 * 
 * @property id Identificador único del servicio
 * @property name Nombre del servicio (ej: "Corte de cabello", "Manicura")
 * @property description Descripción detallada de lo que incluye el servicio
 * @property price Precio del servicio en la moneda local
 * @property duration Duración estimada del servicio en minutos
 * @property imageUrl URL de la imagen representativa del servicio (opcional)
 */
data class Service(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val duration: Int, // en minutos
    val imageUrl: String? = null
)

