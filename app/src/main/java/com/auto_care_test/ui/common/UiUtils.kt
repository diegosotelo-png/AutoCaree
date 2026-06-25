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
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.*
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
    "Preventivo"        -> Icons.Default.Build
    "Correctivo"        -> Icons.Default.Handyman
    "SOAT"              -> Icons.AutoMirrored.Filled.Article
    "Revisión Técnica"  -> Icons.AutoMirrored.Filled.FactCheck
    "Cambio de Aceite"  -> Icons.Default.OilBarrel
    "Frenos"            -> Icons.Default.TripOrigin
    "Batería"           -> Icons.Default.BatteryChargingFull
    "Refrigerante"      -> Icons.Default.Thermostat
    "Mejora"            -> Icons.Default.Upgrade
    else                -> Icons.Default.Build
}

/** Ícono representativo por tipo de vehículo. */
fun iconoTipoVehiculo(tipo: String): ImageVector = when (tipo) {
    "Auto"        -> Icons.Default.DirectionsCar
    "Moto"        -> Icons.Default.TwoWheeler
    "SUV/4x4"     -> Icons.Default.DirectionsCarFilled
    "Otros"       -> Icons.Default.Category
    // Compatibilidad con datos antiguos
    "Camioneta"   -> Icons.Default.LocalShipping
    "Bus/Minibus" -> Icons.Default.DirectionsBus
    else          -> Icons.Default.DirectionsCar
}

// ============================================================
//  Catálogos para los formularios (datos de presentación)
// ============================================================

/** Tipo de mantenimiento + su ícono, para chips/selectores. */
data class OpcionMantenimiento(val nombre: String, val icono: ImageVector)

val TIPOS_MANTENIMIENTO: List<OpcionMantenimiento> = listOf(
    OpcionMantenimiento("Preventivo", Icons.Default.Build),
    OpcionMantenimiento("Correctivo", Icons.Default.Handyman),
    OpcionMantenimiento("SOAT", Icons.AutoMirrored.Filled.Article),
    OpcionMantenimiento("Revisión Técnica", Icons.AutoMirrored.Filled.FactCheck),
    OpcionMantenimiento("Cambio de Aceite", Icons.Default.OilBarrel),
    OpcionMantenimiento("Frenos", Icons.Default.TripOrigin),
    OpcionMantenimiento("Batería", Icons.Default.BatteryChargingFull),
    OpcionMantenimiento("Refrigerante", Icons.Default.Thermostat)
)

/** Tipo de vehículo + su ícono. */
data class OpcionVehiculo(val nombre: String, val icono: ImageVector)

val TIPOS_VEHICULO: List<OpcionVehiculo> = listOf(
    OpcionVehiculo("Auto", Icons.Default.DirectionsCar),
    OpcionVehiculo("Moto", Icons.Default.TwoWheeler),
    OpcionVehiculo("SUV/4x4", Icons.Default.DirectionsCarFilled),
    OpcionVehiculo("Otros", Icons.Default.Category)
)

/** Etiqueta especial del dropdown para escribir una marca que no está en la lista. */
const val MARCA_OTRA = "Otra (escribir)…"

/** Marcas de autos más comunes en el mercado (incluye marcas chinas). */
val MARCAS_VEHICULO: List<String> = listOf(
    "Toyota", "Chevrolet", "Hyundai", "Kia", "Nissan",
    "Volkswagen", "Ford", "Honda", "Mazda", "Renault",
    "Mitsubishi", "Suzuki", "Jeep", "BMW", "Mercedes-Benz",
    "Audi", "Subaru", "Volvo", "Peugeot", "Citroën",
    // Marcas chinas
    "BYD", "Chery", "Great Wall", "Haval", "JAC",
    "Changan", "Geely", "DFSK", "Foton", "MG"
)

/** Marcas de motos más importantes. */
val MARCAS_MOTO: List<String> = listOf(
    "Honda", "Yamaha", "Suzuki", "Bajaj", "Italika",
    "TVS", "Hero", "KTM", "Kawasaki", "Pulsar",
    "Lifan", "Zongshen", "Wanxin", "Ronco", "Royal Enfield"
)

/** Devuelve las marcas sugeridas según el tipo de vehículo elegido. */
fun marcasPorTipo(tipo: String): List<String> = when (tipo) {
    "Moto"  -> MARCAS_MOTO
    "Otros" -> emptyList()           // el usuario la escribe a mano
    else    -> MARCAS_VEHICULO       // Auto, SUV/4x4
}

/**
 * Modelos comunes por marca. Si una marca no está aquí, la UI debe
 * permitir escribir el modelo libremente.
 */
val MODELOS_POR_MARCA: Map<String, List<String>> = mapOf(
    "Toyota"    to listOf("Corolla", "Yaris", "RAV4", "Hilux", "Land Cruiser"),
    "Hyundai"   to listOf("Tucson", "Santa Fe", "Elantra", "i10", "Accent"),
    "Kia"       to listOf("Sportage", "Seltos", "Rio", "Sorento", "Stinger"),
    "Chevrolet" to listOf("Spark", "Tracker", "Captiva", "Onix", "Cruze"),
    "Nissan"    to listOf("Sentra", "Versa", "X-Trail", "Frontier", "Kicks")
)

/** Modelos de moto más importantes por marca. */
val MODELOS_MOTO_POR_MARCA: Map<String, List<String>> = mapOf(
    "Honda"         to listOf("CB 190R", "XR 150L", "CG 125", "Navi", "Tornado", "Wave"),
    "Yamaha"        to listOf("YBR 125", "FZ", "MT-03", "XTZ 125", "R15", "Crypton"),
    "Suzuki"        to listOf("GN 125", "Gixxer", "DR 150", "GSX", "AX 100"),
    "Bajaj"         to listOf("Pulsar 180", "Pulsar 200", "Rouser", "Dominar", "Boxer"),
    "Italika"       to listOf("FT 150", "DM 150", "125Z", "Vitro", "RT 200"),
    "TVS"           to listOf("Apache RTR", "Raider", "Sport", "Star HLX"),
    "Hero"          to listOf("Hunk", "Eco Deluxe", "Dash", "Splendor"),
    "KTM"           to listOf("Duke 200", "Duke 390", "RC 200", "Adventure 390"),
    "Kawasaki"      to listOf("Ninja 400", "Z400", "KLX 150", "Versys 300"),
    "Pulsar"        to listOf("NS 200", "NS 160", "N250", "RS 200"),
    "Royal Enfield" to listOf("Classic 350", "Meteor 350", "Hunter 350", "Himalayan")
)

/** Modelos sugeridos según el tipo de vehículo y la marca. Null = escribir libre. */
fun modelosPorMarca(tipo: String, marca: String): List<String>? =
    if (tipo == "Moto") MODELOS_MOTO_POR_MARCA[marca]
    else MODELOS_POR_MARCA[marca]

/** Etiqueta especial para escribir un título de mantenimiento que no está en la lista. */
const val TITULO_OTRO = "Otro (escribir)…"

/** Mantenimientos más comunes para autos / SUV. */
val TITULOS_MANTENIMIENTO_AUTO: List<String> = listOf(
    "Cambio de aceite", "Cambio de frenos", "Cambio de llantas",
    "Alineación y balanceo", "Cambio de batería", "Cambio de filtro de aire",
    "Cambio de bujías", "Cambio de refrigerante", "Revisión técnica", "SOAT"
)

/** Mantenimientos más comunes para motos. */
val TITULOS_MANTENIMIENTO_MOTO: List<String> = listOf(
    "Cambio de aceite", "Ajuste de cadena", "Cambio de frenos",
    "Cambio de llantas", "Cambio de bujía", "Cambio de filtro de aire",
    "Revisión técnica", "SOAT"
)

/** Títulos sugeridos según el tipo de vehículo seleccionado. */
fun titulosPorTipoVehiculo(tipo: String): List<String> =
    if (tipo == "Moto") TITULOS_MANTENIMIENTO_MOTO else TITULOS_MANTENIMIENTO_AUTO

/** Indica si el título es uno de los predefinidos (auto o moto). */
fun esTituloPredefinido(titulo: String): Boolean =
    titulo in TITULOS_MANTENIMIENTO_AUTO || titulo in TITULOS_MANTENIMIENTO_MOTO

/**
 * Estado mostrado al usuario: si está "Pendiente" pero la fecha ya pasó,
 * se considera "Vencido" automáticamente. El "Vencido" no se asigna a mano.
 */
fun estadoEfectivo(estado: String, fechaProgramada: String): String {
    if (estado != "Pendiente") return estado
    return try {
        if (LocalDate.parse(fechaProgramada).isBefore(LocalDate.now())) "Vencido" else "Pendiente"
    } catch (e: Exception) {
        estado
    }
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
