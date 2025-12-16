package com.meetline.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) para representar una foto de un proyecto.
 *
 * Mapea la respuesta del endpoint /api/Projects/{projectId}/photos
 * que retorna las fotos asociadas a un proyecto/negocio.
 *
 * @property id Identificador único de la foto.
 * @property url URL de la imagen en Cloudinary u otro servicio.
 * @property isMain Indica si es la foto principal del proyecto.
 * @property createdAt Timestamp de cuándo se subió la foto.
 */
data class PhotoDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("url")
    val url: String,
    
    @SerializedName("isMain")
    val isMain: Boolean,
    
    @SerializedName("createdAt")
    val createdAt: String
)
