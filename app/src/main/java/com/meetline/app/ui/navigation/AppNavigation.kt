package com.meetline.app.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.meetline.app.data.local.SessionManager
import com.meetline.app.ui.appointments.AppointmentsScreen
import com.meetline.app.ui.booking.BookingScreen
import com.meetline.app.ui.booking.BookingSuccessScreen
import com.meetline.app.ui.business.BusinessDetailScreen
import com.meetline.app.ui.business.BusinessListScreen
import com.meetline.app.ui.home.HomeScreen
import com.meetline.app.ui.login.LoginScreen
import com.meetline.app.ui.profile.ProfileScreen
import com.meetline.app.ui.register.RegisterScreen
import com.meetline.app.ui.theme.*

data class BottomNavItemData(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItemData(
        route = Screen.Home.route,
        title = "Inicio",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItemData(
        route = Screen.Appointments.route,
        title = "Citas",
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth
    ),
    BottomNavItemData(
        route = Screen.Profile.route,
        title = "Perfil",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
)

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route, // App pública - inicia en Home
    sessionManager: SessionManager
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // Rutas donde mostrar el bottom bar
    val showBottomBar = currentDestination?.route in listOf(
        Screen.Home.route,
        Screen.Appointments.route,
        Screen.Profile.route
    )
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Background,
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    navController = navController,
                    currentRoute = currentDestination?.route,
                    sessionManager = sessionManager
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Auth screens
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }
            
            composable(Screen.Register.route) {
                RegisterScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            
            // Main screens
            composable(Screen.Home.route) {
                HomeScreen(
                    onBusinessClick = { businessId ->
                        navController.navigate(Screen.BusinessDetail.createRoute(businessId))
                    },
                    onCategoryClick = { category ->
                        navController.navigate(Screen.BusinessList.createRoute(category.name))
                    },
                    onAppointmentClick = { /* TODO: Appointment detail */ },
                    onSeeAllBusinesses = {
                        navController.navigate(Screen.BusinessList.createRoute())
                    },
                    onSeeAllAppointments = {
                        // Navegar igual que el bottom bar para consistencia
                        navController.navigate(Screen.Appointments.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            
            composable(Screen.Appointments.route) {
                // Proteger ruta - requiere autenticación
                if (!sessionManager.isLoggedIn()) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.Login.route)
                    }
                } else {
                    AppointmentsScreen(
                        onAppointmentClick = { /* TODO: Appointment detail */ },
                        onNavigateToLogin = {
                            // Limpiar sesión si expiró
                            sessionManager.logout()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
            
            composable(Screen.Profile.route) {
                // Proteger ruta - requiere autenticación
                if (!sessionManager.isLoggedIn()) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.Login.route)
                    }
                } else {
                    ProfileScreen(
                        onLogout = {
                            sessionManager.logout()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
            
            // Business screens
            composable(
                route = "businesses?category={category}",
                arguments = listOf(
                    navArgument("category") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) {
                BusinessListScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onBusinessClick = { businessId ->
                        navController.navigate(Screen.BusinessDetail.createRoute(businessId))
                    }
                )
            }
            
            composable(
                route = Screen.BusinessDetail.route,
                arguments = listOf(
                    navArgument("businessId") { type = NavType.StringType }
                )
            ) {
                BusinessDetailScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onBookAppointment = { businessId, professionalId, serviceId ->
                        // Verificar autenticación antes de permitir booking
                        if (sessionManager.isLoggedIn()) {
                            navController.navigate(
                                Screen.Booking.createRoute(businessId, professionalId, serviceId)
                            )
                        } else {
                            navController.navigate(Screen.Login.route)
                        }
                    }
                )
            }
            
            // Booking screens
            composable(
                route = "booking/{businessId}?professionalId={professionalId}&serviceId={serviceId}",
                arguments = listOf(
                    navArgument("businessId") { type = NavType.StringType },
                    navArgument("professionalId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("serviceId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) {
                // Proteger ruta - requiere autenticación
                if (!sessionManager.isLoggedIn()) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.Login.route)
                    }
                } else {
                    BookingScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onBookingSuccess = {
                            navController.navigate(Screen.Appointments.route) {
                                popUpTo(Screen.Home.route)
                            }
                        }
                    )
                }
            }
            
            composable(Screen.BookingSuccess.route) {
                BookingSuccessScreen(
                    onGoHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onViewAppointments = {
                        navController.navigate(Screen.Appointments.route) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    currentRoute: String?,
    sessionManager: SessionManager
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Surface,
        tonalElevation = 8.dp
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route
            
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    // Verificar autenticación para rutas protegidas
                    if (item.route in listOf(Screen.Appointments.route, Screen.Profile.route)) {
                        if (!sessionManager.isLoggedIn()) {
                            navController.navigate(Screen.Login.route)
                            return@NavigationBarItem
                        }
                    }
                    
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    selectedTextColor = Primary,
                    unselectedIconColor = OnSurfaceVariant,
                    unselectedTextColor = OnSurfaceVariant,
                    indicatorColor = PrimaryContainer
                )
            )
        }
    }
}
