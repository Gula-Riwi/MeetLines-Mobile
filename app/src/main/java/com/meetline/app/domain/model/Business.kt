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
    
    /** Servicios de tatuajes y arte corporal */
    TATTOO(
        "Tatuajes",
        "https://images.unsplash.com/photo-1568515387631-8b650bbcdb90?w=400"
    ),
    
    /** Servicios de fisioterapia y rehabilitación */
    PHYSIOTHERAPY(
        "Fisioterapia",
        "https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=400"
    ),
    
    /** Servicios de fotografía profesional */
    PHOTOGRAPHY(
        "Fotografía",
        "https://images.unsplash.com/photo-1554048612-b6a482bc67e5?w=400"
    ),
    
    /** Servicios de consultoría y asesoramiento empresarial */
    CONSULTING(
        "Consultoría",
        "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=400"
    ),
    
    /** Servicios de psicología y terapia */
    PSYCHOLOGY(
        "Psicología",
        "https://images.unsplash.com/photo-1527689368864-3a821dbccc34?w=400"
    ),
    
    /** Academias y servicios de enseñanza */
    EDUCATION(
        "Academia",
        "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?w=400"
    ),
    
    /** Servicios de mantenimiento automotriz */
    CAR_SERVICE(
        "Taller Automotriz",
        "https://images.unsplash.com/photo-1486262715619-67b85e0b08d3?w=400"
    ),
    
    /** Servicios de lavandería y tintorería */
    LAUNDRY(
        "Lavandería",
        "https://images.unsplash.com/photo-1517677208171-0bc6725a3e60?w=400"
    ),
    
    /** Servicios de reparación de dispositivos electrónicos */
    ELECTRONICS_REPAIR(
        "Reparación Electrónica",
        "https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=400"
    ),
    
    /** Servicios de limpieza del hogar */
    CLEANING(
        "Limpieza del Hogar",
        "https://images.unsplash.com/photo-1581578731548-c64695cc6952?w=400"
    ),
    
    /** Servicios de cuidado y estética de mascotas */
    PET_GROOMING(
        "Estética de Mascotas",
        "https://images.unsplash.com/photo-1559190394-df5a28aab5c5?w=400"
    ),
    
    /** Estudios de yoga y meditación */
    YOGA(
        "Yoga & Meditación",
        "https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=400"
    ),
    
    /** Servicios de nutrición y dietética */
    NUTRITION(
        "Nutrición",
        "https://images.unsplash.com/photo-1490645935967-10de6ba17061?w=400"
    ),
    
    /** Servicios contables y fiscales */
    ACCOUNTING(
        "Contabilidad",
        "https://images.unsplash.com/photo-1554224155-6726b3ff858f?w=400"
    ),
    
    /** Servicios de diseño gráfico y creativo */
    DESIGN(
        "Diseño Gráfico",
        "https://images.unsplash.com/photo-1561070791-2526d30994b5?w=400"
    ),
    
    /** Servicios de arquitectura y diseño de interiores */
    ARCHITECTURE(
        "Arquitectura",
        "https://images.unsplash.com/photo-1503387762-592deb58ef4e?w=400"
    ),
    
    /** Servicios de oftalmología y cuidado visual */
    OPTOMETRY(
        "Oftalmología",
        "https://images.unsplash.com/photo-1574258495973-f010dfbb5371?w=400"
    ),
    
    /** Servicios de dermatología y cuidado de la piel */
    DERMATOLOGY(
        "Dermatología",
        "https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=400"
    ),
    
    /** Servicios de quiropráctica y ajustes de columna */
    CHIROPRACTIC(
        "Quiropráctica",
        "https://images.unsplash.com/photo-1540555700478-4be289fbecef?w=400"
    ),
    
    /** Servicios de podología y cuidado de pies */
    PODIATRY(
        "Podología",
        "https://images.unsplash.com/photo-1612349316228-5942a9b489c2?w=400"
    ),
    
    /** Servicios de laboratorio clínico y análisis */
    LABORATORY(
        "Laboratorio Clínico",
        "https://images.unsplash.com/photo-1579154204601-01588f351e67?w=400"
    ),
    
    /** Servicios de terapia ocupacional */
    OCCUPATIONAL_THERAPY(
        "Terapia Ocupacional",
        "https://images.unsplash.com/photo-1559757175-5700dde675bc?w=400"
    ),
    
    /** Servicios de terapia del lenguaje */
    SPEECH_THERAPY(
        "Terapia del Lenguaje",
        "https://images.unsplash.com/photo-1503454537195-1dcabb73ffb9?w=400"
    ),
    
    /** Servicios de entrenamiento personal */
    PERSONAL_TRAINING(
        "Entrenamiento Personal",
        "https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=400"
    ),
    
    /** Servicios de estilismo y asesoría de imagen */
    STYLING(
        "Estilismo",
        "https://images.unsplash.com/photo-1490481651871-ab68de25d43d?w=400"
    ),
    
    /** Servicios de barbería premium */
    BARBER_SHOP_PREMIUM(
        "Barbería Premium",
        "https://images.unsplash.com/photo-1503951914875-452162b0f3f1?w=400"
    ),
    
    /** Servicios de extensiones y tratamientos capilares */
    HAIR_EXTENSIONS(
        "Extensiones Capilares",
        "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?w=400"
    ),
    
    /** Servicios de maquillaje profesional */
    MAKEUP(
        "Maquillaje Profesional",
        "https://images.unsplash.com/photo-1487412947147-5cebf100ffc2?w=400"
    ),
    
    /** Servicios de depilación láser y estética */
    LASER_HAIR_REMOVAL(
        "Depilación Láser",
        "https://images.unsplash.com/photo-1519415387722-a1c3bbef716c?w=400"
    ),
    
    /** Servicios de masajes terapéuticos */
    MASSAGE_THERAPY(
        "Masajes Terapéuticos",
        "https://images.unsplash.com/photo-1544161515-4ab6ce6db874?w=400"
    ),
    
    /** Servicios de acupuntura y medicina alternativa */
    ACUPUNCTURE(
        "Acupuntura",
        "https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=400"
    ),
    
    /** Servicios de floristería y decoración */
    FLORIST(
        "Floristería",
        "https://images.unsplash.com/photo-1490750967868-88aa4486c946?w=400"
    ),
    
    /** Servicios de catering y eventos */
    CATERING(
        "Catering",
        "https://images.unsplash.com/photo-1555244162-803834f70033?w=400"
    ),
    
    /** Servicios de organización de eventos */
    EVENT_PLANNING(
        "Organización de Eventos",
        "https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?w=400"
    ),
    
    /** Servicios de carpintería y ebanistería */
    CARPENTRY(
        "Carpintería",
        "https://images.unsplash.com/photo-1504148455328-c376907d081c?w=400"
    ),
    
    /** Servicios de plomería */
    PLUMBING(
        "Plomería",
        "https://images.unsplash.com/photo-1607472586893-edb57bdc0e39?w=400"
    ),
    
    /** Servicios de electricidad */
    ELECTRICAL(
        "Electricidad",
        "https://images.unsplash.com/photo-1621905251189-08b45d6a269e?w=400"
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

