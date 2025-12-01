package com.meetline.app.ui.appointments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.meetline.app.domain.model.AppointmentStatus
import com.meetline.app.ui.components.*
import com.meetline.app.ui.theme.*

/**
 * Pantalla que muestra el historial y las próximas citas del usuario.
 *
 * Organiza las citas en dos pestañas: "Próximas" y "Historial".
 * Permite visualizar los detalles de cada cita y cancelar las citas futuras.
 *
 * @param onAppointmentClick Callback al seleccionar una cita para ver detalles
 * @param viewModel ViewModel que gestiona la lista de citas y acciones
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(
    onAppointmentClick: (String) -> Unit,
    viewModel: AppointmentsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var showCancelDialog by remember { mutableStateOf<String?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis Citas",
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface
                )
            )
        },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(paddingValues)
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = Surface,
                contentColor = Primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab]),
                        color = Primary
                    )
                }
            ) {
                Tab(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("Próximas")
                            if (uiState.upcomingAppointments.isNotEmpty()) {
                                Badge(
                                    containerColor = Primary,
                                    contentColor = Color.White
                                ) {
                                    Text(uiState.upcomingAppointments.size.toString())
                                }
                            }
                        }
                    }
                )
                Tab(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    text = { Text("Historial") }
                )
            }
            
            if (uiState.isLoading) {
                LoadingIndicator()
            } else {
                val appointments = if (uiState.selectedTab == 0) {
                    uiState.upcomingAppointments
                } else {
                    uiState.pastAppointments
                }
                
                if (appointments.isEmpty()) {
                    EmptyState(
                        title = if (uiState.selectedTab == 0) "Sin citas próximas" else "Sin historial",
                        message = if (uiState.selectedTab == 0) {
                            "No tienes citas programadas. ¡Agenda una ahora!"
                        } else {
                            "Aún no tienes citas completadas"
                        }
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(appointments) { appointment ->
                            AppointmentCard(
                                appointment = appointment,
                                onClick = { onAppointmentClick(appointment.id) }
                            )
                            
                            // Mostrar botón de cancelar solo para próximas citas
                            if (uiState.selectedTab == 0 && 
                                appointment.status != AppointmentStatus.CANCELLED) {
                                OutlinedButton(
                                    onClick = { showCancelDialog = appointment.id },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Error
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Cancelar cita")
                                }
                            }
                        }
                        
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
    
    // Diálogo de confirmación para cancelar
    if (showCancelDialog != null) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = null },
            title = { Text("Cancelar cita") },
            text = { Text("¿Estás seguro de que quieres cancelar esta cita?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCancelDialog?.let { viewModel.cancelAppointment(it) }
                        showCancelDialog = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Error)
                ) {
                    Text("Sí, cancelar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = null }) {
                    Text("No")
                }
            }
        )
    }
}
