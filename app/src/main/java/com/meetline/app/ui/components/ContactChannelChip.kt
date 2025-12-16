package com.meetline.app.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.meetline.app.domain.model.ContactChannel
import com.meetline.app.domain.model.ContactChannelType
import com.meetline.app.ui.theme.*

/**
 * Chip clickeable que muestra un canal de contacto.
 * 
 * Al hacer click, abre la aplicación correspondiente (WhatsApp, Facebook, etc.)
 * o realiza la acción apropiada (llamar, enviar email, etc.).
 * 
 * @param channel Canal de contacto a mostrar
 * @param modifier Modificador opcional
 */
@Composable
fun ContactChannelChip(
    channel: ContactChannel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Surface(
        modifier = modifier.clickable {
            openContactChannel(context, channel)
        },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = getIconForChannel(channel.type),
                contentDescription = channel.type.name,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            
            Text(
                text = channel.displayValue,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Obtiene el ícono apropiado para cada tipo de canal.
 */
private fun getIconForChannel(type: ContactChannelType): ImageVector {
    return when (type) {
        ContactChannelType.WHATSAPP -> Icons.Default.Phone
        ContactChannelType.FACEBOOK -> Icons.Default.Share
        ContactChannelType.TIKTOK -> Icons.Default.VideoLibrary
        ContactChannelType.INSTAGRAM -> Icons.Default.CameraAlt
        ContactChannelType.EMAIL -> Icons.Default.Email
        ContactChannelType.PHONE -> Icons.Default.Call
        ContactChannelType.WEBSITE -> Icons.Default.Language
        ContactChannelType.TWITTER -> Icons.Default.Tag
        else -> Icons.Default.Link
    }
}

/**
 * Abre el canal de contacto en la aplicación correspondiente.
 * 
 * Crea un Intent apropiado según el tipo de canal y lo ejecuta.
 * Maneja errores si la aplicación no está instalada.
 */
private fun openContactChannel(context: android.content.Context, channel: ContactChannel) {
    val intent = when (channel.type) {
        ContactChannelType.WHATSAPP -> {
            // Abrir WhatsApp con el número
            // Formato: https://wa.me/573025689564
            val phoneNumber = channel.value.replace(Regex("[^0-9]"), "")
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://wa.me/$phoneNumber")
            }
        }
        ContactChannelType.FACEBOOK -> {
            // Abrir Facebook
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(channel.value)
            }
        }
        ContactChannelType.TIKTOK -> {
            // Abrir TikTok
            val username = if (channel.value.startsWith("@")) {
                channel.value.substring(1)
            } else {
                channel.value
            }
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://www.tiktok.com/@$username")
            }
        }
        ContactChannelType.INSTAGRAM -> {
            // Abrir Instagram
            val username = if (channel.value.startsWith("@")) {
                channel.value.substring(1)
            } else {
                channel.value
            }
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://www.instagram.com/$username")
            }
        }
        ContactChannelType.PHONE -> {
            // Abrir marcador
            Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${channel.value}")
            }
        }
        ContactChannelType.EMAIL -> {
            // Abrir email
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:${channel.value}")
            }
        }
        ContactChannelType.WEBSITE -> {
            // Abrir en navegador
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(channel.value)
            }
        }
        else -> {
            // Intentar abrir como URL
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(channel.value)
            }
        }
    }
    
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Manejar error si no hay app instalada
        // Podríamos mostrar un Toast o Snackbar aquí
    }
}
