package com.meetline.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
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
 * - Inicializar el sistema de navegación con Home como pantalla inicial
 * 
 * La aplicación es pública: los usuarios pueden navegar sin autenticación.
 * El login solo se requiere para acciones específicas como agendar citas.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * Gestor de sesión inyectado por Hilt.
     * Necesario para verificar autenticación en rutas protegidas.
     */
    @Inject
    lateinit var sessionManager: SessionManager
    
    /**
     * Gestor de tema inyectado por Hilt.
     * Maneja la preferencia del modo oscuro del usuario.
     */
    @Inject
    lateinit var themeManager: com.meetline.app.data.local.ThemeManager

    /**
     * Método del ciclo de vida llamado cuando se crea la actividad.
     * 
     * Configura:
     * - Edge-to-edge display con barras de sistema transparentes
     * - Inicializa el sistema de navegación con Home como pantalla inicial
     * - Pasa SessionManager para control de autenticación en rutas protegidas
     * 
     * @param savedInstanceState Estado guardado de la instancia anterior, si existe
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            val isDarkMode by themeManager.isDarkMode.collectAsState()
            
            // Configurar barras de sistema según el tema
            androidx.compose.runtime.LaunchedEffect(isDarkMode) {
                enableEdgeToEdge(
                    statusBarStyle = if (isDarkMode) {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(Color.WHITE, Color.WHITE)
                    },
                    navigationBarStyle = if (isDarkMode) {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(Color.WHITE, Color.WHITE)
                    }
                )
            }
            
            MeetLineTheme(darkTheme = isDarkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    
                    // Siempre iniciar en Home - la app es pública
                    AppNavigation(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        sessionManager = sessionManager,
                        isDarkMode = isDarkMode,
                        onThemeToggle = { themeManager.toggleDarkMode() }
                    )
                }
            }
        }
    }
}