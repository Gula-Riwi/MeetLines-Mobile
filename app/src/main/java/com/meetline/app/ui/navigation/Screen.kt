package com.meetline.app.ui.navigation

/**
 * Rutas de navegación de la aplicación.
 */
sealed class Screen(val route: String) {
    // Auth
    data object Login : Screen("login")
    data object Register : Screen("register")
    
    // Main
    data object Home : Screen("home")
    data object Appointments : Screen("appointments")
    data object Profile : Screen("profile")
    
    // Business
    data object BusinessList : Screen("businesses?category={category}") {
        fun createRoute(category: String? = null): String {
            return if (category != null) {
                "businesses?category=$category"
            } else {
                "businesses"
            }
        }
    }
    
    data object BusinessDetail : Screen("business/{businessId}") {
        fun createRoute(businessId: String): String = "business/$businessId"
    }
    
    // Booking
    data object Booking : Screen("booking/{businessId}?professionalId={professionalId}&serviceId={serviceId}") {
        fun createRoute(
            businessId: String,
            professionalId: String? = null,
            serviceId: String? = null
        ): String {
            val params = mutableListOf<String>()
            if (professionalId != null) params.add("professionalId=$professionalId")
            if (serviceId != null) params.add("serviceId=$serviceId")
            
            return if (params.isNotEmpty()) {
                "booking/$businessId?${params.joinToString("&")}"
            } else {
                "booking/$businessId"
            }
        }
    }
    
    data object BookingSuccess : Screen("booking-success")
}

/**
 * Items del bottom navigation.
 */
// Bottom nav is now handled in AppNavigation.kt with Material Icons
