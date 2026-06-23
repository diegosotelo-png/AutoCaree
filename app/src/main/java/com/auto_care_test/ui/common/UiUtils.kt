package com.auto_care_test.ui.common

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.auto_care_test.ui.theme.GradientEnd
import com.auto_care_test.ui.theme.GradientStart
import com.auto_care_test.ui.theme.StatusPendiente
import com.auto_care_test.ui.theme.StatusPendienteContainer
import com.auto_care_test.ui.theme.StatusRealizado
import com.auto_care_test.ui.theme.StatusRealizadoContainer
import com.auto_care_test.ui.theme.StatusVencido
import com.auto_care_test.ui.theme.StatusVencidoContainer
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.Locale.forLanguageTag

val HeaderGradient = Brush.linearGradient(listOf(GradientStart, GradientEnd))

/** Bloque "shimmer" que sugiere contenido cargando, en vez de un spinner genérico. */
@Composable
fun ShimmerBox(modifier: Modifier = Modifier, shape: RoundedCornerShape = RoundedCornerShape(12.dp)) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )
    Box(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.18f))
    )
}

/** Encoge ligeramente el componente mientras se presiona, para que se sienta táctil. */
@Composable
fun Modifier.pressScale(interactionSource: MutableInteractionSource): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "pressScale"
    )
    return this.graphicsLayer { scaleX = scale; scaleY = scale }
}

fun estadoColor(estado: String): Color = when (estado) {
    "Pendiente" -> StatusPendiente
    "Realizado" -> StatusRealizado
    "Vencido"   -> StatusVencido
    else        -> Color.Gray
}

fun estadoContainerColor(estado: String): Color = when (estado) {
    "Pendiente" -> StatusPendienteContainer
    "Realizado" -> StatusRealizadoContainer
    "Vencido"   -> StatusVencidoContainer
    else        -> Color(0xFFF5F5F5)
}

fun formatFecha(raw: String): String = try {
    LocalDate.parse(raw)
        .format(DateTimeFormatter.ofPattern("dd MMM yyyy", forLanguageTag("es")))
} catch (e: Exception) { raw }

fun iconoTipoMantenimiento(tipo: String): ImageVector = when (tipo) {
    "Preventivo" -> Icons.Default.Build
    "Correctivo" -> Icons.Default.Handyman
    "Mejora"     -> Icons.Default.Upgrade
    else         -> Icons.Default.Build
}

/** Título con relleno en gradiente para un toque más futurista. */
@Composable
fun GradientText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight? = null
) {
    Text(
        text = text,
        modifier = modifier,
        style = style.copy(brush = HeaderGradient),
        fontWeight = fontWeight
    )
}

@Composable
fun StatusChip(estado: String) {
    val pulseAlpha: Float = if (estado == "Vencido") {
        val transition = rememberInfiniteTransition(label = "vencidoPulse")
        val alpha by transition.animateFloat(
            initialValue = 1f,
            targetValue = 0.4f,
            animationSpec = infiniteRepeatable(
                animation = tween(700, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "vencidoPulseAlpha"
        )
        alpha
    } else 1f

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(estadoContainerColor(estado).copy(alpha = estadoContainerColor(estado).alpha * (0.6f + 0.4f * pulseAlpha)))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(
            text = estado,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = estadoColor(estado).copy(alpha = estadoColor(estado).alpha * (0.7f + 0.3f * pulseAlpha))
        )
    }
}
