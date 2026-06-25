package com.auto_care_test.ui.vehiculo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.auto_care_test.domain.model.Vehiculo
import com.auto_care_test.ui.common.HeaderGradient
import com.auto_care_test.ui.common.MARCA_OTRA
import com.auto_care_test.ui.common.ShimmerBox
import com.auto_care_test.ui.common.TIPOS_VEHICULO
import com.auto_care_test.ui.common.iconoTipoVehiculo
import com.auto_care_test.ui.common.marcasPorTipo
import com.auto_care_test.ui.common.modelosPorMarca
import com.auto_care_test.ui.common.pressScale
import com.auto_care_test.viewmodel.VehiculoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehiculosScreen(
    viewModel: VehiculoViewModel,
    onNavigateBack: () -> Unit
) {
    val vehiculos by viewModel.vehiculos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showForm by remember { mutableStateOf(false) }
    var tipoVehiculo by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var placa by remember { mutableStateOf("") }
    var marcaOtra by remember { mutableStateOf(false) } // marca escrita a mano (fuera de la lista)
    var expandedTipo by remember { mutableStateOf(false) }
    var expandedMarca by remember { mutableStateOf(false) }
    var expandedModelo by remember { mutableStateOf(false) }

    val haptics = LocalHapticFeedback.current
    val fabInteraction = remember { MutableInteractionSource() }
    val guardarInteraction = remember { MutableInteractionSource() }

    val esOtros = tipoVehiculo == "Otros"
    val marcasDisponibles = marcasPorTipo(tipoVehiculo)
    val modelosDisponibles = modelosPorMarca(tipoVehiculo, marca)

    fun limpiarFormulario() {
        tipoVehiculo = ""
        marca = ""
        modelo = ""
        placa = ""
        marcaOtra = false
    }

    Scaffold(
        topBar = {
            Box(modifier = Modifier.background(HeaderGradient)) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                "Tus Vehículos",
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                "${vehiculos.size} registrados",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
                            )
                        }
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    showForm = !showForm
                },
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                interactionSource = fabInteraction,
                modifier = Modifier.pressScale(fabInteraction)
            ) {
                Icon(
                    imageVector = if (showForm) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = if (showForm) "Cerrar" else "Agregar"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            AnimatedVisibility(
                visible = showForm,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                "Agrega tu vehículo",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))

                        // 1) Tipo de vehículo (define qué marcas se muestran)
                        ExposedDropdownMenuBox(
                            expanded = expandedTipo,
                            onExpandedChange = { expandedTipo = it }
                        ) {
                            OutlinedTextField(
                                value = tipoVehiculo,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Tipo de vehículo") },
                                leadingIcon = if (tipoVehiculo.isNotBlank()) {
                                    {
                                        Icon(
                                            iconoTipoVehiculo(tipoVehiculo),
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
                                TIPOS_VEHICULO.forEach { opcion ->
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
                                            tipoVehiculo = opcion.nombre
                                            // Al cambiar el tipo, reiniciamos marca y modelo
                                            marca = ""
                                            modelo = ""
                                            marcaOtra = opcion.nombre == "Otros"
                                            expandedTipo = false
                                        }
                                    )
                                }
                            }
                        }

                        // 2) Marca: depende del tipo elegido
                        when {
                            tipoVehiculo.isBlank() -> {
                                OutlinedTextField(
                                    value = "",
                                    onValueChange = {},
                                    enabled = false,
                                    readOnly = true,
                                    label = { Text("Marca") },
                                    placeholder = { Text("Elige primero el tipo") },
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            // "Otros" o marca fuera de la lista → texto libre
                            esOtros || marcaOtra -> {
                                OutlinedTextField(
                                    value = marca,
                                    onValueChange = { marca = it },
                                    label = { Text("Marca") },
                                    placeholder = { Text("Escribe la marca") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(14.dp),
                                    trailingIcon = if (!esOtros) {
                                        {
                                            IconButton(onClick = {
                                                marcaOtra = false
                                                marca = ""
                                                modelo = ""
                                            }) {
                                                Icon(Icons.Default.Close, contentDescription = "Elegir de la lista")
                                            }
                                        }
                                    } else null,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            // Auto / Moto / SUV → dropdown de marcas + opción "Otra"
                            else -> {
                                ExposedDropdownMenuBox(
                                    expanded = expandedMarca,
                                    onExpandedChange = { expandedMarca = it }
                                ) {
                                    OutlinedTextField(
                                        value = marca,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Marca") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedMarca) },
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                            .fillMaxWidth()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = expandedMarca,
                                        onDismissRequest = { expandedMarca = false }
                                    ) {
                                        marcasDisponibles.forEach { m ->
                                            DropdownMenuItem(
                                                text = { Text(m) },
                                                onClick = {
                                                    marca = m
                                                    modelo = ""
                                                    expandedMarca = false
                                                }
                                            )
                                        }
                                        HorizontalDivider()
                                        DropdownMenuItem(
                                            text = { Text(MARCA_OTRA, fontWeight = FontWeight.SemiBold) },
                                            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                                            onClick = {
                                                marcaOtra = true
                                                marca = ""
                                                modelo = ""
                                                expandedMarca = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // 3) Modelo: dropdown si la marca tiene catálogo (autos), si no campo libre
                        if (modelosDisponibles != null) {
                            ExposedDropdownMenuBox(
                                expanded = expandedModelo,
                                onExpandedChange = { expandedModelo = it }
                            ) {
                                OutlinedTextField(
                                    value = modelo,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Modelo") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedModelo) },
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                        .fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedModelo,
                                    onDismissRequest = { expandedModelo = false }
                                ) {
                                    modelosDisponibles.forEach { mod ->
                                        DropdownMenuItem(
                                            text = { Text(mod) },
                                            onClick = {
                                                modelo = mod
                                                expandedModelo = false
                                            }
                                        )
                                    }
                                }
                            }
                        } else {
                            OutlinedTextField(
                                value = modelo,
                                onValueChange = { modelo = it },
                                label = { Text("Modelo") },
                                placeholder = { Text("Escribe el modelo") },
                                enabled = marca.isNotBlank(),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // 4) Placa
                        OutlinedTextField(
                            value = placa,
                            onValueChange = { placa = it.uppercase() },
                            label = { Text("Placa") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                val nuevoVehiculo = Vehiculo(
                                    marca = marca.trim(),
                                    modelo = modelo.trim(),
                                    placa = placa.trim(),
                                    tipoVehiculo = tipoVehiculo
                                )
                                viewModel.agregarVehiculo(nuevoVehiculo)
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                limpiarFormulario()
                                showForm = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .pressScale(guardarInteraction),
                            enabled = tipoVehiculo.isNotBlank()
                                    && marca.isNotBlank()
                                    && modelo.isNotBlank()
                                    && placa.isNotBlank(),
                            shape = RoundedCornerShape(14.dp),
                            interactionSource = guardarInteraction
                        ) {
                            Text("Agregar vehículo", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            when {
                isLoading -> Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(3) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            ShimmerBox(modifier = Modifier.size(50.dp), shape = RoundedCornerShape(50))
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                ShimmerBox(modifier = Modifier.width(140.dp).height(16.dp))
                                ShimmerBox(modifier = Modifier.width(90.dp).height(12.dp))
                            }
                        }
                    }
                }

                vehiculos.isEmpty() && !showForm -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.DirectionsCar,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                        Text(
                            "Sin vehículos registrados",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Text(
                            "Pulsa + para agregar el primero",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                        )
                    }
                }

                else -> LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = if (showForm) 4.dp else 12.dp,
                        bottom = 88.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(vehiculos, key = { _, v -> v.idVehiculo }) { index, v ->
                        var visible by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) { visible = true }
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(250, delayMillis = index * 40)) +
                                slideInVertically(
                                    animationSpec = tween(250, delayMillis = index * 40),
                                    initialOffsetY = { it / 4 }
                                )
                        ) {
                            VehiculoCard(
                                vehiculo = v,
                                onDelete = { viewModel.eliminarVehiculo(v.idVehiculo) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VehiculoCard(vehiculo: Vehiculo, onDelete: () -> Unit) {
    var showConfirm by remember { mutableStateOf(false) }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Eliminar vehículo") },
            text = {
                Text("¿Eliminar ${vehiculo.marca} ${vehiculo.modelo}? Se eliminarán también sus mantenimientos.")
            },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showConfirm = false
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) { Text("Cancelar") }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
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
                    iconoTipoVehiculo(vehiculo.tipoVehiculo),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                    tint = Color.White
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${vehiculo.marca} ${vehiculo.modelo}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${vehiculo.placa}  ·  ${vehiculo.tipoVehiculo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                )
            }
            IconButton(onClick = { showConfirm = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
    }
}
