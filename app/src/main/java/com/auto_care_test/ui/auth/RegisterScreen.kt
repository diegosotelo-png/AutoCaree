package com.auto_care_test.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.auto_care_test.ui.common.HeaderGradient
import com.auto_care_test.ui.theme.NeonCyan
import com.auto_care_test.ui.theme.NeonViolet
import com.auto_care_test.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var camposVaciosError by remember { mutableStateOf<String?>(null) }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible = true }
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) onRegisterSuccess()
    }

    val errorMostrado = camposVaciosError ?: uiState.error
    val formularioValido = !uiState.isLoading

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.TopStart)
                .offset(x = (-90).dp, y = (-70).dp)
                .clip(CircleShape)
                .background(NeonCyan.copy(alpha = 0.18f))
                .blur(80.dp)
        )
        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 70.dp, y = 60.dp)
                .clip(CircleShape)
                .background(NeonViolet.copy(alpha = 0.25f))
                .blur(80.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 4 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(HeaderGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        "Crear cuenta",
                        style = MaterialTheme.typography.displaySmall.copy(brush = HeaderGradient),
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Regístrate para empezar a usar AutoCare",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                        ),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedTextField(
                                value = nombre,
                                onValueChange = { nombre = it; camposVaciosError = null },
                                label = { Text("Nombre") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it; camposVaciosError = null },
                                label = { Text("Correo electrónico") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it; camposVaciosError = null },
                                label = { Text("Contraseña") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                singleLine = true,
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                trailingIcon = {
                                    val icon: ImageVector =
                                        if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(icon, contentDescription = null)
                                    }
                                },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            AnimatedVisibility(visible = errorMostrado != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.12f))
                                        .padding(12.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = errorMostrado ?: "",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    camposVaciosError = when {
                                        nombre.isBlank() -> "El nombre no puede estar vacío"
                                        email.isBlank() -> "El correo no puede estar vacío"
                                        password.isBlank() -> "La contraseña no puede estar vacía"
                                        else -> null
                                    }
                                    if (camposVaciosError == null) {
                                        viewModel.register(email.trim(), password, nombre.trim())
                                    }
                                },
                                enabled = formularioValido,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                                contentPadding = PaddingValues()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            if (formularioValido) HeaderGradient
                                            else Brush.linearGradient(
                                                listOf(
                                                    MaterialTheme.colorScheme.outline,
                                                    MaterialTheme.colorScheme.outline
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (uiState.isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(22.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text(
                                            "Registrarse",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "¿Ya tienes cuenta?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        TextButton(onClick = onNavigateToLogin) {
                            Text("Inicia sesión", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
