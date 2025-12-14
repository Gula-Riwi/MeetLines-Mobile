package com.meetline.app.domain.usecase

import com.meetline.app.domain.model.Appointment
import com.meetline.app.domain.repository.AppointmentRepository
import javax.inject.Inject

/**
 * Caso de uso para obtener el historial completo de citas del usuario autenticado.
 *
 * Este caso de uso encapsula la lógica para obtener todas las citas del usuario,
 * independientemente de su estado (pendientes, completadas, canceladas).
 * Requiere autenticación JWT válida.
 *
 * El token se maneja automáticamente por el AuthInterceptor, por lo que este
 * caso de uso solo necesita invocar el repositorio.
 *
 * ## Flujo de ejecución:
 * 1. Invoca el repositorio para obtener historial desde la API
 * 2. El repositorio agrega automáticamente el token JWT en el header
 * 3. La API valida el token y extrae el userId
 * 4. Devuelve todas las citas del usuario (todos los estados)
 *
 * ## Manejo de errores:
 * - 401 Unauthorized: Token expirado o inválido
 * - 403 Forbidden: Token válido pero sin permisos
 * - Error de red: Sin conexión o timeout
 *
 * ## Casos de uso comunes:
 * - Pantalla de "Historial de citas"
 * - Pantalla de "Mis citas" con todas las categorías
 * - Estadísticas del usuario (cuántas citas ha tenido)
 *
 * @property repository Repositorio de citas que maneja la comunicación con la API.
 *
 * @see AppointmentRepository Para la implementación del repositorio.
 * @see GetMyActiveAppointmentsUseCase Para obtener solo las citas activas.
 */
class GetMyAppointmentHistoryUseCase @Inject constructor(
    private val repository: AppointmentRepository
) {
    /**
     * Obtiene el historial completo de citas del usuario autenticado.
     *
     * @return Result con lista completa de citas o error.
     */
    suspend operator fun invoke(): Result<List<Appointment>> {
        return repository.getMyAppointmentHistory()
    }
}
