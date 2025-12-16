package com.meetline.app.di

import android.content.Context
import com.meetline.app.data.local.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Hilt para proveer dependencias en toda la aplicación.
 * 
 * Este objeto define cómo se crean e inyectan las dependencias principales
 * de la aplicación utilizando Dagger Hilt.
 * 
 * @Module indica que esta clase proporciona dependencias
 * @InstallIn(SingletonComponent::class) especifica que estas dependencias
 * estarán disponibles durante todo el ciclo de vida de la aplicación
 * 
 * Todas las funciones marcadas con @Provides y @Singleton crean instancias
 * únicas que se reutilizan en toda la aplicación (patrón Singleton).
 * 
 * Esto es equivalente a la configuración de beans en Spring Boot, pero
 * para Android con Hilt.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provee una instancia única de SessionManager.
     * 
     * SessionManager gestiona la sesión del usuario y requiere el contexto
     * de la aplicación para acceder a SharedPreferences.
     * 
     * @param context Contexto de la aplicación inyectado automáticamente por Hilt
     * @return Instancia singleton de SessionManager
     */
    @Provides
    @Singleton
    fun provideSessionManager(
        @ApplicationContext context: Context
    ): SessionManager = SessionManager(context)

    /**
     * Provee una instancia única de LocationManager.
     * 
     * LocationManager gestiona la obtención de coordenadas GPS del dispositivo.
     * 
     * @param context Contexto de la aplicación inyectado automáticamente por Hilt
     * @return Instancia singleton de LocationManager
     */
    @Provides
    @Singleton
    fun provideLocationManager(
        @ApplicationContext context: Context
    ): com.meetline.app.data.location.LocationManager = 
        com.meetline.app.data.location.LocationManager(context)

    // Repositories are now provided by RepositoryModule via @Binds
}
