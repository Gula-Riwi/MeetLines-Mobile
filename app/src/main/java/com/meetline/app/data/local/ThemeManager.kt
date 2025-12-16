package com.meetline.app.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestor de preferencias del tema de la aplicación.
 *
 * Maneja la configuración del modo oscuro/claro y persiste la preferencia del usuario.
 */
@Singleton
class ThemeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val _isDarkMode = MutableStateFlow(getDarkMode())
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    /**
     * Obtiene el modo del tema actual.
     *
     * @return true si está en modo oscuro, false si está en modo claro
     */
    fun getDarkMode(): Boolean {
        return prefs.getBoolean(KEY_DARK_MODE, false) // Por defecto: modo claro
    }

    /**
     * Establece el modo del tema.
     *
     * @param isDark true para modo oscuro, false para modo claro
     */
    fun setDarkMode(isDark: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, isDark).apply()
        _isDarkMode.value = isDark
    }

    /**
     * Alterna entre modo oscuro y claro.
     *
     * @return El nuevo estado del modo oscuro
     */
    fun toggleDarkMode(): Boolean {
        val newMode = !getDarkMode()
        setDarkMode(newMode)
        return newMode
    }

    companion object {
        private const val PREFS_NAME = "theme_preferences"
        private const val KEY_DARK_MODE = "dark_mode"
    }
}
