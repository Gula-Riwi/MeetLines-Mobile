package com.meetline.app.data.remote

import com.meetline.app.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Interfaz que define todos los endpoints de la API de MeetLine.
 *
 * Esta interfaz es utilizada por Retrofit para generar la implementación
 * de las llamadas HTTP al servidor. Cada método representa un endpoint
 * específico de la API.
 *
 * Organización de endpoints:
 * - **Autenticación**: Login, registro, logout y recuperación de contraseña.
 * - **Usuario**: Perfil y actualización de datos.
 * - **Negocios**: Búsqueda, listado y detalles de negocios.
 * - **Proyectos**: Listado público de proyectos/negocios.
 * - **Citas**: Gestión completa de citas (CRUD).
 *
 * Todas las respuestas están envueltas en [ApiResponse] para
 * manejo consistente de éxito/error.
 *
 * @see ApiResponse Estructura de respuesta estándar.
 * @see NetworkModule Configuración de Retrofit.
 */
interface MeetLineApiService {

    // ==================== AUTENTICACIÓN ====================

    /**
     * Inicia sesión con credenciales de usuario.
     *
     * @param request Credenciales de login (email y contraseña).
     * @return Respuesta con datos del usuario y token de acceso.
     */
    @POST("auth/login")
    suspend fun login(
        @Body request: AuthRequest
    ): Response<ApiResponse<AuthResponse>>

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param request Datos de registro (nombre, email, teléfono, contraseña).
     * @return Respuesta con datos del usuario creado y token de acceso.
     */
    @POST("auth/register")
    suspend fun register(
        @Body request: AuthRequest
    ): Response<ApiResponse<AuthResponse>>

    /**
     * Cierra la sesión del usuario actual.
     *
     * Invalida el token de acceso en el servidor.
     *
     * @return Respuesta indicando éxito o error.
     */
    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    /**
     * Solicita recuperación de contraseña.
     *
     * Envía un correo electrónico con instrucciones para restablecer
     * la contraseña del usuario.
     *
     * @param email Mapa con el email del usuario.
     * @return Respuesta indicando si el correo fue enviado.
     */
    @POST("auth/forgot-password")
    suspend fun requestPasswordReset(
        @Body email: Map<String, String>
    ): Response<ApiResponse<Unit>>

    // ==================== USUARIO ====================

    /**
     * Obtiene el perfil del usuario autenticado.
     *
     * @return Datos completos del usuario actual.
     */
    @GET("user/profile")
    suspend fun getCurrentUser(): Response<ApiResponse<UserDto>>

    /**
     * Actualiza el perfil del usuario.
     *
     * @param user Datos actualizados del usuario.
     * @return Usuario con los datos actualizados.
     */
    @PUT("user/profile")
    suspend fun updateProfile(
        @Body user: UserDto
    ): Response<ApiResponse<UserDto>>

    // ==================== NEGOCIOS ====================

    /**
     * Obtiene la lista de negocios destacados.
     *
     * @param limit Número máximo de negocios a retornar.
     * @return Lista de negocios destacados.
     */
    @GET("businesses/featured")
    suspend fun getFeaturedBusinesses(
        @Query("limit") limit: Int = 10
    ): Response<ApiResponse<List<BusinessDto>>>

    /**
     * Obtiene negocios cercanos a la ubicación del usuario.
     *
     * @param latitude Latitud de la ubicación actual.
     * @param longitude Longitud de la ubicación actual.
     * @param radiusKm Radio de búsqueda en kilómetros.
     * @param limit Número máximo de negocios a retornar.
     * @return Lista de negocios ordenados por distancia.
     */
    @GET("businesses/nearby")
    suspend fun getNearbyBusinesses(
        @Query("latitude") latitude: Double? = null,
        @Query("longitude") longitude: Double? = null,
        @Query("radius_km") radiusKm: Int = 10,
        @Query("limit") limit: Int = 10
    ): Response<ApiResponse<List<BusinessDto>>>

    /**
     * Busca negocios por texto libre.
     *
     * Realiza búsqueda en nombre, descripción y categoría.
     *
     * @param query Texto de búsqueda.
     * @param category Filtro opcional por categoría.
     * @param page Número de página para paginación.
     * @param perPage Elementos por página.
     * @return Lista de negocios que coinciden con la búsqueda.
     */
    @GET("businesses/search")
    suspend fun searchBusinesses(
        @Query("query") query: String,
        @Query("category") category: String? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Response<ApiResponse<List<BusinessDto>>>

    /**
     * Obtiene los detalles completos de un negocio.
     *
     * Incluye profesionales, servicios y horarios disponibles.
     *
     * @param businessId ID del negocio.
     * @return Datos completos del negocio.
     */
    @GET("businesses/{id}")
    suspend fun getBusinessDetail(
        @Path("id") businessId: String
    ): Response<ApiResponse<BusinessDto>>

    /**
     * Obtiene negocios por categoría.
     *
     * @param category Nombre de la categoría.
     * @param page Número de página para paginación.
     * @param perPage Elementos por página.
     * @return Lista de negocios de la categoría especificada.
     */
    @GET("businesses")
    suspend fun getBusinessesByCategory(
        @Query("category") category: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Response<ApiResponse<List<BusinessDto>>>

    // ==================== PROYECTOS PÚBLICOS ====================

    /**
     * Obtiene la lista pública de proyectos/negocios.
     *
     * Este endpoint no requiere autenticación y retorna todos los proyectos
     * públicos disponibles en la plataforma.
     * 
     * Opcionalmente, puede filtrar por ubicación si se proporcionan coordenadas.
     *
     * @param latitude Latitud de la ubicación del usuario (opcional).
     * @param longitude Longitud de la ubicación del usuario (opcional).
     * @return Lista de proyectos públicos.
     */
    @GET("api/Projects/public")
    suspend fun getPublicProjects(
        @Query("latitude") latitude: Double? = null,
        @Query("longitude") longitude: Double? = null
    ): Response<List<ProjectDto>>

    /**
     * Obtiene los empleados de un proyecto específico.
     *
     * Este endpoint no requiere autenticación y retorna todos los empleados
     * públicos de un proyecto.
     *
     * @param projectId ID del proyecto.
     * @return Lista de empleados del proyecto.
     */
    @GET("api/Projects/{projectId}/employees/public")
    suspend fun getProjectEmployees(
        @Path("projectId") projectId: String
    ): Response<List<EmployeeDto>>
    
    /**
     * Obtiene los slots disponibles para un proyecto en una fecha específica.
     * 
     * @param url URL completa del endpoint (para usar localhost temporalmente).
     * @param date Fecha en formato YYYY-MM-DD.
     */
    @GET
    suspend fun getAvailableSlots(
        @Url url: String,
        @Query("date") date: String
    ): Response<AvailabilityResponseDto>
    
    /**
     * Obtiene los canales de contacto públicos de un proyecto.
     *
     * Este endpoint retorna los medios de comunicación que el negocio
     * ofrece a sus clientes (WhatsApp, redes sociales, teléfono, etc.).
     *
     * @param projectId ID del proyecto.
     * @return Lista de canales de contacto.
     */
    @GET("api/projects/{projectId}/channels/public")
    suspend fun getProjectContactChannels(
        @Path("projectId") projectId: String
    ): Response<List<ContactChannelDto>>

    // ==================== CITAS ====================

    /**
     * Obtiene todas las citas del usuario.
     *
     * @param status Filtro opcional por estado de la cita.
     * @param page Número de página para paginación.
     * @param perPage Elementos por página.
     * @return Lista de citas del usuario.
     */
    @GET("appointments")
    suspend fun getAppointments(
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Response<ApiResponse<List<AppointmentDto>>>

    /**
     * Obtiene las próximas citas del usuario.
     *
     * Filtra automáticamente las citas futuras ordenadas por fecha.
     *
     * @param limit Número máximo de citas a retornar.
     * @return Lista de próximas citas.
     */
    @GET("appointments/upcoming")
    suspend fun getUpcomingAppointments(
        @Query("limit") limit: Int = 5
    ): Response<ApiResponse<List<AppointmentDto>>>

    /**
     * Crea una nueva cita.
     *
     * @param request Datos de la cita a crear.
     * @return Datos de la cita creada.
     */
    @POST("appointments")
    suspend fun createAppointment(
        @Body request: CreateAppointmentRequest
    ): Response<ApiResponse<AppointmentDto>>

    /**
     * Crea una nueva cita en el backend local.
     *
     * @param url URL del endpoint (para usar localhost/IP temporalmente).
     * @param request Datos de la cita a crear.
     * @return Datos de la cita creada.
     */
    @POST
    suspend fun createAppointmentLocal(
        @Url url: String,
        @Body request: CreateAppointmentRequest
    ): Response<AppointmentCreatedResponseDto>

    /**
     * Cancela una cita existente.
     *
     * @param appointmentId ID de la cita a cancelar.
     * @param reason Mapa con el motivo de cancelación.
     * @return Cita actualizada con estado cancelado.
     */
    @PUT("appointments/{id}/cancel")
    suspend fun cancelAppointment(
        @Path("id") appointmentId: String,
        @Body reason: Map<String, String>?
    ): Response<ApiResponse<AppointmentDto>>

    /**
     * Obtiene horarios disponibles para un negocio y profesional.
     *
     * @param businessId ID del negocio.
     * @param professionalId ID del profesional.
     * @param date Fecha para consultar disponibilidad (YYYY-MM-DD).
     * @param serviceId ID del servicio (para calcular duración).
     * @return Lista de horarios disponibles.
     */
    @GET("businesses/{businessId}/availability")
    suspend fun getAvailableTimeSlots(
        @Path("businessId") businessId: String,
        @Query("professional_id") professionalId: String,
        @Query("date") date: String,
        @Query("service_id") serviceId: String
    ): Response<ApiResponse<List<TimeSlotDto>>>
}

/**
 * DTO para representar un slot de tiempo disponible.
 *
 * @property time Hora del slot (formato HH:mm).
 * @property available Indica si el slot está disponible para reservar.
 */
data class TimeSlotDto(
    @com.google.gson.annotations.SerializedName("time")
    val time: String,
    
    @com.google.gson.annotations.SerializedName("available")
    val available: Boolean
)
