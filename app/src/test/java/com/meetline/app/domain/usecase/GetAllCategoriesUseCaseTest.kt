package com.meetline.app.domain.usecase

import com.meetline.app.domain.model.BusinessCategory
import com.meetline.app.domain.repository.BusinessRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests unitarios para [GetAllCategoriesUseCase].
 *
 * Verifica que el caso de uso devuelve correctamente todas las categorías
 * disponibles desde el repositorio.
 *
 * @see GetAllCategoriesUseCase Clase bajo prueba.
 */
class GetAllCategoriesUseCaseTest {

    private lateinit var businessRepository: BusinessRepository
    private lateinit var getAllCategoriesUseCase: GetAllCategoriesUseCase

    @Before
    fun setUp() {
        businessRepository = mockk()
        getAllCategoriesUseCase = GetAllCategoriesUseCase(businessRepository)
    }

    /**
     * Verifica que devuelve todas las categorías del repositorio.
     */
    @Test
    fun `invoke returns all categories from repository`() {
        // Given
        val expectedCategories = listOf(
            BusinessCategory.BARBERSHOP,
            BusinessCategory.BEAUTY_SALON,
            BusinessCategory.SPA,
            BusinessCategory.DENTIST,
            BusinessCategory.LAWYER
        )
        
        every { businessRepository.getAllCategories() } returns expectedCategories

        // When
        val result = getAllCategoriesUseCase()

        // Then
        assertEquals(expectedCategories, result)
        verify { businessRepository.getAllCategories() }
    }

    /**
     * Verifica que devuelve lista vacía si el repositorio no tiene categorías.
     */
    @Test
    fun `invoke returns empty list when repository has no categories`() {
        // Given
        every { businessRepository.getAllCategories() } returns emptyList()

        // When
        val result = getAllCategoriesUseCase()

        // Then
        assertTrue(result.isEmpty())
    }

    /**
     * Verifica que devuelve las categorías en el orden del repositorio.
     */
    @Test
    fun `invoke maintains category order from repository`() {
        // Given
        val orderedCategories = listOf(
            BusinessCategory.SPA,
            BusinessCategory.BARBERSHOP,
            BusinessCategory.BEAUTY_SALON
        )
        
        every { businessRepository.getAllCategories() } returns orderedCategories

        // When
        val result = getAllCategoriesUseCase()

        // Then
        assertEquals(orderedCategories, result)
        assertEquals(BusinessCategory.SPA, result[0])
        assertEquals(BusinessCategory.BARBERSHOP, result[1])
        assertEquals(BusinessCategory.BEAUTY_SALON, result[2])
    }
}
