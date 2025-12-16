package com.meetline.app.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Gestor de ubicación que proporciona acceso a las coordenadas GPS del dispositivo.
 * 
 * Utiliza FusedLocationProviderClient de Google Play Services para obtener
 * la ubicación del usuario de manera eficiente y precisa.
 */
@Singleton
class LocationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Obtiene la ubicación actual del dispositivo.
     * 
     * Requiere que los permisos de ubicación hayan sido otorgados previamente.
     * Si los permisos no están otorgados, retorna un error.
     * 
     * @return Result con la ubicación actual o un error si no se puede obtener
     */
    suspend fun getCurrentLocation(): Result<Location> = suspendCancellableCoroutine { continuation ->
        // Verificar permisos
        if (!hasLocationPermission()) {
            continuation.resume(
                Result.failure(SecurityException("Permisos de ubicación no otorgados"))
            )
            return@suspendCancellableCoroutine
        }

        val cancellationTokenSource = CancellationTokenSource()

        try {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                if (location != null) {
                    continuation.resume(Result.success(location))
                } else {
                    continuation.resume(
                        Result.failure(Exception("No se pudo obtener la ubicación"))
                    )
                }
            }.addOnFailureListener { exception ->
                continuation.resume(Result.failure(exception))
            }
        } catch (e: SecurityException) {
            continuation.resume(Result.failure(e))
        }

        // Cancelar la solicitud si la corrutina es cancelada
        continuation.invokeOnCancellation {
            cancellationTokenSource.cancel()
        }
    }

    /**
     * Verifica si la aplicación tiene permisos de ubicación otorgados.
     * 
     * @return true si tiene permisos de ubicación fina o aproximada, false en caso contrario
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}
