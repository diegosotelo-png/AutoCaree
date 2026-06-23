package com.auto_care_test.ui.mantenimiento

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.auto_care_test.ui.common.HeaderGradient
import com.auto_care_test.ui.common.StatusChip
import com.auto_care_test.ui.common.estadoColor
import com.auto_care_test.ui.common.formatFecha
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

    LaunchedEffect(idMantenimiento) {
        viewModel.cargarDetalle(idMantenimiento)
    }

    Scaffold(
        topBar = {
            Box(modifier = Modifier.background(HeaderGradient)) {
                TopAppBar(
                    title = { Text("Detalle", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToEdit) {
                            Icon(Icons.Default.Edit, "Editar")
                        }
                        IconButton(onClick = {
                            viewModel.eliminarMantenimiento(idMantenimiento)
                            onNavigateBack()
                        }) {
                            Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
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
                val statusColor = estadoColor(m.estado)

                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                        elevation = CardDefaults.cardElevation(3.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = m.titulo,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                                )
                                StatusChip(m.estado)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = m.tipoMantenimiento,
                                style = MaterialTheme.typography.bodyMedium,
                                color = statusColor
                            )
                        }
                    }

                    // Vehículo asociado
                    uiState.vehiculoAsociado?.let { v ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    "Vehículo asociado",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                InfoRow(Icons.Default.DirectionsCar, "Vehículo", "${v.marca} ${v.modelo}")
                                InfoRow(Icons.Default.Badge, "Placa", v.placa)
                                InfoRow(Icons.Default.Category, "Tipo", v.tipoVehiculo)
                            }
                        }
                    }

                    // Info card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                "Información",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            InfoRow(Icons.Default.CalendarMonth, "Fecha programada", formatFecha(m.fechaProgramada))
                            InfoRow(Icons.Default.Category, "Tipo", m.tipoMantenimiento)
                            InfoRow(
                                Icons.Default.Notifications,
                                "Recordatorio",
                                if (m.recordatorioActivo) "Activo" else "Inactivo"
                            )
                            if (m.descripcion.isNotBlank()) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Description,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Text(
                                            "Descripción",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                                        )
                                    }
                                    Text(
                                        text = m.descripcion,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(start = 48.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Technical API card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Text(
                                text = uiState.consejoApi ?: "Cargando información técnica...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.5f
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
        }
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}
