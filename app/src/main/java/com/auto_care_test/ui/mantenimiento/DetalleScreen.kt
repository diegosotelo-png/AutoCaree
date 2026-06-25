package com.auto_care_test.ui.mantenimiento

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import com.auto_care_test.ui.common.HeaderGradient
import com.auto_care_test.ui.common.StatusChip
import com.auto_care_test.ui.common.estadoColor
import com.auto_care_test.ui.common.estadoEfectivo
import com.auto_care_test.ui.common.formatFecha
import com.auto_care_test.ui.common.iconoTipoMantenimiento
import com.auto_care_test.ui.common.iconoTipoVehiculo
import com.auto_care_test.viewmodel.MantenimientoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    idMantenimiento: Int,
    viewModel: MantenimientoViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteConfirm by remember { mutableStateOf(false) }
    // Override optimista del estado al marcar realizado/pendiente sin entrar a editar
    var estadoOverride by remember(idMantenimiento) { mutableStateOf<String?>(null) }
    var showCelebracion by remember { mutableStateOf(false) }

    LaunchedEffect(idMantenimiento) {
        viewModel.cargarDetalle(idMantenimiento)
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            icon = { Icon(Icons.Default.DeleteForever, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Eliminar mantenimiento") },
            text = { Text("Esta acción no se puede deshacer. ¿Deseas continuar?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    viewModel.eliminarMantenimiento(idMantenimiento)
                    onNavigateBack()
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            Box(modifier = Modifier.background(HeaderGradient)) {
                TopAppBar(
                    title = { Text("Ficha del mantenimiento", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            uiState.mensajeError != null -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.mensajeError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            else -> uiState.mantenimientoSeleccionado?.let { m ->
                val estadoStored = estadoOverride ?: m.estado
                val estadoEff = estadoEfectivo(estadoStored, m.fechaProgramada)
                val statusColor = estadoColor(estadoEff)
                val realizado = estadoStored == "Realizado"

                if (showCelebracion) {
                    CelebracionDialog(titulo = m.titulo, onDismiss = { showCelebracion = false })
                }

                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // --- Encabezado tipo "ficha": ícono del tipo + título + estado grande ---
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                        elevation = CardDefaults.cardElevation(3.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(statusColor.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        iconoTipoMantenimiento(m.tipoMantenimiento),
                                        contentDescription = null,
                                        tint = statusColor,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = m.titulo,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = m.tipoMantenimiento,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // Indicador de estado grande
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(statusColor.copy(alpha = 0.12f))
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(statusColor)
                                )
                                Text(
                                    "Estado: $estadoEff",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = statusColor
                                )
                            }
                        }
                    }

                    // --- Vehículo asociado ---
                    uiState.vehiculoAsociado?.let { v ->
                        SectionCard(title = "Vehículo asociado", icon = Icons.Default.DirectionsCar) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .background(HeaderGradient),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        iconoTipoVehiculo(v.tipoVehiculo),
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "${v.marca} ${v.modelo}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "${v.placa}  ·  ${v.tipoVehiculo}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                                    )
                                }
                            }
                        }
                    }

                    // --- Información ---
                    SectionCard(title = "Información", icon = Icons.Default.Info) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            InfoRow(Icons.Default.CalendarMonth, "Fecha programada", formatFecha(m.fechaProgramada))
                            InfoRow(Icons.Default.Category, "Tipo", m.tipoMantenimiento)
                            InfoRow(
                                Icons.Default.Notifications,
                                "Recordatorio",
                                if (m.recordatorioActivo) "Activo" else "Inactivo"
                            )
                            if (m.descripcion.isNotBlank()) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                InfoRow(Icons.Default.Description, "Descripción", m.descripcion)
                            }
                        }
                    }

                    // --- Datos técnicos de la API en chips ---
                    val techPairs = remember(uiState.consejoApi) {
                        uiState.consejoApi?.lines()?.mapNotNull { line ->
                            val idx = line.indexOf(':')
                            if (idx > 0) line.substring(0, idx).trim() to line.substring(idx + 1).trim()
                            else null
                        } ?: emptyList()
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    "Datos técnicos del vehículo",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            if (techPairs.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    techPairs.forEach { (label, value) ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                label,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.65f)
                                            )
                                            Text(
                                                value,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(start = 12.dp)
                                            )
                                        }
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f)
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = uiState.consejoApi ?: "Cargando información técnica...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    // --- Marcar realizado deslizando / barra de completado ---
                    if (realizado) {
                        CompletedBar(
                            onUndo = {
                                estadoOverride = "Pendiente"
                                viewModel.editarMantenimiento(m.copy(estado = "Pendiente"))
                            }
                        )
                    } else {
                        SwipeToComplete(
                            onComplete = {
                                estadoOverride = "Realizado"
                                viewModel.editarMantenimiento(m.copy(estado = "Realizado"))
                                showCelebracion = true
                            }
                        )
                    }

                    // --- Acciones visibles ---
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = onNavigateToEdit,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Editar", fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = { showDeleteConfirm = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Eliminar", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, icon: ImageVector, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
            content()
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
        }
        Column(modifier = Modifier.padding(top = 2.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}

/** Deslizador "desliza para marcar realizado" (estilo slide-to-confirm). */
@Composable
private fun SwipeToComplete(onComplete: () -> Unit) {
    val verde = estadoColor("Realizado")
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val thumbSize = 48.dp
    val hPad = 4.dp
    val thumbSizePx = with(density) { thumbSize.toPx() }
    val hPadPx = with(density) { hPad.toPx() }
    var trackWidthPx by remember { mutableIntStateOf(0) }
    val maxOffset = (trackWidthPx - thumbSizePx - hPadPx * 2).coerceAtLeast(0f)
    val offsetX = remember { Animatable(0f) }
    val progress = if (maxOffset > 0f) (offsetX.value / maxOffset).coerceIn(0f, 1f) else 0f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .onGloballyPositioned { trackWidthPx = it.size.width },
        contentAlignment = Alignment.CenterStart
    ) {
        // Relleno verde que crece con el avance
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .background(verde.copy(alpha = 0.35f))
        )
        // Texto guía (se desvanece al deslizar)
        Text(
            "Desliza para marcar realizado",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer { alpha = 1f - progress }
        )
        // Perilla deslizable
        Box(
            modifier = Modifier
                .padding(horizontal = hPad)
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .size(thumbSize)
                .clip(CircleShape)
                .background(verde)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        scope.launch {
                            offsetX.snapTo((offsetX.value + delta).coerceIn(0f, maxOffset))
                        }
                    },
                    onDragStopped = {
                        if (maxOffset > 0f && offsetX.value >= maxOffset * 0.9f) {
                            offsetX.animateTo(maxOffset)
                            onComplete()
                        } else {
                            offsetX.animateTo(0f)
                        }
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.KeyboardDoubleArrowRight,
                contentDescription = "Deslizar para completar",
                tint = Color.White
            )
        }
    }
}

/** Diálogo central de felicitación al completar un mantenimiento. */
@Composable
private fun CelebracionDialog(titulo: String, onDismiss: () -> Unit) {
    val verde = estadoColor("Realizado")
    val scale = remember { Animatable(0.6f) }
    LaunchedEffect(Unit) {
        scale.animateTo(
            1f,
            animationSpec = androidx.compose.animation.core.spring(
                dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                stiffness = androidx.compose.animation.core.Spring.StiffnessLow
            )
        )
    }
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { scaleX = scale.value; scaleY = scale.value },
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(verde.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(verde),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    "¡Felicidades! 🎉",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Completaste «$titulo»",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
                Text(
                    "Tu vehículo te lo agradece 🚗",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = verde, contentColor = Color.White)
                ) {
                    Text("¡Genial!", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/** Barra mostrada cuando el mantenimiento ya está realizado, con opción de deshacer. */
@Composable
private fun CompletedBar(onUndo: () -> Unit) {
    val verde = estadoColor("Realizado")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(verde.copy(alpha = 0.2f))
            .padding(start = 18.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = verde)
            Text(
                "¡Realizado!",
                fontWeight = FontWeight.Bold,
                color = verde,
                style = MaterialTheme.typography.titleSmall
            )
        }
        TextButton(onClick = onUndo) {
            Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text("Deshacer")
        }
    }
}

