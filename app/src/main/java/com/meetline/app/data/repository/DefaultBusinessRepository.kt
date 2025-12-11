package com.meetline.app.data.repository


import com.meetline.app.data.model.toDomain
import com.meetline.app.data.model.toProfessional
import com.meetline.app.data.remote.MeetLineApiService
import com.meetline.app.domain.model.Business
import com.meetline.app.domain.model.BusinessCategory
import com.meetline.app.domain.model.TimeSlot
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

import com.meetline.app.domain.repository.BusinessRepository

/**
 * Repositorio para operaciones relacionadas con negocios.
 * 
 * Gestiona todas las operaciones de consulta y búsqueda de negocios,
 * incluyendo:
 * - Obtención de negocios por ID o categoría
 * - Búsqueda de negocios por texto
 * - Listados especiales (destacados, cercanos)
 * - Consulta de horarios disponibles
 * - Gestión de categorías
 * 
 * Utiliza la API real para obtener proyectos públicos y datos mock
 * para funcionalidades que aún no tienen endpoints disponibles.
 */
@Singleton
class DefaultBusinessRepository @Inject constructor(
    private val apiService: MeetLineApiService
) : BusinessRepository {

    /**
     * Obtiene todos los negocios disponibles en la plataforma.
     * 
     * Consume el endpoint público de proyectos para obtener datos reales.
     * En caso de error, retorna una lista vacía.
     * 
     * @return Result con la lista completa de negocios desde la API
     */
    override suspend fun getAllBusinesses(): Result<List<Business>> {
        return try {
            val response = apiService.getPublicProjects()
            if (response.isSuccessful && response.body() != null) {
                val projects = response.body()!!
                val businesses = projects.map { it.toDomain() }
                Result.success(businesses)
            } else {
                Result.failure(Exception("Error al obtener proyectos: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}", e))
        }
    }

    /**
     * Obtiene un negocio específico por su identificador.
     * 
     * Además de los datos básicos del negocio, obtiene:
     * - Los empleados del proyecto
     * - Los slots disponibles del día actual para calcular el horario de trabajo
     * 
     * @param id Identificador único del negocio
     * @return Result con el negocio si existe, o un error si no se encuentra
     */
    override suspend fun getBusinessById(id: String): Result<Business> {
        return try {
            // Intentar obtener de la API primero
            val response = apiService.getPublicProjects()
            if (response.isSuccessful && response.body() != null) {
                val project = response.body()!!.find { it.id == id }
                
                if (project != null) {
                    var openingHours: String? = null
                    var contactChannels: List<com.meetline.app.domain.model.ContactChannel> = emptyList()
                    
                    // Intentar obtener slots disponibles para calcular horario
                    try {
                        val today = java.time.LocalDate.now()
                        val dateStr = today.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)
                        val url = "http://ipprovisional:8080/api/v1/appointments/projects/$id/available-slots"
                        
                        val slotsResponse = apiService.getAvailableSlots(url, dateStr)
                        if (slotsResponse.isSuccessful && slotsResponse.body() != null) {
                            val availability = slotsResponse.body()!!
                            openingHours = com.meetline.app.data.model.calculateWorkingHours(availability.availableSlots)
                        }
                    } catch (e: Exception) {
                        // Si falla obtener slots, continuar sin horario
                        // El negocio mostrará "Horario no disponible"
                    }
                    
                    // Intentar obtener canales de contacto
                    try {
                        val channelsResponse = apiService.getProjectContactChannels(id)
                        if (channelsResponse.isSuccessful && channelsResponse.body() != null) {
                            contactChannels = channelsResponse.body()!!.map { it.toDomain() }
                        }
                    } catch (e: Exception) {
                        // Si falla obtener canales, continuar sin ellos
                    }
                    
                    // Convertir proyecto a Business con el horario y canales calculados
                    var business = project.toDomain(openingHours, contactChannels)
                    
                    // Intentar obtener empleados del proyecto
                    try {
                        val employeesResponse = apiService.getProjectEmployees(id)
                        if (employeesResponse.isSuccessful && employeesResponse.body() != null) {
                            val employees = employeesResponse.body()!!
                            val professionals = employees.map { it.toProfessional() }
                            // Actualizar el negocio con los empleados
                            business = business.copy(professionals = professionals)
                        }
                    } catch (e: Exception) {
                        // Si falla obtener empleados, continuar sin ellos
                        // El negocio ya tiene una lista vacía por defecto
                    }
                    
                    return Result.success(business)
                }
            }
            
            Result.failure(Exception("Negocio no encontrado"))
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener negocio: ${e.message}", e))
        }
    }

    /**
     * Filtra los negocios por categoría.
     * 
     * @param category Categoría por la cual filtrar
     * @return Result con la lista de negocios de esa categoría
     */
    override suspend fun getBusinessesByCategory(category: BusinessCategory): Result<List<Business>> {
         return try {
            val response = apiService.getPublicProjects()
            if (response.isSuccessful && response.body() != null) {
                val projects = response.body()!!
                val businesses = projects
                    .map { it.toDomain() }
                    .filter { it.category == category }
                Result.success(businesses)
            } else {
                Result.failure(Exception("Error al filtrar negocios: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}", e))
        }
    }

    /**
     * Busca negocios por texto libre.
     * 
     * @param query Texto a buscar
     * @return Result con la lista de negocios que coinciden con la búsqueda
     */
    override suspend fun searchBusinesses(query: String): Result<List<Business>> {
        return try {
            val response = apiService.getPublicProjects()
            if (response.isSuccessful && response.body() != null) {
                val projects = response.body()!!
                val businesses = projects
                    .map { it.toDomain() }
                    .filter { 
                        it.name.contains(query, ignoreCase = true) || 
                        it.description.contains(query, ignoreCase = true)
                    }
                Result.success(businesses)
            } else {
                Result.failure(Exception("Error al buscar negocios: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}", e))
        }
    }

    /**
     * Obtiene los negocios destacados/populares.
     * 
     * @return Result con la lista de negocios destacados
     */
    override suspend fun getFeaturedBusinesses(): Result<List<Business>> {
        return try {
            val response = apiService.getPublicProjects()
            if (response.isSuccessful && response.body() != null) {
                val businesses = response.body()!!.map { it.toDomain() }
                // Por ahora retornamos los primeros 5 ya que no hay lógica de "destacados" en API
                Result.success(businesses.take(5))
            } else {
                 Result.failure(Exception("Error al obtener destacados: ${response.code()}"))
            }
        } catch (e: Exception) {
             Result.failure(Exception("Error de red: ${e.message}", e))
        }
    }

    /**
     * Obtiene los negocios cercanos a la ubicación del usuario.
     * 
     * @return Result con la lista de negocios cercanos
     */
    override suspend fun getNearbyBusinesses(): Result<List<Business>> {
        return try {
            val response = apiService.getPublicProjects()
            if (response.isSuccessful && response.body() != null) {
                val businesses = response.body()!!.map { it.toDomain() }
                // Por ahora retornamos los primeros 6 ya que no hay geolocalización real
                Result.success(businesses.take(6))
            } else {
                Result.failure(Exception("Error al obtener cercanos: ${response.code()}"))
            }
        } catch (e: Exception) {
             Result.failure(Exception("Error de red: ${e.message}", e))
        }
    }

    /**
     * Obtiene los horarios disponibles para reservar con un profesional.
     * 
     * Consulta los slots de tiempo disponibles para una fecha específica.
     * En producción, esto verificaría la disponibilidad real del profesional
     * consultando las citas ya agendadas.

     * 
     * @param businessId ID del negocio
     * @param professionalId ID del profesional
     * @param date Fecha para la cual consultar disponibilidad (timestamp)
     * @return Result con la lista de horarios disponibles
     */
    /**
     * Obtiene los horarios disponibles para reservar con un profesional.
     * 
     * Consulta los slots de tiempo disponibles para una fecha específica desde la API.
     * Utiliza temporalmente localhost (10.0.2.2) para el endpoint de disponibilidad.
     * 
     * @param businessId ID del negocio
     * @param professionalId ID del profesional
     * @param date Fecha para la cual consultar disponibilidad (timestamp)
     * @return Result con la lista de horarios disponibles
     */
    override suspend fun getAvailableTimeSlots(
        businessId: String,
        professionalId: String,
        date: Long
    ): Result<List<TimeSlot>> {
        return try {
            // Formatear fecha a YYYY-MM-DD
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val dateStr = sdf.format(java.util.Date(date))
            
            // Construir URL para localhost
            val url = "http://ipprovisional:8080/api/v1/appointments/projects/$businessId/available-slots"
            
            val response = apiService.getAvailableSlots(url, dateStr)
            
            if (response.isSuccessful && response.body() != null) {
                val availability = response.body()!!
                
                // Mapear slots de la API a TimeSlot del dominio
                val timeSlots = availability.availableSlots.map { slot ->
                    // Extraer solo la hora del startTime (ej: "2025-12-15T09:00:00-05:00" -> "09:00")
                    val time = try {
                        val slotDate = java.time.ZonedDateTime.parse(slot.startTime)
                        java.time.format.DateTimeFormatter.ofPattern("HH:mm").format(slotDate)
                    } catch (e: Exception) {
                        // Fallback simple si falla el parseo complejo
                        slot.startTime.substringAfter("T").substring(0, 5)
                    }
                    
                    TimeSlot(time, true)
                }
                
                Result.success(timeSlots)
            } else {
                // Si falla o no hay slots, devolver lista vacía o error
                Result.failure(Exception("Error al obtener slots: ${response.code()}"))
            }
        } catch (e: Exception) {
             Result.failure(Exception("Error de red al obtener slots: ${e.message}", e))
        }
    }

    /**
     * Obtiene todas las categorías de negocios disponibles.
     * 
     * Retorna la lista completa de categorías que los negocios pueden tener
     * (barbería, spa, dentista, abogado, etc.).
     * 
     * @return Lista de todas las categorías disponibles
     */
    override fun getAllCategories(): List<BusinessCategory> = BusinessCategory.entries
}
