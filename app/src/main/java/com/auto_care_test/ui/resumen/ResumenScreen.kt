package com.auto_care_test.ui.resumen

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import java.time.LocalDate
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.auto_care_test.ui.common.HeaderGradient
import com.auto_care_test.ui.theme.StatusPendiente
import com.auto_care_test.ui.theme.StatusRealizado
import com.auto_care_test.ui.theme.StatusVencido
import com.auto_care_test.viewmodel.MantenimientoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenScreen(
    viewModel: MantenimientoViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val lista = uiState.mantenimientos

    val hoy = LocalDate.now()
    val total = lista.size
    val pendientes = lista.count { it.estado == "Pendiente" }
    val realizados = lista.count { it.estado == "Realizado" }
    val vencidos = lista.count { it.estado == "Vencido" }
    val proximos = lista.count { m ->
        m.estado == "Pendiente" && runCatching {
            val fecha = LocalDate.parse(m.fechaProgramada)
            !fecha.isBefore(hoy) && !fecha.isAfter(hoy.plusDays(30))
        }.getOrDefault(false)
    }
    val progreso = if (total > 0) realizados.toFloat() / total else 0f

    Scaffold(
        topBar = {
            Box(modifier = Modifier.background(HeaderGradient)) {
                TopAppBar(
                    title = {
                        Text(
                            "Tu Resumen",
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
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
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Total + progress card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(HeaderGradient)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Total de mantenimientos",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
                            )
                            Text(
                                "$total",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        // Anillo de progreso con el % de completado
                        Box(modifier = Modifier.size(76.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { 1f },
                                modifier = Modifier.fillMaxSize(),
                                strokeWidth = 7.dp,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                                trackColor = Color.Transparent,
                                strokeCap = StrokeCap.Round
                            )
                            CircularProgressIndicator(
                                progress = { progreso },
                                modifier = Modifier.fillMaxSize(),
                                strokeWidth = 7.dp,
                                color = MaterialTheme.colorScheme.onPrimary,
                                trackColor = Color.Transparent,
                                strokeCap = StrokeCap.Round
                            )
                            Text(
                                "${(progreso * 100).toInt()}%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                    Text(
                        "Tasa de completado",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
                    )
                }
            }

            // Stats grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    titulo = "Pendientes",
                    valor = pendientes,
                    color = StatusPendiente,
                    icon = Icons.Default.Schedule
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    titulo = "Realizados",
                    valor = realizados,
                    color = StatusRealizado,
                    icon = Icons.Default.CheckCircle
                )
            }

            StatCard(
                modifier = Modifier.fillMaxWidth(),
                titulo = "Vencidos sin atender",
                valor = vencidos,
                color = StatusVencido,
                icon = Icons.Default.Warning,
                horizontal = true
            )

            StatCard(
                modifier = Modifier.fillMaxWidth(),
                titulo = "Próximos 30 días",
                valor = proximos,
                color = MaterialTheme.colorScheme.tertiary,
                icon = Icons.Default.CalendarMonth,
                horizontal = true
            )

            if (total == 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Aún no hay mantenimientos registrados",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    titulo: String,
    valor: Int,
    color: Color,
    icon: ImageVector,
    horizontal: Boolean = false
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        if (horizontal) {
            Row(
                modifier = Modifier.padding(20.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, null, tint = color, modifier = Modifier.size(26.dp))
                    }
                    Text(titulo, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                }
                Text(
                    "$valor",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        } else {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
                }
                Column {
                    Text(
                        "$valor",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                    Text(
                        titulo,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
