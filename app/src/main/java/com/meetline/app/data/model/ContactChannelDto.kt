package com.meetline.app.data.model

import com.google.gson.annotations.SerializedName
import com.meetline.app.domain.model.ContactChannel
import com.meetline.app.domain.model.ContactChannelType
import org.json.JSONObject

/**
 * DTO para canales de contacto de la API.
 * 
 * Mapea la respuesta del endpoint /api/projects/{id}/channels/public
 * 
 * @property type Tipo de canal (whatsapp, facebook, tiktok, etc.)
 * @property value JSON string con el valor: "{\"value\": \"...\"}"
 */
data class ContactChannelDto(
    @SerializedName("type")
    val type: String,
    
    @SerializedName("value")
    val value: String
)

/**
 * Convierte ContactChannelDto a modelo de dominio.
 * 
 * Parsea el JSON string del campo value para extraer el valor real
 * y lo formatea apropiadamente según el tipo de canal.
 * 
 * @return ContactChannel con el valor parseado y formateado
 */
fun ContactChannelDto.toDomain(): ContactChannel {
    // Parsear el JSON string para extraer el valor real
    // Ejemplo: "{\"value\": \"3025689564\"}" -> "3025689564"
    val actualValue = try {
        val jsonObject = JSONObject(value)
        jsonObject.getString("value")
    } catch (e: Exception) {
        // Fallback al valor original si falla el parseo
        value
    }
    
    val channelType = ContactChannelType.fromString(type)
    
    return ContactChannel(
        type = channelType,
        value = actualValue,
        displayValue = formatDisplayValue(channelType, actualValue)
    )
}

/**
 * Formatea el valor para mostrar al usuario de forma amigable.
 * 
 * @param type Tipo de canal
 * @param value Valor sin formatear
 * @return Valor formateado para display
 */
private fun formatDisplayValue(type: ContactChannelType, value: String): String {
    return when (type) {
        ContactChannelType.WHATSAPP, ContactChannelType.PHONE -> {
            // Formatear número de teléfono
            // 3025689564 -> (302) 568-9564
            if (value.length == 10 && value.all { it.isDigit() }) {
                "(${value.substring(0, 3)}) ${value.substring(3, 6)}-${value.substring(6)}"
            } else {
                value
            }
        }
        ContactChannelType.FACEBOOK, 
        ContactChannelType.TIKTOK, 
        ContactChannelType.INSTAGRAM, 
        ContactChannelType.TWITTER -> {
            // Extraer username de URL si es necesario
            if (value.startsWith("http")) {
                // https://web.facebook.com/Zaipaund -> @Zaipaund
                val username = value.substringAfterLast("/")
                if (username.isNotEmpty()) "@$username" else value
            } else {
                // Si ya es un username, agregar @
                if (!value.startsWith("@")) "@$value" else value
            }
        }
        else -> value
    }
}
