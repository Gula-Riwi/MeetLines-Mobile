package com.meetline.app.data.repository

import com.meetline.app.data.local.MockData
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
 * Actualmente utiliza datos mock de MockData, pero está diseñado
 * para integrarse fácilmente con una API REST en producción.
 */
@Singleton
class DefaultBusinessRepository @Inject constructor() : BusinessRepository {

    /**
     * Obtiene todos los negocios disponibles en la plataforma.
     * 
     * @return Result con la lista completa de negocios
     */
    override suspend fun getAllBusinesses(): Result<List<Business>> {
        delay(800) // Simular latencia de red
        return Result.success(MockData.businesses)
    }

    /**
     * Obtiene un negocio específico por su identificador.
     * 
     * @param id Identificador único del negocio
     * @return Result con el negocio si existe, o un error si no se encuentra
     */
    override suspend fun getBusinessById(id: String): Result<Business> {
        delay(500)
        val business = MockData.getBusinessById(id)
        return if (business != null) {
            Result.success(business)
        } else {
            Result.failure(Exception("Negocio no encontrado"))
        }
    }

    /**
     * Filtra los negocios por categoría.
     * 
     * Permite obtener todos los negocios que pertenecen a una categoría
     * específica (barbería, spa, dentista, etc.).
     * 
     * @param category Categoría por la cual filtrar
     * @return Result con la lista de negocios de esa categoría
     */
    override suspend fun getBusinessesByCategory(category: BusinessCategory): Result<List<Business>> {
        delay(600)
        return Result.success(MockData.getBusinessesByCategory(category))
    }

    /**
     * Busca negocios por texto libre.
     * 
     * Realiza una búsqueda en el nombre, descripción y categoría de los negocios.
     * La búsqueda no distingue entre mayúsculas y minúsculas.
     * 
     * @param query Texto a buscar
     * @return Result con la lista de negocios que coinciden con la búsqueda
     */
    override suspend fun searchBusinesses(query: String): Result<List<Business>> {
        delay(700)
        return Result.success(MockData.searchBusinesses(query))
    }

    /**
     * Obtiene los negocios destacados/populares.
     * 
     * Calcula los negocios más populares basándose en su calificación
     * y número de reseñas, retornando los 5 mejores.
     * 
     * @return Result con la lista de los 5 negocios más destacados
     */
    override suspend fun getFeaturedBusinesses(): Result<List<Business>> {
        delay(600)
        return Result.success(
            MockData.businesses
                .sortedByDescending { it.rating * it.reviewCount }
                .take(5)
        )
    }

    /**
     * Obtiene los negocios cercanos a la ubicación del usuario.
     * 
     * Ordena los negocios por distancia y retorna los 6 más cercanos.
     * En producción, esto utilizaría la ubicación GPS real del usuario.
     * 
     * @return Result con la lista de los 6 negocios más cercanos
     */
    override suspend fun getNearbyBusinesses(): Result<List<Business>> {
        delay(600)
        return Result.success(
            MockData.businesses
                .sortedBy { it.distance.replace(" km", "").toFloatOrNull() ?: Float.MAX_VALUE }
                .take(6)
        )
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
    override suspend fun getAvailableTimeSlots(
        businessId: String,
        professionalId: String,
        date: Long
    ): Result<List<TimeSlot>> {
        delay(500)
        // En producción esto consultaría la disponibilidad real
        return Result.success(MockData.timeSlots)
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
