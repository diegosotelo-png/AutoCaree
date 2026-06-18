package com.auto_care_test.ui.mantenimiento

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.auto_care_test.domain.model.Mantenimiento
import com.auto_care_test.ui.common.estadoColor
import com.auto_care_test.ui.common.estadoContainerColor
import com.auto_care_test.ui.common.formatFecha
import com.auto_care_test.viewmodel.MantenimientoViewModel
import com.auto_care_test.viewmodel.VehiculoViewModel
import java.time.Instant
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(
    idMantenimiento: Int?,
    mantenimientoViewModel: MantenimientoViewModel,
    vehiculoViewModel: VehiculoViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by mantenimientoViewModel.uiState.collectAsState()
    val vehiculos by vehiculoViewModel.vehiculos.collectAsState()

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var tipoMantenimiento by remember { mutableStateOf("") }
    var fechaProgramada by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("Pendiente") }
    var recordatorioActivo by remember { mutableStateOf(true) }
    var idVehiculoSeleccionado by remember { mutableStateOf<Int?>(null) }

    var expandedVehiculo by remember { mutableStateOf(false) }
    var expandedTipo by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val tipos = listOf("Preventivo", "Correctivo", "Mejora")
    val estados = listOf("Pendiente", "Realizado", "Vencido")

    LaunchedEffect(idMantenimiento) {
        if (idMantenimiento != null) mantenimientoViewModel.cargarDetalle(idMantenimiento)
    }

    LaunchedEffect(uiState.mantenimientoSeleccionado) {
        if (idMantenimiento != null) {
            uiState.mantenimientoSeleccionado?.let { m ->
                titulo = m.titulo
                descripcion = m.descripcion
                tipoMantenimiento = m.tipoMantenimiento
                fechaProgramada = m.fechaProgramada
                estado = m.estado
                recordatorioActivo = m.recordatorioActivo
                idVehiculoSeleccionado = m.idVehiculo
            }
        }
    }

    LaunchedEffect(uiState.guardadoExitoso) {
        if (uiState.guardadoExitoso) {
            mantenimientoViewModel.resetGuardado()
            onNavigateBack()
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        fechaProgramada = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                            .toString()
                    }
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (idMantenimiento == null) "Nuevo Mantenimiento" else "Editar Mantenimiento",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
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
            // Sección: Vehículo
            FormSection(title = "Vehículo") {
                ExposedDropdownMenuBox(
                    expanded = expandedVehiculo,
                    onExpandedChange = { expandedVehiculo = it }
                ) {
                    OutlinedTextField(
                        value = vehiculos.find { it.idVehiculo == idVehiculoSeleccionado }
                            ?.let { "${it.marca} ${it.modelo}" } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Seleccionar vehículo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedVehiculo) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedVehiculo,
                        onDismissRequest = { expandedVehiculo = false }
                    ) {
                        if (vehiculos.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("No hay vehículos registrados") },
                                onClick = { expandedVehiculo = false },
                                enabled = false
                            )
                        } else {
                            vehiculos.forEach { v ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text("${v.marca} ${v.modelo}", fontWeight = FontWeight.Medium)
                                            Text(v.placa, style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                        }
                                    },
                                    onClick = {
                                        idVehiculoSeleccionado = v.idVehiculo
                                        expandedVehiculo = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Sección: Detalles
            FormSection(title = "Detalles del mantenimiento") {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
                ExposedDropdownMenuBox(
                    expanded = expandedTipo,
                    onExpandedChange = { expandedTipo = it }
                ) {
                    OutlinedTextField(
                        value = tipoMantenimiento,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de mantenimiento") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedTipo) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedTipo,
                        onDismissRequest = { expandedTipo = false }
                    ) {
                        tipos.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t) },
                                onClick = { tipoMantenimiento = t; expandedTipo = false }
                            )
                        }
                    }
                }
            }

            // Sección: Programación
            FormSection(title = "Programación") {
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(end = 0.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (fechaProgramada.isEmpty()) "Seleccionar fecha"
                               else formatFecha(fechaProgramada),
                        fontWeight = if (fechaProgramada.isEmpty()) FontWeight.Normal else FontWeight.SemiBold
                    )
                }

                Text(
                    text = "Estado",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    estados.forEach { e ->
                        FilterChip(
                            selected = estado == e,
                            onClick = { estado = e },
                            label = { Text(e, style = MaterialTheme.typography.labelMedium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = estadoContainerColor(e),
                                selectedLabelColor = estadoColor(e)
                            )
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Switch(
                        checked = recordatorioActivo,
                        onCheckedChange = { recordatorioActivo = it }
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Recordatorio activo", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        Text(
                            "Recibirás alertas de este mantenimiento",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (idVehiculoSeleccionado != null) {
                        val m = Mantenimiento(
                            idMantenimiento = idMantenimiento ?: 0,
                            idVehiculo = idVehiculoSeleccionado!!,
                            titulo = titulo.trim(),
                            descripcion = descripcion.trim(),
                            tipoMantenimiento = tipoMantenimiento,
                            fechaProgramada = fechaProgramada,
                            estado = estado,
                            recordatorioActivo = recordatorioActivo
                        )
                        if (idMantenimiento == null) mantenimientoViewModel.guardarMantenimiento(m)
                        else mantenimientoViewModel.editarMantenimiento(m)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = titulo.isNotBlank()
                        && idVehiculoSeleccionado != null
                        && fechaProgramada.isNotEmpty()
                        && tipoMantenimiento.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (idMantenimiento == null) "Guardar mantenimiento" else "Actualizar mantenimiento",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FormSection(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            content()
        }
    }
}
