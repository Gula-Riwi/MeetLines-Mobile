package com.meetline.app.di

import com.meetline.app.data.local.SessionManager
import com.meetline.app.data.remote.AuthInterceptor
import com.meetline.app.data.remote.CommonHeadersInterceptor
import com.meetline.app.data.remote.MeetLineApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Módulo de Hilt para la configuración de la capa de red.
 *
 * Este módulo proporciona las dependencias necesarias para las comunicaciones
 * HTTP con el backend de MeetLine, incluyendo:
 *
 * - **OkHttpClient**: Cliente HTTP configurado con interceptores y timeouts.
 * - **Retrofit**: Cliente REST para consumo de la API.
 * - **MeetLineApiService**: Interfaz tipada para los endpoints.
 *
 * Características de seguridad:
 * - Autenticación automática mediante [AuthInterceptor].
 * - Logging de peticiones solo en modo debug.
 * - Timeouts configurados para evitar bloqueos.
 *
 * @see AuthInterceptor Interceptor para añadir tokens de autenticación.
 * @see CommonHeadersInterceptor Interceptor para headers comunes.
 * @see MeetLineApiService Definición de endpoints de la API.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /** URL base de la API de MeetLines. */
    private const val BASE_URL = "https://services.meet-lines.com/"
    
    /** URL base para el servicio de citas (Spring Boot local). */
    private const val APPOINTMENTS_BASE_URL = "https://app.meet-lines.com/"
    
    /** Timeout de conexión en segundos. */
    private const val CONNECT_TIMEOUT = 30L
    
    /** Timeout de lectura en segundos. */
    private const val READ_TIMEOUT = 30L
    
    /** Timeout de escritura en segundos. */
    private const val WRITE_TIMEOUT = 30L

    /**
     * Provee el interceptor de logging para peticiones HTTP.
     *
     * Solo registra el cuerpo completo de las peticiones en modo debug
     * para evitar exponer información sensible en producción.
     *
     * @return Instancia configurada de [HttpLoggingInterceptor].
     */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            // Usar isDebuggable() para determinar si estamos en debug
            level = if (isDebuggable()) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }
    
    /**
     * Determina si la app está en modo debug.
     * Alternativa a BuildConfig.DEBUG que funciona sin necesidad de sync.
     */
    private fun isDebuggable(): Boolean {
        return try {
            val clazz = Class.forName("com.meetline.app.BuildConfig")
            val field = clazz.getField("DEBUG")
            field.getBoolean(null)
        } catch (e: Exception) {
            // Si no se puede determinar, asumimos release (más seguro)
            false
        }
    }

    /**
     * Provee el interceptor de autenticación.
     *
     * @param sessionManager Gestor de sesión para obtener el token.
     * @return Instancia de [AuthInterceptor].
     */
    @Provides
    @Singleton
    fun provideAuthInterceptor(
        sessionManager: SessionManager
    ): AuthInterceptor {
        return AuthInterceptor(sessionManager)
    }

    /**
     * Provee el cliente HTTP OkHttp configurado.
     *
     * Configuración incluida:
     * - Interceptor de autenticación para añadir tokens.
     * - Interceptor de headers comunes.
     * - Interceptor de logging (solo en debug).
     * - Timeouts de conexión, lectura y escritura.
     *
     * @param loggingInterceptor Interceptor de logging.
     * @param authInterceptor Interceptor de autenticación.
     * @return Instancia configurada de [OkHttpClient].
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(CommonHeadersInterceptor())
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Provee la instancia de Retrofit configurada.
     *
     * Configuración:
     * - URL base de la API.
     * - Conversor Gson para serialización JSON.
     * - Cliente OkHttp personalizado.
     *
     * @param okHttpClient Cliente HTTP configurado.
     * @return Instancia de [Retrofit].
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Provee la implementación del servicio de API.
     *
     * Retrofit genera automáticamente la implementación de la interfaz
     * basándose en las anotaciones de los métodos.
     *
     * @param retrofit Instancia de Retrofit configurada.
     * @return Implementación de [MeetLineApiService].
     */
    @Provides
    @Singleton
    fun provideMeetLineApiService(
        retrofit: Retrofit
    ): MeetLineApiService {
        return retrofit.create(MeetLineApiService::class.java)
    }

    /**
     * Provee la URL base del servicio de citas.
     * 
     * @return URL string para el servicio de appointments.
     */
    @Provides
    @Singleton
    @javax.inject.Named("AppointmentsUrl")
    fun provideAppointmentsUrl(): String {
        return BASE_URL
    }
}
