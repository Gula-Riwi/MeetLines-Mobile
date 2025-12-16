package com.meetline.app.domain.usecase

import com.meetline.app.domain.model.Appointment
import com.meetline.app.domain.repository.AppointmentRepository
import javax.inject.Inject

/**
 * Caso de uso para obtener las citas activas (pendientes) del usuario autenticado.
 *
 * Este caso de uso encapsula la lógica para obtener las citas que están en estado
 * PENDING del usuario que ha iniciado sesión. Requiere autenticación JWT válida.
 *
 * El token se maneja automáticamente por el AuthInterceptor, por lo que este
 * caso de uso solo necesita invocar el repositorio.
 *
 * ## Flujo de ejecución:
 * 1. Invoca el repositorio para obtener citas activas desde la API
 * 2. El repositorio agrega automáticamente el token JWT en el header
 * 3. La API valida el token y extrae el userId
 * 4. Devuelve solo las citas con estado PENDING del usuario
 *
 * ## Manejo de errores:
 * - 401 Unauthorized: Token expirado o inválido
 * - 403 Forbidden: Token válido pero sin permisos
 * - Error de red: Sin conexión o timeout
 *
 * @property repository Repositorio de citas que maneja la comunicación con la API.
 *
 * @see AppointmentRepository Para la implementación del repositorio.
 * @see GetMyAppointmentHistoryUseCase Para obtener el historial completo.
 */
class GetMyActiveAppointmentsUseCase @Inject constructor(
    private val repository: AppointmentRepository
) {
    /**
     * Obtiene las citas activas del usuario autenticado.
     *
     * @return Result con lista de citas pendientes o error.
     */
    suspend operator fun invoke(): Result<List<Appointment>> {
        return repository.getMyActiveAppointments()
    }
}
