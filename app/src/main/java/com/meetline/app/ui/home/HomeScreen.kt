package com.meetline.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.meetline.app.domain.model.Business
import com.meetline.app.domain.model.BusinessCategory
import com.meetline.app.ui.components.*
import com.meetline.app.ui.theme.*

/**
 * Pantalla principal de la aplicación (Home).
 *
 * Muestra un resumen personalizado para el usuario, incluyendo:
 * - Saludo y avatar del usuario
 * - Barra de búsqueda de negocios
 * - Próximas citas agendadas
 * - Categorías de servicios disponibles
 * - Negocios destacados y cercanos
 *
 * @param onBusinessClick Callback al seleccionar un negocio
 * @param onCategoryClick Callback al seleccionar una categoría
 * @param onAppointmentClick Callback al seleccionar una cita
 * @param onSeeAllBusinesses Callback para ver todos los negocios
 * @param onSeeAllAppointments Callback para ver todas las citas
 * @param viewModel ViewModel que gestiona el estado de la pantalla
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onBusinessClick: (String) -> Unit,
    onCategoryClick: (BusinessCategory) -> Unit,
    onAppointmentClick: (String) -> Unit,
    onSeeAllBusinesses: () -> Unit,
    onSeeAllAppointments: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    if (uiState.isLoading) {
        LoadingIndicator()
        return
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Header con saludo
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Primary, PrimaryLight)
                        ),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Hola, ${uiState.user?.name?.split(" ")?.first() ?: "Usuario"}",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "¿Qué quieres agendar hoy?",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiState.user?.name?.take(2)?.uppercase() ?: "US",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Barra de búsqueda
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.search(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Buscar barbería, spa, dentista...", color = Color.White.copy(alpha = 0.6f))
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.8f)
                            )
                        },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.clearSearch() }) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Limpiar",
                                        tint = Color.White
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White.copy(alpha = 0.5f),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedContainerColor = Color.White.copy(alpha = 0.15f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                            cursorColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            }
        }
        
        // Mostrar resultados de búsqueda si hay query
        if (uiState.searchQuery.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Resultados de búsqueda",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (uiState.isSearching) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
            } else if (uiState.searchResults.isEmpty()) {
                item {
                    EmptyState(
                        title = "Sin resultados",
                        message = "No encontramos negocios que coincidan con \"${uiState.searchQuery}\""
                    )
                }
            } else {
                items(uiState.searchResults) { business ->
                    BusinessCard(
                        business = business,
                        onClick = { onBusinessClick(business.id) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }
            }
        } else {
            // Contenido normal del home
            
            // Próximas citas
            if (uiState.upcomingAppointments.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    SectionHeader(
                        title = "Próximas citas",
                        onSeeAll = onSeeAllAppointments
                    )
                }
                
                items(uiState.upcomingAppointments) { appointment ->
                    AppointmentCardCompact(
                        appointment = appointment,
                        onClick = { onAppointmentClick(appointment.id) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )
                }
            }
            
            // Categorías
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Categorías",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.categories.take(6)) { category ->
                        CategoryCircle(
                            category = category,
                            onClick = { onCategoryClick(category) }
                        )
                    }
                }
            }
            
            // Destacados
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(
                    title = "Destacados",
                    onSeeAll = onSeeAllBusinesses
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.featuredBusinesses) { business ->
                        FeaturedBusinessCard(
                            business = business,
                            onClick = { onBusinessClick(business.id) }
                        )
                    }
                }
            }
            
            // Cerca de ti
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(
                    title = "Cerca de ti",
                    onSeeAll = onSeeAllBusinesses
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            items(uiState.nearbyBusinesses) { business ->
                BusinessCard(
                    business = business,
                    onClick = { onBusinessClick(business.id) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    onSeeAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = OnSurface
        )
        
        TextButton(onClick = onSeeAll) {
            Text(
                text = "Ver todo",
                color = Primary,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
