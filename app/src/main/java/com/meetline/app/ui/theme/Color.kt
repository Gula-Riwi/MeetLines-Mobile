package com.meetline.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// Colores base para configuración de temas
internal val PrimaryLight = Color(0xFF2563EB)
internal val PrimaryDark = Color(0xFF3B82F6)
internal val SurfaceLight = Color(0xFFFFFFFF)
internal val SurfaceDarkColor = Color(0xFF1E293B)
internal val BackgroundLight = Color(0xFFF1F5F9)
internal val BackgroundDarkColor = Color(0xFF0F172A)

// Estados (mantener igual en ambos temas)
val Success = Color(0xFF059669)
val Warning = Color(0xFFD97706)
val Error = Color(0xFFDC2626)
val Info = Color(0xFF2563EB)

// Colores adaptables al tema - USAR ESTOS EN LOS COMPONENTES
val Primary: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.primary

val Surface: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surface

val Background: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.background

val OnSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onSurface

val OnSurfaceVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onSurfaceVariant

val SurfaceVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceVariant

val OnPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onPrimary

val OnBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onBackground

// Para compatibilidad con código legacy
internal val PrimaryContainer = Color(0xFFDBEAFE)
internal val Secondary = Color(0xFF475569)
internal val SecondaryLight = Color(0xFF64748B)
internal val SecondaryDark = Color(0xFF334155)
internal val Tertiary = Color(0xFF059669)
internal val TertiaryLight = Color(0xFF10B981)
internal val GradientStart = Color(0xFF2563EB)
internal val GradientEnd = Color(0xFF3B82F6)
internal val PrimaryDarkTheme = Color(0xFF3B82F6)
internal val SurfaceDark = Color(0xFF1E293B)
internal val SurfaceVariantDark = Color(0xFF334155)
internal val OnSurfaceDark = Color(0xFFF8FAFC)
internal val OnSurfaceVariantDark = Color(0xFF94A3B8)
internal val BackgroundDark = Color(0xFF0F172A)
internal val Purple80 = PrimaryLight
internal val PurpleGrey80 = Color(0xFFCCC2DC)
internal val Pink80 = Color(0xFFEFB8C8)
internal val Purple40 = PrimaryLight
internal val PurpleGrey40 = Color(0xFF625b71)
internal val Pink40 = Color(0xFF7D5260)