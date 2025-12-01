package com.meetline.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * MeetLineApplication
 * 
 * Para ti (Spring): Es como tu clase principal con @SpringBootApplication.
 * 
 * @HiltAndroidApp:
 * ---------------
 * Qué hace: Inicializa Hilt (el sistema de inyección de dependencias) para toda la app.
 * Es OBLIGATORIO tener esta anotación en una clase Application si quieres usar Hilt.
 * 
 * Sin esta clase, cuando intentes usar @AndroidEntryPoint en MainActivity, la app crasheará
 * porque Hilt no sabrá dónde está su "raíz" o "contexto global".
 * 
 * Esta clase se ejecuta ANTES que cualquier Activity (pantalla).
 * Es el primer código de tu app que Android ejecuta.
 */
@HiltAndroidApp
class MeetLineApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Aquí puedes inicializar librerías globales (Analytics, Crashlytics, etc.)
        // Por ahora está vacío, pero es el lugar correcto para configuraciones globales.
    }
}
