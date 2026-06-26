package com.auto_care_test.ui.mantenimiento

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.auto_care_test.domain.model.Mantenimiento
import com.auto_care_test.notification.RecordatorioWorker
import com.auto_care_test.ui.common.TIPOS_MANTENIMIENTO
import com.auto_care_test.ui.common.TITULO_OTRO
import com.auto_care_test.ui.common.esTituloPredefinido
import com.auto_care_test.ui.common.estadoColor
import com.auto_care_test.ui.common.estadoContainerColor
import com.auto_care_test.ui.common.formatFecha
import com.auto_care_test.ui.common.iconoTipoMantenimiento
import com.auto_care_test.ui.common.pressScale
import com.auto_care_test.ui.common.titulosPorTipoVehiculo
import com.auto_care_test.viewmodel.MantenimientoViewModel
import com.auto_care_test.viewmodel.VehiculoViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit

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
    val context = LocalContext.current

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var tipoMantenimiento by remember { mutableStateOf("") }
    var fechaProgramada by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("Pendiente") }
    var recordatorioActivo by remember { mutableStateOf(true) }
    var idVehiculoSeleccionado by remember { mutableStateOf<Int?>(null) }

    var tituloOtro by remember { mutableStateOf(false) } // título escrito a mano

    var expandedVehiculo by remember { mutableStateOf(false) }
    var expandedTitulo by remember { mutableStateOf(false) }
    var expandedTipo by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    // "Vencido" ya no se asigna a mano: se calcula automáticamente al pasar la fecha.
    val estados = listOf("Pendiente", "Realizado")

    val tipoVehiculoSel = vehiculos.find { it.idVehiculo == idVehiculoSeleccionado }?.tipoVehiculo ?: ""
    val titulosDisponibles = titulosPorTipoVehiculo(tipoVehiculoSel)

    LaunchedEffect(idMantenimiento) {
        if (idMantenimiento != null) mantenimientoViewModel.cargarDetalle(idMantenimiento)
    }

    LaunchedEffect(uiState.mantenimientoSeleccionado) {
        if (idMantenimiento != null) {
            uiState.mantenimientoSeleccionado?.let { m ->
                titulo = m.titulo
                tituloOtro = m.titulo.isNotBlank() && !esTituloPredefinido(m.titulo)
                descripcion = m.descripcion
                tipoMantenimiento = m.tipoMantenimiento
                fechaProgramada = m.fechaProgramada
                // "Vencido" antiguo se trata como "Pendiente" (ahora es automático)
                estado = if (m.estado == "Vencido") "Pendiente" else m.estado
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
                // Título: selector de mantenimientos comunes según el vehículo
                when {
                    idVehiculoSeleccionado == null -> {
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            enabled = false,
                            readOnly = true,
                            label = { Text("Título") },
                            placeholder = { Text("Selecciona primero el vehículo") },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    tituloOtro -> {
                        OutlinedTextField(
                            value = titulo,
                            onValueChange = { titulo = it },
                            label = { Text("Título") },
                            placeholder = { Text("Escribe el mantenimiento") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            trailingIcon = {
                                IconButton(onClick = { tituloOtro = false; titulo = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Elegir de la lista")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    else -> {
                        ExposedDropdownMenuBox(
                            expanded = expandedTitulo,
                            onExpandedChange = { expandedTitulo = it }
                        ) {
                            OutlinedTextField(
                                value = titulo,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Título") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedTitulo) },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedTitulo,
                                onDismissRequest = { expandedTitulo = false }
                            ) {
                                titulosDisponibles.forEach { t ->
                                    DropdownMenuItem(
                                        text = { Text(t) },
                                        onClick = { titulo = t; expandedTitulo = false }
                                    )
                                }
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text(TITULO_OTRO, fontWeight = FontWeight.SemiBold) },
                                    leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                                    onClick = { tituloOtro = true; titulo = ""; expandedTitulo = false }
                                )
                            }
                        }
                    }
                }
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
                        leadingIcon = if (tipoMantenimiento.isNotBlank()) {
                            {
                                Icon(
                                    iconoTipoMantenimiento(tipoMantenimiento),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else null,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedTipo) },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedTipo,
                        onDismissRequest = { expandedTipo = false }
                    ) {
                        TIPOS_MANTENIMIENTO.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion.nombre) },
                                leadingIcon = {
                                    Icon(
                                        opcion.icono,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                onClick = {
                                    tipoMantenimiento = opcion.nombre
                                    expandedTipo = false
                                }
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
                Text(
                    "El estado «Vencido» se marca solo cuando pasa la fecha programada.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )

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

            val guardarInteraction = remember { MutableInteractionSource() }
            val haptics = LocalHapticFeedback.current
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
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        if (idMantenimiento == null) {
                            // Nuevo: esperamos el ID real generado por Room para el recordatorio
                            mantenimientoViewModel.guardarMantenimiento(m) { nuevoId ->
                                if (recordatorioActivo) {
                                    programarRecordatorio(
                                        context = context,
                                        titulo = m.titulo,
                                        idMantenimiento = nuevoId,
                                        fechaProgramada = m.fechaProgramada
                                    )
                                }
                            }
                        } else {
                            // Edición: el ID ya se conoce
                            mantenimientoViewModel.editarMantenimiento(m)
                            if (recordatorioActivo) {
                                programarRecordatorio(
                                    context = context,
                                    titulo = m.titulo,
                                    idMantenimiento = m.idMantenimiento,
                                    fechaProgramada = m.fechaProgramada
                                )
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .pressScale(guardarInteraction),
                enabled = titulo.isNotBlank()
                        && idVehiculoSeleccionado != null
                        && fechaProgramada.isNotEmpty()
                        && tipoMantenimiento.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                interactionSource = guardarInteraction
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
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

/**
 * Programa un OneTimeWorkRequest que disparará el recordatorio en la fecha indicada.
 * El delay se calcula desde ahora hasta el inicio del día de [fechaProgramada];
 * si la fecha ya pasó (o es hoy) se dispara cuanto antes.
 */
private fun programarRecordatorio(
    context: android.content.Context,
    titulo: String,
    idMantenimiento: Int,
    fechaProgramada: String
) {
    // Para poder probar HOY sin esperar: si la fecha es hoy o ya pasó,
    // la notificación se dispara en 10 segundos. Si es futura, delay exacto.
    val pruebaMillis = 10_000L
    val delayMillis = try {
        val fecha = LocalDate.parse(fechaProgramada)
        val hoy = LocalDate.now()
        if (!fecha.isAfter(hoy)) {
            pruebaMillis
        } else {
            val triggerMillis = fecha
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
            (triggerMillis - System.currentTimeMillis()).coerceAtLeast(pruebaMillis)
        }
    } catch (e: Exception) {
        pruebaMillis
    }

    val datos = workDataOf(
        RecordatorioWorker.KEY_TITULO to titulo,
        RecordatorioWorker.KEY_ID_MANTENIMIENTO to idMantenimiento
    )

    val request = OneTimeWorkRequestBuilder<RecordatorioWorker>()
        .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
        .setInputData(datos)
        .build()

    WorkManager.getInstance(context).enqueue(request)
}
