package com.meetline.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.meetline.app.domain.model.Appointment
import com.meetline.app.domain.model.AppointmentStatus
import com.meetline.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Tarjeta que muestra los detalles de una cita.
 *
 * Visualiza la información clave de una cita incluyendo:
 * - Fecha y hora formateadas
 * - Estado de la cita (Pendiente, Confirmada, etc.)
 * - Información del negocio (Nombre, dirección, imagen)
 * - Servicio y profesional asignado
 * - Precio y duración
 *
 * @param appointment Objeto Appointment con los datos de la cita
 * @param onClick Callback al hacer click en la tarjeta
 * @param modifier Modificador para el layout
 */
@Composable
fun AppointmentCard(
    appointment: Appointment,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("EEE, dd MMM", Locale.forLanguageTag("es-ES"))
    val formattedDate = dateFormat.format(Date(appointment.date))
    
    val statusColor = when (appointment.status) {
        AppointmentStatus.PENDING -> Color(0xFFFFA726)
        AppointmentStatus.CONFIRMED -> Color(0xFF66BB6A)
        AppointmentStatus.COMPLETED -> Color(0xFF42A5F5)
        AppointmentStatus.CANCELLED -> Color(0xFFEF5350)
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            // Header con gradiente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Primary, PrimaryLight)
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = formattedDate.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = appointment.time,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White
                    ) {
                        Text(
                            text = appointment.status.displayName,
                            style = MaterialTheme.typography.labelMedium,
                            color = statusColor,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
            
            // Contenido
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Negocio
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = appointment.business.imageUrl.ifEmpty { appointment.business.category.imageUrl },
                        contentDescription = appointment.business.name,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = appointment.business.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = appointment.business.address,
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Background
                )
                
                // Servicio y profesional
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Servicio",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                        Text(
                            text = appointment.service.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = OnSurface
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Profesional",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                        Text(
                            text = appointment.professional.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = OnSurface
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Precio y duración
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SurfaceVariant
                        ) {
                            Text(
                                text = "⏱ ${appointment.service.duration} min",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    
                    Text(
                        text = "$${String.format("%.0f", appointment.service.price)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
            }
        }
    }
}

/**
 * Tarjeta compacta de cita para listas.
 */
@Composable
fun AppointmentCardCompact(
    appointment: Appointment,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd MMM", Locale.forLanguageTag("es-ES"))
    val formattedDate = dateFormat.format(Date(appointment.date))
    
    val statusColor = when (appointment.status) {
        AppointmentStatus.PENDING -> Color(0xFFFFA726)
        AppointmentStatus.CONFIRMED -> Color(0xFF66BB6A)
        AppointmentStatus.COMPLETED -> Color(0xFF42A5F5)
        AppointmentStatus.CANCELLED -> Color(0xFFEF5350)
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Fecha
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Primary.copy(alpha = 0.1f)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = formattedDate.split(" ")[0],
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                    Text(
                        text = formattedDate.split(" ")[1].uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appointment.business.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = OnSurface
                )
                Text(
                    text = "${appointment.service.name} • ${appointment.time}",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }
            
            // Estado
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(statusColor, RoundedCornerShape(4.dp))
            )
        }
    }
}
