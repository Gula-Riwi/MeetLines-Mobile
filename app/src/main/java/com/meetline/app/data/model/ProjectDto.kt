package com.meetline.app.data.model

import com.google.gson.annotations.SerializedName
import com.meetline.app.domain.model.Business
import com.meetline.app.domain.model.BusinessCategory

/**
 * Data Transfer Object (DTO) para representar un proyecto/negocio en las respuestas de la API pública.
 *
 * Esta clase mapea la estructura JSON del endpoint público de proyectos.
 * Contiene información básica del proyecto que luego se convierte al modelo de dominio [Business].
 *
 * @property id Identificador único del proyecto.
 * @property name Nombre del proyecto/negocio.
 * @property description Descripción del proyecto.
 * @property industry Industria o sector al que pertenece el proyecto.
 *
 * @see Business Modelo de dominio correspondiente.
 * @see toDomain Función de extensión para convertir a modelo de dominio.
 */
data class ProjectDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("industry")
    val industry: String
)

/**
 * Convierte un [ProjectDto] a un modelo de dominio [Business].
 *
 * Transforma los datos básicos del proyecto de la API al modelo interno de la aplicación.
 * Los campos no proporcionados por la API se rellenan con valores por defecto.
 *
 * Mapeo de industrias a categorías:
 * - "Restaurante" -> RESTAURANT
 * - "E-commerce" -> OTHER (por ahora, hasta tener una categoría específica)
 * - "Servicios" -> OTHER
 * - Otros -> OTHER
 *
 * @param openingHours Horario de trabajo calculado desde los slots disponibles (opcional)
 * @param contactChannels Lista de canales de contacto del negocio (opcional)
 * @return Una instancia de [Business] con los datos del proyecto y valores por defecto.
 */
fun ProjectDto.toDomain(
    openingHours: String? = null,
    contactChannels: List<com.meetline.app.domain.model.ContactChannel> = emptyList()
): Business {
    // Mapear la industria a una categoría de negocio
    val category = when (industry.lowercase()) {
        "restaurante" -> BusinessCategory.RESTAURANT
        "barbería", "barberia" -> BusinessCategory.BARBERSHOP
        "spa", "spa & bienestar" -> BusinessCategory.SPA
        "salón de belleza", "salon de belleza", "belleza" -> BusinessCategory.BEAUTY_SALON
        "abogado", "legal" -> BusinessCategory.LAWYER
        "dentista", "odontología", "odontologia" -> BusinessCategory.DENTIST
        "médico", "medico", "medicina", "salud" -> BusinessCategory.DOCTOR
        "gimnasio", "gym", "fitness" -> BusinessCategory.GYM
        "veterinaria", "veterinario" -> BusinessCategory.VETERINARY
        else -> BusinessCategory.OTHER
    }
    
    return Business(
        id = id,
        name = name,
        description = description,
        category = category,
        // Valores por defecto para campos no proporcionados por la API
        imageUrl = category.imageUrl, // Usar la imagen por defecto de la categoría
        rating = 0.0f,
        reviewCount = 0,
        address = "Dirección no disponible",
        distance = "N/A",
        isOpen = true,
        openingHours = openingHours ?: "Horario no disponible",
        professionals = emptyList(),
        services = listOf(
            // SERVICIO QUEMADO PARA PRUEBAS
            com.meetline.app.domain.model.Service(
                id = "1",
                name = "Servicio de Prueba",
                description = "Servicio para probar disponibilidad",
                price = 15000.0,
                duration = 60, // 60 minutos
                imageUrl = "https://images.unsplash.com/photo-1585747860715-2ba37e788b70?w=400"
            )
        ),
        contactChannels = contactChannels,
        isFavorite = false
    )
}

/**
 * Calcula el horario de trabajo basándose en los slots disponibles.
 *
 * Extrae la hora de inicio del primer slot y la hora de fin del último slot
 * para determinar el rango de horario de trabajo del día.
 *
 * @param slots Lista de slots disponibles del día
 * @return String con el formato "HH:mm - HH:mm" o "Horario no disponible" si no hay slots
 */
fun calculateWorkingHours(slots: List<SlotDto>): String {
    if (slots.isEmpty()) {
        return "Horario no disponible"
    }
    
    return try {
        val firstSlot = slots.first()
        val lastSlot = slots.last()
        
        // Extraer hora de inicio del primer slot (ej: "2025-12-15T09:00:00-05:00" -> "09:00")
        val openingTime = extractTime(firstSlot.startTime)
        
        // Extraer hora de fin del último slot (ej: "2025-12-15T17:00:00-05:00" -> "17:00")
        val closingTime = extractTime(lastSlot.endTime)
        
        "$openingTime - $closingTime"
    } catch (e: Exception) {
        "Horario no disponible"
    }
}

/**
 * Extrae la hora en formato HH:mm de un timestamp ISO-8601.
 *
 * @param isoTimestamp Timestamp en formato ISO-8601 (ej: "2025-12-15T09:00:00-05:00")
 * @return Hora en formato HH:mm (ej: "09:00")
 */
private fun extractTime(isoTimestamp: String): String {
    return try {
        // Parsear usando java.time (API 26+)
        val zonedDateTime = java.time.ZonedDateTime.parse(isoTimestamp)
        java.time.format.DateTimeFormatter.ofPattern("HH:mm").format(zonedDateTime)
    } catch (e: Exception) {
        // Fallback: extraer manualmente si falla el parseo
        // "2025-12-15T09:00:00-05:00" -> "09:00"
        isoTimestamp.substringAfter("T").substring(0, 5)
    }
}
