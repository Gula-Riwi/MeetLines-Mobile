package com.meetline.app.ui.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.meetline.app.ui.components.*
import com.meetline.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla para agendar una nueva cita.
 *
 * Guía al usuario a través de un proceso de 4 pasos:
 * 1. Selección de profesional (opcional)
 * 2. Selección de servicio
 * 3. Selección de fecha
 * 4. Selección de hora
 *
 * Muestra un resumen antes de confirmar la reserva.
 *
 * @param onNavigateBack Callback para volver a la pantalla anterior
 * @param onBookingSuccess Callback ejecutado cuando la reserva se completa exitosamente
 * @param viewModel ViewModel que gestiona el flujo de reserva y validaciones
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    onNavigateBack: () -> Unit,
    onBookingSuccess: () -> Unit,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState.bookingSuccess) {
        if (uiState.bookingSuccess) {
            onBookingSuccess()
        }
    }
    
    if (uiState.isLoading) {
        LoadingIndicator()
        return
    }
    
    val business = uiState.business
    if (business == null) {
        EmptyState(
            title = "Error",
            message = "No se pudo cargar la información"
        )
        return
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Agendar cita",
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = OnSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Surface
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Resumen
                    if (uiState.selectedService != null && uiState.selectedDate != null && uiState.selectedTime != null) {
                        val dateFormat = SimpleDateFormat("EEE, dd MMM", Locale("es", "ES"))
                        
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = PrimaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = uiState.selectedService!!.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = OnSurface
                                    )
                                    Text(
                                        text = "${dateFormat.format(Date(uiState.selectedDate!!))} • ${uiState.selectedTime}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OnSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "$${String.format("%.0f", uiState.selectedService!!.price)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    
                    Button(
                        onClick = { viewModel.confirmBooking() },
                        enabled = viewModel.canProceed() && !uiState.isBooking,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        if (uiState.isBooking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Confirmar Cita",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(paddingValues),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Info del negocio
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = business.imageUrl.ifEmpty { business.category.imageUrl },
                            contentDescription = business.name,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column {
                            Text(
                                text = business.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurface
                            )
                            Text(
                                text = business.address,
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            // Paso 1: Seleccionar profesional
            if (business.professionals.isNotEmpty()) {
                item {
                    StepHeader(
                        number = 1,
                        title = "Elige profesional",
                        isCompleted = uiState.selectedProfessional != null
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(business.professionals) { professional ->
                            ProfessionalChip(
                                professional = professional,
                                isSelected = uiState.selectedProfessional?.id == professional.id,
                                onClick = { viewModel.selectProfessional(professional) }
                            )
                        }
                    }
                }
            }
            
            // Paso 2: Seleccionar servicio
            item {
                StepHeader(
                    number = if (business.professionals.isNotEmpty()) 2 else 1,
                    title = "Elige servicio",
                    isCompleted = uiState.selectedService != null
                )
                
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            items(business.services) { service ->
                ServiceCard(
                    service = service,
                    isSelected = uiState.selectedService?.id == service.id,
                    onClick = { viewModel.selectService(service) }
                )
            }
            
            // Paso 3: Seleccionar fecha
            item {
                Spacer(modifier = Modifier.height(8.dp))
                
                StepHeader(
                    number = if (business.professionals.isNotEmpty()) 3 else 2,
                    title = "Elige fecha",
                    isCompleted = uiState.selectedDate != null
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                DatePicker(
                    selectedDate = uiState.selectedDate,
                    onDateSelected = { viewModel.selectDate(it) }
                )
            }
            
            // Paso 4: Seleccionar hora
            if (uiState.selectedDate != null) {
                if (uiState.availableTimeSlots.isNotEmpty()) {
                    item {
                        StepHeader(
                            number = if (business.professionals.isNotEmpty()) 4 else 3,
                            title = "Elige hora",
                            isCompleted = uiState.selectedTime != null
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        TimeSlotPicker(
                            timeSlots = uiState.availableTimeSlots,
                            selectedTime = uiState.selectedTime,
                            onTimeSelected = { viewModel.selectTime(it) }
                        )
                    }
                } else {
                    // Mensaje cuando no hay horarios disponibles
                    item {
                        StepHeader(
                            number = if (business.professionals.isNotEmpty()) 4 else 3,
                            title = "Elige hora",
                            isCompleted = false
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.errorContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (business.isOpen) "No hay horarios disponibles" else "Cerrado hoy",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (business.isOpen) 
                                            "Todos los horarios están ocupados para esta fecha. Intenta con otro día." 
                                        else 
                                            "El negocio está cerrado hoy. Selecciona otro día para agendar tu cita.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun StepHeader(
    number: Int,
    title: String,
    isCompleted: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isCompleted) Primary else SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Text(
                    text = number.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = OnSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = OnSurface
        )
    }
}
