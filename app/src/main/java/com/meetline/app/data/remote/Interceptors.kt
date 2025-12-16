package com.meetline.app.data.remote

import com.meetline.app.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor de OkHttp para añadir el token de autenticación a las peticiones.
 *
 * Este interceptor añade automáticamente el header `Authorization` con el token
 * Bearer a todas las peticiones HTTP, excepto a los endpoints de autenticación
 * que no requieren token.
 *
 * El token se obtiene del [SessionManager] donde se almacena después del login.
 *
 * Endpoints excluidos de autenticación:
 * - `/auth/login`
 * - `/auth/register`
 * - `/auth/forgot-password`
 *
 * @property sessionManager Gestor de sesión que proporciona el token.
 *
 * @see SessionManager Para almacenamiento del token.
 * @see NetworkModule Para configuración del cliente HTTP.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    companion object {
        /** Nombre del header de autorización HTTP. */
        private const val HEADER_AUTHORIZATION = "Authorization"
        
        /** Prefijo del token Bearer. */
        private const val TOKEN_PREFIX = "Bearer "
        
        /** Endpoints que no requieren autenticación. */
        private val UNAUTHENTICATED_ENDPOINTS = listOf(
            "auth/login",
            "auth/register",
            "auth/forgot-password",
            "available-slots",  // Endpoint público de disponibilidad
            "working-hours",    // Endpoint público de horarios
            "Projects/public",  // Proyectos públicos
            "/projects/public", // Endpoints públicos de proyectos (específico)
            "employees/public", // Empleados públicos
            "services/public",  // Servicios públicos
            "channels/public"   // Canales de contacto públicos
        )
    }

    /**
     * Intercepta la petición HTTP y añade el token de autenticación si es necesario.
     *
     * @param chain Cadena de interceptores de OkHttp.
     * @return Respuesta HTTP después de procesar la petición.
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestPath = originalRequest.url.encodedPath
        
        // No añadir token a endpoints de autenticación ni endpoints públicos
        val requiresAuth = UNAUTHENTICATED_ENDPOINTS.none { requestPath.contains(it) }
        
        val request = if (requiresAuth) {
            val token = sessionManager.getAuthToken()
            if (!token.isNullOrBlank()) {

                originalRequest.newBuilder()
                    .header(HEADER_AUTHORIZATION, "$TOKEN_PREFIX$token")
                    .build()
            } else {

                originalRequest
            }
        } else {

            originalRequest
        }
        
        val response = chain.proceed(request)
        
        // Si recibimos 401 Unauthorized, cerrar la sesión automáticamente
        if (response.code == 401 && requiresAuth) {
            sessionManager.logout()
        }
        
        return response
    }
}

/**
 * Interceptor para añadir headers comunes a todas las peticiones.
 *
 * Añade headers estándar como:
 * - `Content-Type`: application/json
 * - `Accept`: application/json
 * - `X-Platform`: Android
 * - `X-App-Version`: Versión de la aplicación
 *
 * Estos headers ayudan al servidor a identificar el cliente y
 * procesar correctamente las peticiones.
 */
class CommonHeadersInterceptor : Interceptor {

    /**
     * Intercepta la petición y añade los headers comunes.
     *
     * @param chain Cadena de interceptores de OkHttp.
     * @return Respuesta HTTP después de procesar la petición.
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .header(HEADER_ACCEPT, CONTENT_TYPE_JSON)
            .header(HEADER_PLATFORM, "Android")
            .header(HEADER_APP_VERSION, APP_VERSION)
        
        // Solo añadir Content-Type para métodos que envían body
        // GET no debe tener Content-Type para evitar CORS preflight
        if (originalRequest.method in listOf("POST", "PUT", "PATCH", "DELETE")) {
            requestBuilder.header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
        }
        
        return chain.proceed(requestBuilder.build())
    }
    
    companion object {
        private const val HEADER_CONTENT_TYPE = "Content-Type"
        private const val HEADER_ACCEPT = "Accept"
        private const val HEADER_PLATFORM = "X-Platform"
        private const val HEADER_APP_VERSION = "X-App-Version"
        private const val CONTENT_TYPE_JSON = "application/json"
        /** Versión de la app - actualizar con cada release */
        private const val APP_VERSION = "1.0.0"
    }
}
