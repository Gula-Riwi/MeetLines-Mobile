package com.meetline.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.meetline.app.domain.model.TimeSlot
import com.meetline.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Componente de calendario para selección de fechas.
 *
 * Permite navegar entre meses y seleccionar un día específico.
 * Muestra visualmente:
 * - Días del mes actual
 * - Día actual resaltado
 * - Día seleccionado resaltado
 * - Días pasados deshabilitados
 *
 * @param selectedDate Fecha seleccionada en milisegundos (o null)
 * @param onDateSelected Callback al seleccionar una fecha
 * @param modifier Modificador para el layout
 */
@Composable
fun DatePicker(
    selectedDate: Long?,
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    
    val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("es-ES"))
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header con navegación de mes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        currentMonth = (currentMonth.clone() as Calendar).apply {
                            add(Calendar.MONTH, -1)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Mes anterior"
                    )
                }
                
                Text(
                    text = dateFormat.format(currentMonth.time).replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                IconButton(
                    onClick = {
                        currentMonth = (currentMonth.clone() as Calendar).apply {
                            add(Calendar.MONTH, 1)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Mes siguiente"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Días de la semana
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb").forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Días del mes
            val daysInMonth = getDaysInMonth(currentMonth)
            val today = Calendar.getInstance()
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(240.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items(daysInMonth) { day ->
                    if (day == 0) {
                        Box(modifier = Modifier.aspectRatio(1f))
                    } else {
                        val calendar = (currentMonth.clone() as Calendar).apply {
                            set(Calendar.DAY_OF_MONTH, day)
                        }
                        val isToday = calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                                calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
                        val isPast = calendar.before(today) && !isToday
                        val isSelected = selectedDate?.let {
                            val selectedCal = Calendar.getInstance().apply { timeInMillis = it }
                            calendar.get(Calendar.YEAR) == selectedCal.get(Calendar.YEAR) &&
                                    calendar.get(Calendar.DAY_OF_YEAR) == selectedCal.get(Calendar.DAY_OF_YEAR)
                        } ?: false
                        
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isSelected -> Primary
                                        isToday -> PrimaryContainer
                                        else -> Color.Transparent
                                    }
                                )
                                .then(
                                    if (isToday && !isSelected) {
                                        Modifier.border(1.dp, Primary, CircleShape)
                                    } else Modifier
                                )
                                .clickable(enabled = !isPast) {
                                    onDateSelected(calendar.timeInMillis)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = when {
                                    isSelected -> Color.White
                                    isPast -> Color.Gray
                                    isToday -> Primary
                                    else -> OnSurface
                                },
                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Componente para selección de horarios disponibles.
 *
 * Organiza los horarios en secciones (Mañana, Tarde, Noche).
 * Muestra visualmente la disponibilidad y selección de cada horario.
 *
 * @param timeSlots Lista de horarios disponibles (TimeSlot)
 * @param selectedTime Hora seleccionada en formato String (HH:mm) o null
 * @param onTimeSelected Callback al seleccionar un horario
 * @param modifier Modificador para el layout
 */
@Composable
fun TimeSlotPicker(
    timeSlots: List<TimeSlot>,
    selectedTime: String?,
    onTimeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Horarios disponibles",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Mañana
            val morningSlots = timeSlots.filter { 
                it.time.split(":")[0].toInt() < 12 
            }
            if (morningSlots.isNotEmpty()) {
                Text(
                    text = "Mañana",
                    style = MaterialTheme.typography.labelMedium,
                    color = OnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                TimeSlotRow(
                    slots = morningSlots,
                    selectedTime = selectedTime,
                    onTimeSelected = onTimeSelected
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Tarde
            val afternoonSlots = timeSlots.filter { 
                val hour = it.time.split(":")[0].toInt()
                hour >= 12 && hour < 18
            }
            if (afternoonSlots.isNotEmpty()) {
                Text(
                    text = "Tarde",
                    style = MaterialTheme.typography.labelMedium,
                    color = OnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                TimeSlotRow(
                    slots = afternoonSlots,
                    selectedTime = selectedTime,
                    onTimeSelected = onTimeSelected
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Noche
            val eveningSlots = timeSlots.filter { 
                it.time.split(":")[0].toInt() >= 18
            }
            if (eveningSlots.isNotEmpty()) {
                Text(
                    text = "Noche",
                    style = MaterialTheme.typography.labelMedium,
                    color = OnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                TimeSlotRow(
                    slots = eveningSlots,
                    selectedTime = selectedTime,
                    onTimeSelected = onTimeSelected
                )
            }
        }
    }
}

@Composable
private fun TimeSlotRow(
    slots: List<TimeSlot>,
    selectedTime: String?,
    onTimeSelected: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.height(((slots.size / 4 + 1) * 48).dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(slots) { slot ->
            val isSelected = slot.time == selectedTime
            
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = slot.isAvailable) {
                        onTimeSelected(slot.time)
                    },
                shape = RoundedCornerShape(8.dp),
                color = when {
                    isSelected -> Primary
                    slot.isAvailable -> SurfaceVariant
                    else -> Color.Gray.copy(alpha = 0.2f)
                }
            ) {
                Text(
                    text = slot.time,
                    style = MaterialTheme.typography.bodyMedium,
                    color = when {
                        isSelected -> Color.White
                        slot.isAvailable -> OnSurface
                        else -> Color.Gray
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }
        }
    }
}

/**
 * Obtiene los días del mes con espacios vacíos para alineación.
 */
private fun getDaysInMonth(calendar: Calendar): List<Int> {
    val cal = calendar.clone() as Calendar
    cal.set(Calendar.DAY_OF_MONTH, 1)
    
    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    
    val days = mutableListOf<Int>()
    
    // Espacios vacíos antes del primer día
    repeat(firstDayOfWeek) { days.add(0) }
    
    // Días del mes
    for (day in 1..daysInMonth) {
        days.add(day)
    }
    
    return days
}
