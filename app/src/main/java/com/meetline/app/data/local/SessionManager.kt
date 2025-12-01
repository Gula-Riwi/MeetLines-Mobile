package com.meetline.app.data.local

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.meetline.app.domain.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestor de sesión seguro para persistencia de datos del usuario.
 *
 * Esta clase maneja toda la información de sesión del usuario utilizando
 * [EncryptedSharedPreferences] para almacenamiento local seguro y encriptado.
 * Los datos sensibles como tokens de autenticación están protegidos
 * mediante encriptación AES256.
 *
 * Es un Singleton inyectado por Hilt, lo que garantiza una única instancia
 * en toda la aplicación.
 *
 * ## Características de Seguridad
 * - Encriptación AES256-GCM para valores.
 * - Encriptación AES256-SIV para claves.
 * - Fallback a SharedPreferences normal si la encriptación falla.
 * - Almacenamiento seguro del token de autenticación.
 *
 * ## Responsabilidades
 * - Guardar y recuperar datos de sesión del usuario.
 * - Gestionar el estado de autenticación.
 * - Almacenar tokens de autenticación de forma segura.
 * - Controlar el estado del onboarding.
 * - Proporcionar métodos para cerrar sesión y limpiar datos.
 *
 * @property context Contexto de la aplicación inyectado por Hilt.
 *
 * @see EncryptedSharedPreferences Para detalles de la encriptación.
 * @see MasterKey Clave maestra para la encriptación.
 */
@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    /**
     * Instancia de SharedPreferences encriptado para almacenar datos de sesión.
     * Utiliza encriptación AES256 para proteger datos sensibles.
     * En caso de error, hace fallback a SharedPreferences normal.
     */
    private val prefs: SharedPreferences = createEncryptedPrefs()
    
    /**
     * Crea una instancia de EncryptedSharedPreferences.
     *
     * Utiliza una clave maestra almacenada en el Android Keystore
     * para encriptar los datos. Si la creación falla (ej: dispositivos
     * muy antiguos), hace fallback a SharedPreferences normal.
     *
     * @return Instancia de SharedPreferences (encriptado o normal).
     */
    private fun createEncryptedPrefs(): SharedPreferences {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            
            EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            // Fallback a SharedPreferences normal si la encriptación falla
            Log.w(TAG, "Error creando EncryptedSharedPreferences, usando fallback", e)
            context.getSharedPreferences(PREFS_NAME_FALLBACK, Context.MODE_PRIVATE)
        }
    }

    companion object {
        /** Tag para logging. */
        private const val TAG = "SessionManager"
        
        /** Nombre del archivo de SharedPreferences encriptado. */
        private const val PREFS_NAME = "meetline_secure_session"
        
        /** Nombre del archivo de fallback no encriptado. */
        private const val PREFS_NAME_FALLBACK = "meetline_session_fallback"
        
        /** Clave para el estado de autenticación. */
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        
        /** Clave para el ID del usuario. */
        private const val KEY_USER_ID = "user_id"
        
        /** Clave para el nombre del usuario. */
        private const val KEY_USER_NAME = "user_name"
        
        /** Clave para el email del usuario. */
        private const val KEY_USER_EMAIL = "user_email"
        
        /** Clave para el teléfono del usuario. */
        private const val KEY_USER_PHONE = "user_phone"
        
        /** Clave para la URL del avatar del usuario. */
        private const val KEY_USER_AVATAR = "user_avatar"
        
        /** Clave para el token de autenticación. */
        private const val KEY_AUTH_TOKEN = "auth_token"
        
        /** Clave para el refresh token. */
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        
        /** Clave para la expiración del token. */
        private const val KEY_TOKEN_EXPIRES_AT = "token_expires_at"
        
        /** Clave para el estado del onboarding. */
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }

    /**
     * Guarda la sesión del usuario después de un login o registro exitoso.
     *
     * Almacena todos los datos del usuario y marca la sesión como activa.
     * El token se almacena de forma encriptada para mayor seguridad.
     *
     * @param user Objeto [User] con los datos del usuario.
     * @param token Token de autenticación JWT del backend.
     * @param refreshToken Token para renovar la sesión (opcional).
     * @param expiresAt Timestamp de expiración del token (opcional).
     */
    fun saveSession(
        user: User, 
        token: String = "",
        refreshToken: String? = null,
        expiresAt: Long? = null
    ) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USER_ID, user.id)
            putString(KEY_USER_NAME, user.name)
            putString(KEY_USER_EMAIL, user.email)
            putString(KEY_USER_PHONE, user.phone)
            putString(KEY_USER_AVATAR, user.avatarUrl)
            putString(KEY_AUTH_TOKEN, token)
            refreshToken?.let { putString(KEY_REFRESH_TOKEN, it) }
            expiresAt?.let { putLong(KEY_TOKEN_EXPIRES_AT, it) }
            apply()
        }
    }

    /**
     * Verifica si el usuario tiene una sesión activa.
     *
     * @return `true` si hay una sesión activa, `false` en caso contrario.
     */
    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    /**
     * Obtiene el usuario actual de la sesión.
     *
     * Reconstruye el objeto [User] a partir de los datos almacenados
     * en SharedPreferences encriptado.
     *
     * @return Objeto [User] si hay sesión activa, `null` en caso contrario.
     */
    fun getCurrentUser(): User? {
        if (!isLoggedIn()) return null
        
        return User(
            id = prefs.getString(KEY_USER_ID, "") ?: "",
            name = prefs.getString(KEY_USER_NAME, "") ?: "",
            email = prefs.getString(KEY_USER_EMAIL, "") ?: "",
            phone = prefs.getString(KEY_USER_PHONE, "") ?: "",
            avatarUrl = prefs.getString(KEY_USER_AVATAR, null)
        )
    }

    /**
     * Actualiza los datos del usuario en la sesión.
     *
     * Permite modificar la información del perfil sin cerrar la sesión.
     * No modifica el ID del usuario ni el estado de autenticación.
     *
     * @param user Objeto [User] con los datos actualizados.
     */
    fun updateUser(user: User) {
        prefs.edit().apply {
            putString(KEY_USER_NAME, user.name)
            putString(KEY_USER_EMAIL, user.email)
            putString(KEY_USER_PHONE, user.phone)
            putString(KEY_USER_AVATAR, user.avatarUrl)
            apply()
        }
    }

    /**
     * Obtiene el token de autenticación almacenado.
     *
     * El token está almacenado de forma encriptada y se desencripta
     * automáticamente al leerlo.
     *
     * @return Token de autenticación si existe, `null` en caso contrario.
     */
    fun getAuthToken(): String? = prefs.getString(KEY_AUTH_TOKEN, null)
    
    /**
     * Obtiene el refresh token para renovar la sesión.
     *
     * @return Refresh token si existe, `null` en caso contrario.
     */
    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)
    
    /**
     * Verifica si el token de autenticación ha expirado.
     *
     * @return `true` si el token ha expirado, `false` si aún es válido.
     */
    fun isTokenExpired(): Boolean {
        val expiresAt = prefs.getLong(KEY_TOKEN_EXPIRES_AT, 0L)
        return expiresAt > 0 && System.currentTimeMillis() >= expiresAt
    }
    
    /**
     * Actualiza el token de autenticación.
     *
     * Útil cuando se renueva el token usando el refresh token.
     *
     * @param newToken Nuevo token de autenticación.
     * @param expiresAt Nueva fecha de expiración (opcional).
     */
    fun updateAuthToken(newToken: String, expiresAt: Long? = null) {
        prefs.edit().apply {
            putString(KEY_AUTH_TOKEN, newToken)
            expiresAt?.let { putLong(KEY_TOKEN_EXPIRES_AT, it) }
            apply()
        }
    }

    /**
     * Marca el onboarding como completado.
     *
     * Utilizado para no volver a mostrar la pantalla de introducción
     * en futuros inicios de la aplicación.
     */
    fun setOnboardingCompleted() {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, true).apply()
    }

    /**
     * Verifica si el onboarding fue completado.
     *
     * @return `true` si el usuario ya completó el onboarding, `false` en caso contrario.
     */
    fun isOnboardingCompleted(): Boolean = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)

    /**
     * Cierra la sesión del usuario y limpia los datos de autenticación.
     *
     * Elimina todos los datos del usuario y tokens, pero mantiene
     * otras preferencias como el estado del onboarding.
     */
    fun logout() {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, false)
            remove(KEY_USER_ID)
            remove(KEY_USER_NAME)
            remove(KEY_USER_EMAIL)
            remove(KEY_USER_PHONE)
            remove(KEY_USER_AVATAR)
            remove(KEY_AUTH_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_TOKEN_EXPIRES_AT)
            apply()
        }
    }

    /**
     * Limpia todos los datos almacenados en SharedPreferences.
     *
     * Elimina absolutamente toda la información, incluyendo preferencias
     * y configuraciones. **Usar con precaución**.
     */
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
