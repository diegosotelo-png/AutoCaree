package com.auto_care_test.ui.perfil

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.auto_care_test.domain.model.Vehiculo
import com.auto_care_test.ui.theme.GradientEnd
import com.auto_care_test.ui.theme.GradientStart
import com.auto_care_test.ui.theme.NeonCyan
import com.auto_care_test.ui.theme.NeonGreen
import com.auto_care_test.ui.theme.NeonViolet
import com.auto_care_test.viewmodel.AuthViewModel
import com.auto_care_test.viewmodel.MantenimientoViewModel
import com.auto_care_test.viewmodel.VehiculoViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val HEADER_HEIGHT = 168.dp
private val AVATAR_SIZE = 104.dp

@Composable
fun PerfilScreen(
    viewModel: AuthViewModel,
    vehiculoViewModel: VehiculoViewModel,
    mantenimientoViewModel: MantenimientoViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToVehiculos: () -> Unit = {},
    onLoggedOut: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val vehiculos by vehiculoViewModel.vehiculos.collectAsState()
    val mantenimientoState by mantenimientoViewModel.uiState.collectAsState()
    val currentUser = remember { FirebaseAuth.getInstance().currentUser }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible = true }
    LaunchedEffect(uiState.isLoggedIn) {
        if (!uiState.isLoggedIn) onLoggedOut()
    }

    val miembroDesde = remember(currentUser) {
        currentUser?.metadata?.creationTimestamp?.takeIf { it > 0 }?.let {
            SimpleDateFormat("MMMM yyyy", Locale("es", "ES")).format(Date(it))
                .replaceFirstChar { c -> c.uppercase() }
        }
    }

    val entranceAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(450),
        label = "perfilAlpha"
    )
    val entranceOffset by animateFloatAsState(
        targetValue = if (visible) 0f else 40f,
        animationSpec = tween(450),
        label = "perfilOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Banner + avatar superpuesto (Box no recorta, así el avatar asoma medio bajo el banner)
            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(HEADER_HEIGHT)
                        .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
                        .background(
                            Brush.verticalGradient(listOf(GradientStart, GradientEnd, MaterialTheme.colorScheme.background))
                        )
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .padding(4.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                    Text(
                        "Mi Perfil",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = AVATAR_SIZE / 2)
                        .size(AVATAR_SIZE)
                        .clip(CircleShape)
                        .border(4.dp, MaterialTheme.colorScheme.background, CircleShape)
                        .background(Brush.linearGradient(listOf(GradientStart, GradientEnd))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(52.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AVATAR_SIZE / 2 + 14.dp)
                    .padding(horizontal = 20.dp)
                    .graphicsLayer {
                        alpha = entranceAlpha
                        translationY = entranceOffset
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentUser?.displayName?.takeIf { it.isNotBlank() } ?: "Usuario",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(NeonGreen.copy(alpha = 0.14f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = NeonGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        "Cuenta verificada",
                        style = MaterialTheme.typography.labelMedium,
                        color = NeonGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Mini-stats: da identidad de "dashboard" personal, distinta al Resumen
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MiniStat(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.DirectionsCar,
                        valor = vehiculos.size,
                        label = if (vehiculos.size == 1) "Vehículo" else "Vehículos",
                        tint = NeonViolet
                    )
                    MiniStat(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Build,
                        valor = mantenimientoState.mantenimientos.size,
                        label = "Mantenimientos",
                        tint = NeonCyan
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Datos de cuenta
                SectionTitle("Datos de la cuenta", Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        InfoRow(
                            icon = Icons.Default.Mail,
                            label = "Correo electrónico",
                            value = currentUser?.email ?: "—",
                            tint = NeonCyan
                        )
                        RowDivider()
                        InfoRow(
                            icon = Icons.Default.Badge,
                            label = "Nombre completo",
                            value = currentUser?.displayName?.takeIf { it.isNotBlank() } ?: "Sin registrar",
                            tint = NeonViolet
                        )
                        if (miembroDesde != null) {
                            RowDivider()
                            InfoRow(
                                icon = Icons.Default.CalendarMonth,
                                label = "Miembro desde",
                                value = miembroDesde,
                                tint = NeonGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Mi Garaje — exclusivo del perfil
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionTitle("Mi Garaje")
                    if (vehiculos.isNotEmpty()) {
                        Text(
                            "Ver todos",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable { onNavigateToVehiculos() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (vehiculos.isEmpty()) {
                    GarageEmpty(onNavigateToVehiculos)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        vehiculos.take(3).forEach { v ->
                            GarageRow(v)
                        }
                        if (vehiculos.size > 3) {
                            Text(
                                "+ ${vehiculos.size - 3} más en tu garaje",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToVehiculos() }
                                    .padding(vertical = 6.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                OutlinedButton(
                    onClick = { viewModel.logout() },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar sesión", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
    )
}

@Composable
private fun MiniStat(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    valor: Int,
    label: String,
    tint: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(tint.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            }
            Text(
                "$valor",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String, tint: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(tint.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f),
        modifier = Modifier.padding(horizontal = 18.dp)
    )
}

@Composable
private fun GarageRow(v: Vehiculo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(Brush.linearGradient(listOf(GradientStart, GradientEnd))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.DirectionsCar,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${v.marca} ${v.modelo}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "${v.placa}  ·  ${v.tipoVehiculo}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                )
            }
        }
    }
}

@Composable
private fun GarageEmpty(onNavigateToVehiculos: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToVehiculos() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.DirectionsCar,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Aún no tienes vehículos",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Toca para agregar el primero",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                modifier = Modifier.size(13.dp)
            )
        }
    }
}
