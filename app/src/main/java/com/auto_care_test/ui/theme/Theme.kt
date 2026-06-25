package com.auto_care_test.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Esquema CLARO bien definido: azul de marca + ámbar de acento sobre neutros claros.
private val AutoCareLightScheme = lightColorScheme(
    primary = AutoBlue,
    onPrimary = Color.White,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = AutoBlueDeep,
    secondary = AutoAmber,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE3C2),
    onSecondaryContainer = Color(0xFF6B3B00),
    tertiary = StatusPendiente,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceMuted,
    outline = LightOutline,
    error = StatusVencido,
    onError = Color.White,
    errorContainer = Color(0xFFFDECEC),
    onErrorContainer = Color(0xFF8A1212)
)

// Esquema OSCURO (navy): mismo lenguaje de marca adaptado a fondo oscuro.
private val AutoCareDarkScheme = darkColorScheme(
    primary = AutoBlueBright,
    onPrimary = Color.White,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = Color(0xFFBBD4F0),
    secondary = AutoAmberLight,
    onSecondary = Color(0xFF231400),
    secondaryContainer = Color(0xFF3A2A0A),
    onSecondaryContainer = Color(0xFFFFE0B2),
    tertiary = StatusPendiente,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceMuted,
    outline = DarkOutline,
    error = Color(0xFFFF6B6B),
    onError = Color(0xFF2A0A0A),
    errorContainer = Color(0xFF3A1414),
    onErrorContainer = Color(0xFFFFB4B4)
)

@Composable
fun AutocaretestTheme(
    // AutoCare usa siempre su identidad oscura (navy) para un look premium,
    // independiente del tema del sistema. El esquema claro queda disponible
    // por si se quiere ofrecer como opción.
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) AutoCareDarkScheme else AutoCareLightScheme
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
