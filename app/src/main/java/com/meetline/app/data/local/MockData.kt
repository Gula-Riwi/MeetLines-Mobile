package com.meetline.app.data.local

import com.meetline.app.domain.model.*

/**
 * Objeto singleton con datos simulados para desarrollo.
 *
 * Este objeto proporciona datos de ejemplo que simulan las respuestas que
 * vendrían de un backend real. Permite desarrollar y probar la aplicación
 * sin necesidad de tener un servidor funcionando.
 *
 * ## Contenido
 * - Profesionales de ejemplo para diferentes categorías de negocios.
 * - Servicios organizados por tipo de negocio.
 * - Negocios completos con toda su información.
 * - Horarios disponibles para reservas.
 * - Funciones de búsqueda y filtrado.
 *
 * En producción, estos datos serán reemplazados por llamadas reales a la API.
 */
object MockData {

    /**
     * Lista de profesionales de ejemplo para todos los negocios.
     * Incluye barberos, esteticistas, médicos, abogados y otros especialistas.
     */
    private val _professionals = listOf(
        Professional(
            id = "prof_1",
            name = "Carlos Mendoza",
            role = "Barbero Senior",
            imageUrl = "https://randomuser.me/api/portraits/men/32.jpg",
            rating = 4.9f,
            reviewCount = 156,
            isAvailable = true
        ),
        Professional(
            id = "prof_2",
            name = "Miguel Ángel",
            role = "Barbero",
            imageUrl = "https://randomuser.me/api/portraits/men/45.jpg",
            rating = 4.7f,
            reviewCount = 89,
            isAvailable = true
        ),
        Professional(
            id = "prof_3",
            name = "Juan Pablo",
            role = "Barbero Junior",
            imageUrl = "https://randomuser.me/api/portraits/men/67.jpg",
            rating = 4.5f,
            reviewCount = 34,
            isAvailable = false
        ),
        Professional(
            id = "prof_4",
            name = "Dra. María González",
            role = "Dermatóloga",
            imageUrl = "https://randomuser.me/api/portraits/women/44.jpg",
            rating = 4.9f,
            reviewCount = 203,
            isAvailable = true
        ),
        Professional(
            id = "prof_5",
            name = "Ana Lucía",
            role = "Esteticista",
            imageUrl = "https://randomuser.me/api/portraits/women/65.jpg",
            rating = 4.8f,
            reviewCount = 178,
            isAvailable = true
        ),
        Professional(
            id = "prof_6",
            name = "Laura Martínez",
            role = "Masajista",
            imageUrl = "https://randomuser.me/api/portraits/women/33.jpg",
            rating = 4.9f,
            reviewCount = 245,
            isAvailable = true
        ),
        Professional(
            id = "prof_7",
            name = "Dr. Roberto Sánchez",
            role = "Abogado Civil",
            imageUrl = "https://randomuser.me/api/portraits/men/52.jpg",
            rating = 4.8f,
            reviewCount = 67,
            isAvailable = true
        ),
        Professional(
            id = "prof_8",
            name = "Dra. Patricia López",
            role = "Odontóloga General",
            imageUrl = "https://randomuser.me/api/portraits/women/28.jpg",
            rating = 4.9f,
            reviewCount = 312,
            isAvailable = true
        )
    )

    /**
     * Mapa de servicios organizados por categoría de negocio.
     * La clave es el tipo de negocio y el valor es la lista de servicios disponibles.
     */
    private val _services = mapOf(
        "barbershop" to listOf(
            Service("srv_1", "Corte Clásico", "Corte de cabello tradicional con tijera", 25.0, 30),
            Service("srv_2", "Corte + Barba", "Corte de cabello y arreglo de barba completo", 40.0, 45),
            Service("srv_3", "Afeitado Clásico", "Afeitado tradicional con navaja y toalla caliente", 20.0, 25),
            Service("srv_4", "Tratamiento Capilar", "Tratamiento hidratante para el cabello", 35.0, 40),
            Service("srv_5", "Corte Infantil", "Corte para niños menores de 12 años", 18.0, 20)
        ),
        "spa" to listOf(
            Service("srv_6", "Masaje Relajante", "Masaje corporal completo de relajación", 80.0, 60),
            Service("srv_7", "Masaje Descontracturante", "Masaje terapéutico para contracturas", 90.0, 60),
            Service("srv_8", "Facial Profundo", "Limpieza facial profunda con hidratación", 65.0, 45),
            Service("srv_9", "Exfoliación Corporal", "Exfoliación completa del cuerpo", 70.0, 50),
            Service("srv_10", "Piedras Calientes", "Terapia con piedras volcánicas", 95.0, 75)
        ),
        "beauty" to listOf(
            Service("srv_11", "Manicure", "Manicure completo con esmaltado", 25.0, 40),
            Service("srv_12", "Pedicure", "Pedicure spa con hidratación", 35.0, 50),
            Service("srv_13", "Uñas Acrílicas", "Aplicación de uñas acrílicas", 55.0, 90),
            Service("srv_14", "Depilación Cera", "Depilación con cera en zonas específicas", 30.0, 30),
            Service("srv_15", "Tinte de Cabello", "Coloración completa del cabello", 75.0, 120)
        ),
        "lawyer" to listOf(
            Service("srv_16", "Consulta Legal", "Asesoría legal inicial de 30 minutos", 50.0, 30),
            Service("srv_17", "Revisión de Contrato", "Análisis y revisión de contratos", 120.0, 60),
            Service("srv_18", "Trámite Notarial", "Gestión de documentos notariales", 150.0, 45),
            Service("srv_19", "Asesoría Empresarial", "Consultoría legal para empresas", 200.0, 60)
        ),
        "dentist" to listOf(
            Service("srv_20", "Limpieza Dental", "Limpieza profesional y pulido", 45.0, 45),
            Service("srv_21", "Blanqueamiento", "Blanqueamiento dental profesional", 180.0, 60),
            Service("srv_22", "Consulta General", "Revisión dental completa", 35.0, 30),
            Service("srv_23", "Extracción Simple", "Extracción de pieza dental", 60.0, 45),
            Service("srv_24", "Ortodoncia Consulta", "Evaluación para ortodoncia", 40.0, 45)
        )
    )

    /**
     * Lista de negocios de ejemplo con toda su información completa.
     * Cada negocio incluye profesionales, servicios, ubicación y calificaciones.
     */
    private val _businesses = listOf(
        Business(
            id = "biz_1",
            name = "BarberKing Studio",
            description = "La mejor barbería de la ciudad. Expertos en cortes modernos y clásicos con más de 10 años de experiencia.",
            category = BusinessCategory.BARBERSHOP,
            imageUrl = "https://images.unsplash.com/photo-1585747860715-2ba37e788b70?w=800",
            rating = 4.8f,
            reviewCount = 324,
            address = "Calle Principal 123, Centro",
            distance = "0.5 km",
            isOpen = true,
            openingHours = "9:00 AM - 8:00 PM",
            professionals = _professionals.filter { it.id in listOf("prof_1", "prof_2", "prof_3") },
            services = _services["barbershop"] ?: emptyList()
        ),
        Business(
            id = "biz_2",
            name = "Zen Spa & Wellness",
            description = "Un oasis de paz y tranquilidad. Ofrecemos tratamientos exclusivos para tu bienestar físico y mental.",
            category = BusinessCategory.SPA,
            imageUrl = "https://images.unsplash.com/photo-1544161515-4ab6ce6db874?w=800",
            rating = 4.9f,
            reviewCount = 567,
            address = "Av. Tranquilidad 456, Zona Rosa",
            distance = "1.2 km",
            isOpen = true,
            openingHours = "8:00 AM - 9:00 PM",
            professionals = _professionals.filter { it.id in listOf("prof_5", "prof_6") },
            services = _services["spa"] ?: emptyList()
        ),
        Business(
            id = "biz_3",
            name = "Glamour Beauty Salon",
            description = "Tu destino de belleza. Manicure, pedicure, tratamientos capilares y mucho más.",
            category = BusinessCategory.BEAUTY_SALON,
            imageUrl = "https://images.unsplash.com/photo-1560066984-138dadb4c035?w=800",
            rating = 4.7f,
            reviewCount = 289,
            address = "Centro Comercial Plaza, Local 45",
            distance = "0.8 km",
            isOpen = true,
            openingHours = "10:00 AM - 7:00 PM",
            professionals = _professionals.filter { it.id == "prof_5" },
            services = _services["beauty"] ?: emptyList()
        ),
        Business(
            id = "biz_4",
            name = "Bufete Sánchez & Asociados",
            description = "Expertos en derecho civil, mercantil y familiar. Más de 20 años protegiendo tus derechos.",
            category = BusinessCategory.LAWYER,
            imageUrl = "https://images.unsplash.com/photo-1589829545856-d10d557cf95f?w=800",
            rating = 4.8f,
            reviewCount = 156,
            address = "Torre Empresarial, Piso 12",
            distance = "2.5 km",
            isOpen = true,
            openingHours = "8:00 AM - 6:00 PM",
            professionals = _professionals.filter { it.id == "prof_7" },
            services = _services["lawyer"] ?: emptyList()
        ),
        Business(
            id = "biz_5",
            name = "Clínica Dental Sonrisas",
            description = "Tu sonrisa es nuestra prioridad. Tecnología de punta y profesionales certificados.",
            category = BusinessCategory.DENTIST,
            imageUrl = "https://images.unsplash.com/photo-1629909613654-28e377c37b09?w=800",
            rating = 4.9f,
            reviewCount = 423,
            address = "Av. Salud 789, Zona Médica",
            distance = "1.8 km",
            isOpen = true,
            openingHours = "7:00 AM - 7:00 PM",
            professionals = _professionals.filter { it.id == "prof_8" },
            services = _services["dentist"] ?: emptyList()
        ),
        Business(
            id = "biz_6",
            name = "Urban Cuts",
            description = "Estilo urbano y moderno. Especialistas en fades, diseños y tendencias actuales.",
            category = BusinessCategory.BARBERSHOP,
            imageUrl = "https://images.unsplash.com/photo-1503951914875-452162b0f3f1?w=800",
            rating = 4.6f,
            reviewCount = 198,
            address = "Calle Trendy 321, Zona Joven",
            distance = "1.5 km",
            isOpen = false,
            openingHours = "10:00 AM - 9:00 PM",
            professionals = _professionals.filter { it.id in listOf("prof_1", "prof_2") },
            services = _services["barbershop"] ?: emptyList()
        ),
        Business(
            id = "biz_7",
            name = "Skin Care Center",
            description = "Centro especializado en dermatología estética. Tratamientos personalizados para tu piel.",
            category = BusinessCategory.SPA,
            imageUrl = "https://images.unsplash.com/photo-1570172619644-dfd03ed5d881?w=800",
            rating = 4.8f,
            reviewCount = 234,
            address = "Plaza Belleza, Local 8",
            distance = "3.2 km",
            isOpen = true,
            openingHours = "9:00 AM - 6:00 PM",
            professionals = _professionals.filter { it.id == "prof_4" },
            services = _services["spa"] ?: emptyList()
        ),
        Business(
            id = "biz_8",
            name = "Dr. Pérez - Medicina General",
            description = "Atención médica integral para toda la familia. Consultas, chequeos y seguimiento.",
            category = BusinessCategory.DOCTOR,
            imageUrl = "https://images.unsplash.com/photo-1631217868264-e5b90bb7e133?w=800",
            rating = 4.7f,
            reviewCount = 312,
            address = "Centro Médico Integral, Cons. 5",
            distance = "2.1 km",
            isOpen = true,
            openingHours = "8:00 AM - 5:00 PM",
            professionals = emptyList(),
            services = emptyList()
        )
    )

    /**
     * Horarios de ejemplo para el sistema de reservas.
     * Muestra slots de tiempo con su disponibilidad.
     */
    private val _timeSlots = listOf(
        TimeSlot("08:00", true),
        TimeSlot("08:30", true),
        TimeSlot("09:00", false),
        TimeSlot("09:30", true),
        TimeSlot("10:00", true),
        TimeSlot("10:30", false),
        TimeSlot("11:00", true),
        TimeSlot("11:30", true),
        TimeSlot("12:00", false),
        TimeSlot("12:30", false),
        TimeSlot("14:00", true),
        TimeSlot("14:30", true),
        TimeSlot("15:00", true),
        TimeSlot("15:30", false),
        TimeSlot("16:00", true),
        TimeSlot("16:30", true),
        TimeSlot("17:00", true),
        TimeSlot("17:30", false),
        TimeSlot("18:00", true),
        TimeSlot("18:30", true),
        TimeSlot("19:00", true),
        TimeSlot("19:30", false)
    )
    
    // ==================== Acceso público a datos ====================
    
    /**
     * Lista pública de todos los negocios.
     * Proporciona acceso directo para los repositorios.
     */
    val businesses: List<Business> get() = _businesses
    
    /**
     * Lista pública de horarios disponibles.
     * Proporciona acceso directo para los repositorios.
     */
    val timeSlots: List<TimeSlot> get() = _timeSlots
    
    /**
     * Obtiene la lista de profesionales.
     */
    fun getProfessionals(): List<Professional> = _professionals
    
    /**
     * Obtiene el mapa de servicios por tipo de negocio.
     */
    fun getServices(): Map<String, List<Service>> = _services
    
    /**
     * Busca un negocio por su ID.
     * @param id Identificador del negocio
     * @return El negocio si existe, null en caso contrario
     */
    fun getBusinessById(id: String): Business? = _businesses.find { it.id == id }
    
    /**
     * Filtra negocios por categoría.
     * @param category La categoría a filtrar
     * @return Lista de negocios de esa categoría
     */
    fun getBusinessesByCategory(category: BusinessCategory): List<Business> = 
        _businesses.filter { it.category == category }
    
    /**
     * Busca negocios por texto.
     * @param query Texto de búsqueda
     * @return Lista de negocios que coinciden
     */
    fun searchBusinesses(query: String): List<Business> = 
        _businesses.filter { 
            it.name.contains(query, ignoreCase = true) || 
            it.description.contains(query, ignoreCase = true) ||
            it.category.displayName.contains(query, ignoreCase = true)
        }
}
