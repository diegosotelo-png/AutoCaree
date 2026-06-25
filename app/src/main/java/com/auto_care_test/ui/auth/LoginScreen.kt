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
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
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
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible = true }
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) onLoginSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Halos decorativos para dar profundidad sin sobrecargar la pantalla
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.TopEnd)
                .offset(x = 90.dp, y = (-90).dp)
                .clip(CircleShape)
                .background(NeonViolet.copy(alpha = 0.25f))
                .blur(80.dp)
        )
        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-80).dp, y = 60.dp)
                .clip(CircleShape)
                .background(NeonCyan.copy(alpha = 0.18f))
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
                            Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        "AutoCare",
                        style = MaterialTheme.typography.displaySmall.copy(brush = HeaderGradient),
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Inicia sesión para continuar",
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
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Correo electrónico") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
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

                            AnimatedVisibility(visible = uiState.error != null) {
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
                                        text = uiState.error ?: "",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }

                            Button(
                                onClick = { viewModel.login(email.trim(), password) },
                                enabled = !uiState.isLoading && email.isNotBlank() && password.isNotBlank(),
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
                                            if (!uiState.isLoading && email.isNotBlank() && password.isNotBlank())
                                                HeaderGradient
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
                                            "Iniciar sesión",
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
                            "¿No tienes cuenta?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        TextButton(onClick = onNavigateToRegister) {
                            Text("Regístrate", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
