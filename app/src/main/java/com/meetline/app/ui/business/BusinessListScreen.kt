package com.meetline.app.ui.business

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.meetline.app.ui.components.*
import com.meetline.app.ui.theme.*

/**
 * Pantalla que muestra el listado de negocios.
 *
 * Permite visualizar y filtrar negocios por categoría.
 * Muestra una lista de tarjetas de negocio con información resumida.
 *
 * @param onNavigateBack Callback para volver a la pantalla anterior
 * @param onBusinessClick Callback al seleccionar un negocio de la lista
 * @param viewModel ViewModel que gestiona la lista de negocios y filtros
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessListScreen(
    onNavigateBack: () -> Unit,
    onBusinessClick: (String) -> Unit,
    viewModel: BusinessListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.selectedCategory?.displayName ?: "Todos los negocios",
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
        containerColor = Background
    ) { paddingValues ->
        if (uiState.isLoading) {
            LoadingIndicator()
            return@Scaffold
        }
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Filtros de categoría
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Opción "Todos"
                    item {
                        FilterChip(
                            selected = uiState.selectedCategory == null,
                            onClick = { viewModel.filterByCategory(null) },
                            label = { Text("Todos") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    
                    items(uiState.categories) { category ->
                        FilterChip(
                            selected = uiState.selectedCategory == category,
                            onClick = { viewModel.filterByCategory(category) },
                            label = { 
                                Text(category.displayName)
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Lista de negocios
            if (uiState.businesses.isEmpty()) {
                item {
                    EmptyState(
                        icon = "🏪",
                        title = "Sin negocios",
                        message = "No hay negocios disponibles en esta categoría"
                    )
                }
            } else {
                item {
                    Text(
                        text = "${uiState.businesses.size} negocios encontrados",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }
                
                items(uiState.businesses) { business ->
                    BusinessCard(
                        business = business,
                        onClick = { onBusinessClick(business.id) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}
