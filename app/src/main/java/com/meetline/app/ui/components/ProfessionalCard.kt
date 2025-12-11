package com.meetline.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meetline.app.domain.model.Professional
import com.meetline.app.ui.theme.*

/**
 * Tarjeta seleccionable que muestra la información de un profesional.
 *
 * Muestra:
 * - Avatar con iniciales
 * - Nombre y rol
 * - Calificación y estado de disponibilidad
 * - Indicador visual de selección
 *
 * @param professional Objeto Professional con los datos del profesional
 * @param isSelected Indica si esta tarjeta está seleccionada actualmente
 * @param onClick Callback al hacer click en la tarjeta (solo si está disponible)
 * @param modifier Modificador para el layout
 */
@Composable
fun ProfessionalCard(
    professional: Professional,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = professional.isAvailable, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryContainer else Surface
        ),
        border = if (isSelected) {
            CardDefaults.outlinedCardBorder().copy(width = 2.dp)
        } else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        if (professional.isAvailable) {
                            Brush.linearGradient(listOf(Primary, PrimaryLight))
                        } else {
                            Brush.linearGradient(listOf(Color.Gray, Color.LightGray))
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = professional.name.take(2).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = professional.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (professional.isAvailable) OnSurface else Color.Gray
                    )
                    
                    if (!professional.isAvailable) {
                        Badge(
                            containerColor = Color.Gray.copy(alpha = 0.2f),
                            contentColor = Color.Gray
                        ) {
                            Text(
                                text = "No disponible",
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
                
                Text(
                    text = professional.role,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (professional.isAvailable) OnSurfaceVariant else Color.Gray
                )
            }
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Seleccionado",
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * Tarjeta compacta de profesional para listas horizontales.
 */
@Composable
fun ProfessionalChip(
    professional: Professional,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Primary else SurfaceVariant,
        tonalElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) Color.White.copy(alpha = 0.2f) 
                        else Primary.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = professional.name.take(2).uppercase(),
                    color = if (isSelected) Color.White else Primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = professional.name.split(" ").first(),
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) Color.White else OnSurface,
                fontWeight = FontWeight.Medium
            }
        }
    }
}
