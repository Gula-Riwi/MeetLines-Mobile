package com.meetline.app.domain.model

/**
 * Tipos de canales de contacto soportados.
 * 
 * Define los diferentes medios de comunicación que un negocio
 * puede tener disponibles para contacto con clientes.
 */
enum class ContactChannelType {
    /** Canal de WhatsApp */
    WHATSAPP,
    
    /** Página de Facebook */
    FACEBOOK,
    
    /** Perfil de TikTok */
    TIKTOK,
    
    /** Perfil de Instagram */
    INSTAGRAM,
    
    /** Perfil de Twitter/X */
    TWITTER,
    
    /** Sitio web */
    WEBSITE,
    
    /** Correo electrónico */
    EMAIL,
    
    /** Número de teléfono */
    PHONE,
    
    /** Otros canales no categorizados */
    OTHER;
    
    companion object {
        /**
         * Convierte un string a ContactChannelType.
         * 
         * @param type String con el tipo de canal
         * @return ContactChannelType correspondiente
         */
        fun fromString(type: String): ContactChannelType {
            return when (type.lowercase()) {
                "whatsapp" -> WHATSAPP
                "facebook" -> FACEBOOK
                "tiktok" -> TIKTOK
                "instagram" -> INSTAGRAM
                "twitter" -> TWITTER
                "website" -> WEBSITE
                "email" -> EMAIL
                "phone" -> PHONE
                else -> OTHER
            }
        }
    }
}

/**
 * Modelo de dominio para un canal de contacto.
 * 
 * Representa un medio de comunicación que el negocio ofrece
 * a sus clientes (WhatsApp, redes sociales, teléfono, etc.).
 * 
 * @property type Tipo de canal de contacto
 * @property value Valor del canal (número, URL, username, etc.)
 * @property displayValue Valor formateado para mostrar al usuario
 */
data class ContactChannel(
    val type: ContactChannelType,
    val value: String,
    val displayValue: String = value
)
