package com.auto_care_test.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================================
//  AutoCare — Identidad de marca: AZUL PROFUNDO + ÁMBAR
// ============================================================

// --- Marca ---
val AutoBlue       = Color(0xFF2563EB) // Primario (azul eléctrico)
val AutoBlueLight  = Color(0xFF3B82F6)
val AutoBlueBright = Color(0xFF60A5FA) // Primario en modo oscuro
val AutoBlueDeep   = Color(0xFF1E40AF)
val AutoCyan       = Color(0xFF22D3EE) // Cian futurista (cierre del gradiente)
val AutoAmber      = Color(0xFFF59E0B) // Acento (modo claro)
val AutoAmberLight = Color(0xFFFBBF24) // Acento (modo oscuro)
val AutoAmberDeep  = Color(0xFFD97706)

// --- Neutros: modo claro ---
val LightBackground     = Color(0xFFF3F6FB)
val LightSurface        = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFE6EDF7)
val LightOutline        = Color(0xFFCBD6E6)
val LightOnSurface      = Color(0xFF16202E)
val LightOnSurfaceMuted = Color(0xFF5A6678)
val LightPrimaryContainer = Color(0xFFD6E4F7)

// --- Neutros: modo oscuro (navy) ---
val DarkBackground     = Color(0xFF0E1726)
val DarkSurface        = Color(0xFF16213A)
val DarkSurfaceVariant = Color(0xFF1E2A44)
val DarkOutline        = Color(0xFF2C3A55)
val DarkOnSurface      = Color(0xFFE8EDF5)
val DarkOnSurfaceMuted = Color(0xFF9FB0C9)
val DarkPrimaryContainer = Color(0xFF1E2A44)

// --- Estados (semáforo) ---
val StatusPendiente = Color(0xFF2D7FF9) // Azul
val StatusRealizado = Color(0xFF22C55E) // Verde
val StatusVencido   = Color(0xFFEF4444) // Rojo

val StatusPendienteContainer = StatusPendiente.copy(alpha = 0.16f)
val StatusRealizadoContainer = StatusRealizado.copy(alpha = 0.16f)
val StatusVencidoContainer   = StatusVencido.copy(alpha = 0.16f)

// --- Gradiente de cabecera (azul profundo → teal, futurista pero legible) ---
val GradientStart = Color(0xFF1E3A8A)
val GradientEnd   = Color(0xFF0E7490)

// ============================================================
//  Alias de compatibilidad (las pantallas existentes los usan)
//  Se remapean a la nueva paleta azul + ámbar.
// ============================================================
val DeepSpace  = DarkBackground
val NeonViolet = AutoBlue        // tinte azul de marca
val NeonCyan   = AutoAmber       // tinte ámbar de acento
val NeonGreen  = StatusRealizado // verde de "realizado"/verificado
val NeonCoral  = StatusVencido   // rojo de error/vencido
