package com.auto_care_test.ui.theme

import androidx.compose.ui.graphics.Color

// Futuristic neon-on-dark identity
val NeonViolet = Color(0xFF7C5CFC)
val NeonMagenta = Color(0xFFD946EF)
val NeonCyan = Color(0xFF22D3EE)
val NeonGreen = Color(0xFF34F5A3)
val NeonCoral = Color(0xFFFF5577)

val DeepSpace = Color(0xFF0A0D1A)
val SurfaceDark = Color(0xFF131A30)
val SurfaceVariantDark = Color(0xFF1C2542)
val OutlineDark = Color(0xFF333E66)
val OnDark = Color(0xFFEAEBFA)
val OnDarkMuted = Color(0xFFA8AFD6)

val Blue80 = NeonCyan
val Amber80 = NeonGreen
val BlueGrey80 = NeonViolet

val Blue40 = NeonViolet
val Amber40 = NeonMagenta
val BlueGrey40 = NeonGreen

val BackgroundLight = DeepSpace
val SurfaceLight = SurfaceDark
val SurfaceVariantLight = SurfaceVariantDark

// Header gradient
val GradientStart = NeonViolet
val GradientEnd = NeonCyan

// Status colors (neon tints, foreground use only)
val StatusPendiente = NeonCyan
val StatusRealizado = NeonGreen
val StatusVencido   = NeonCoral

val StatusPendienteContainer = NeonCyan.copy(alpha = 0.16f)
val StatusRealizadoContainer = NeonGreen.copy(alpha = 0.16f)
val StatusVencidoContainer   = NeonCoral.copy(alpha = 0.16f)
