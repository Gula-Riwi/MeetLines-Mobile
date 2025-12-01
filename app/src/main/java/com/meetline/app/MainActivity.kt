package com.meetline.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.meetline.app.data.local.SessionManager
import com.meetline.app.ui.navigation.AppNavigation
import com.meetline.app.ui.navigation.Screen
import com.meetline.app.ui.theme.MeetLineTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Actividad principal de la aplicación MeetLine.
 * 
 * Esta es la única actividad de la aplicación y sirve como punto de entrada.
 * Utiliza Jetpack Compose para toda la interfaz de usuario y Navigation Component
 * para la navegación entre pantallas.
 * 
 * La anotación @AndroidEntryPoint permite la inyección de dependencias mediante Hilt,
 * lo que facilita el acceso a repositorios y servicios en toda la aplicación.
 * 
 * Responsabilidades:
 * - Configurar el tema de la aplicación
 * - Configurar las barras de sistema (status bar y navigation bar)
 * - Determinar la pantalla inicial basándose en el estado de autenticación
 * - Inicializar el sistema de navegación
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * Gestor de sesión inyectado por Hilt.
     * Utilizado para verificar si el usuario está autenticado y determinar
     * la pantalla inicial de la aplicación.
     */
    @Inject
    lateinit var sessionManager: SessionManager

    /**
     * Método del ciclo de vida llamado cuando se crea la actividad.
     * 
     * Configura:
     * - Edge-to-edge display con barras de sistema transparentes
     * - Determina la pantalla inicial (Login o Home) según el estado de sesión
     * - Inicializa el sistema de navegación de Compose
     * 
     * @param savedInstanceState Estado guardado de la instancia anterior, si existe
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Configurar barras de sistema con colores claros
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.WHITE,
                Color.WHITE
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.WHITE,
                Color.WHITE
            )
        )
        
        // Determinar pantalla inicial ANTES del setContent
        val isLoggedIn = sessionManager.isLoggedIn()
        
        setContent {
            MeetLineTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    
                    val startDestination = remember {
                        if (isLoggedIn) Screen.Home.route else Screen.Login.route
                    }
                    
                    AppNavigation(
                        navController = navController,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}