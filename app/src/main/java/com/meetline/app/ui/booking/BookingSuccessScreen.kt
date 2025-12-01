package com.meetline.app.ui.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meetline.app.ui.theme.*

@Composable
fun BookingSuccessScreen(
    onGoHome: () -> Unit,
    onViewAppointments: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Primary, PrimaryLight)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Surface)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icono de éxito
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Success.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Success),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "¡Cita Agendada!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Tu cita ha sido confirmada exitosamente. Recibirás una notificación de recordatorio.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Botones
                Button(
                    onClick = onViewAppointments,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text(
                        text = "Ver mis citas",
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedButton(
                    onClick = onGoHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Volver al inicio",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
