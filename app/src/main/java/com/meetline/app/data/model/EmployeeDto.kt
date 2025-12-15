package com.meetline.app.data.model

import com.google.gson.annotations.SerializedName
import com.meetline.app.domain.model.Professional
import java.util.UUID

/**
 * Data Transfer Object (DTO) para representar un empleado en las respuestas de la API pública.
 *
 * Esta clase mapea la estructura JSON del endpoint público de empleados de proyectos.
 * Contiene información básica del empleado que luego se convierte al modelo de dominio [Professional].
 *
 * @property id Identificador único del empleado (UUID).
 * @property name Nombre completo del empleado.
 * @property role Rol o cargo del empleado (ej: "Barbero", "Estilista", "Admin").
 *
 * @see Professional Modelo de dominio correspondiente.
 * @see toProfessional Función de extensión para convertir a modelo de dominio.
 */
data class EmployeeDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("role")
    val role: String
)

/**
 * Convierte un [EmployeeDto] a un modelo de dominio [Professional].
 *
 * Transforma los datos básicos del empleado de la API al modelo interno de la aplicación.
 * Los campos no proporcionados por la API se rellenan con valores por defecto.
 *
 * @return Una instancia de [Professional] con los datos del empleado y valores por defecto.
 */
fun EmployeeDto.toProfessional(): Professional {
    return Professional(
        id = id, // Usar el ID real del empleado de la API
        name = name,
        role = role,
        imageUrl = "https://ui-avatars.com/api/?name=${name.replace(" ", "+")}&background=random", // Avatar generado
        rating = 0.0f, // Sin calificación por defecto
        reviewCount = 0, // Sin reseñas por defecto
        isAvailable = true // Disponible por defecto
    )
}
