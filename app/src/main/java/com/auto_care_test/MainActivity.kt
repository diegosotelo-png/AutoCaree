package com.auto_care_test

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.auto_care_test.data.local.database.AutoCareDatabase
import com.auto_care_test.data.remote.api.RetrofitClient
import com.auto_care_test.data.repository.AutoCareRepository
import com.auto_care_test.notification.NotificationHelper
import com.auto_care_test.ui.navigation.NavGraph
import com.auto_care_test.ui.navigation.Screen
import com.auto_care_test.ui.theme.AutocaretestTheme
import com.auto_care_test.viewmodel.AuthViewModel
import com.auto_care_test.viewmodel.MantenimientoViewModel
import com.auto_care_test.viewmodel.VehiculoViewModel
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    // Id del mantenimiento que llega al tocar una notificación (deep link).
    private val deepLinkId = mutableStateOf<Int?>(null)

    // Solicitud del permiso de notificaciones (Android 13+).
    private val permisoNotificaciones = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (!concedido) {
            Toast.makeText(
                this,
                "Sin el permiso de notificaciones no podrás recibir los recordatorios de tus mantenimientos.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Notificaciones: canal + permiso + token FCM
        NotificationHelper.crearCanal(this)
        solicitarPermisoNotificaciones()
        logTokenFcm()
        leerDeepLink(intent)

        // 1. Base de datos
        val database = AutoCareDatabase.getDatabase(this)

        // 2. Repositorio
        val repository = AutoCareRepository(
            vehiculoDao = database.vehiculoDao(),
            mantenimientoDao = database.mantenimientoDao(),
            carApiService = RetrofitClient.carApiService
        )

        // 3. Factory para inyectar el repositorio en los ViewModels
        val viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return when {
                    modelClass.isAssignableFrom(MantenimientoViewModel::class.java) ->
                        MantenimientoViewModel(repository) as T
                    modelClass.isAssignableFrom(VehiculoViewModel::class.java) ->
                        VehiculoViewModel(repository) as T
                    modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                        AuthViewModel() as T
                    else -> throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }

        enableEdgeToEdge()
        setContent {
            AutocaretestTheme {
                val navController = rememberNavController()

                // 4. ViewModels
                val mantenimientoViewModel: MantenimientoViewModel = viewModel(factory = viewModelFactory)
                val vehiculoViewModel: VehiculoViewModel = viewModel(factory = viewModelFactory)
                val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)

                // 5. Navegación
                NavGraph(
                    navController = navController,
                    mantenimientoViewModel = mantenimientoViewModel,
                    vehiculoViewModel = vehiculoViewModel,
                    authViewModel = authViewModel
                )

                // 6. Deep link: si llegó un id por notificación y hay sesión, abrir el detalle
                val authState by authViewModel.uiState.collectAsState()
                val pendingId by deepLinkId
                LaunchedEffect(pendingId, authState.isLoggedIn) {
                    val id = pendingId
                    if (id != null && authState.isLoggedIn) {
                        navController.navigate(Screen.Detalle.createRoute(id))
                        deepLinkId.value = null
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        leerDeepLink(intent)
    }

    private fun leerDeepLink(intent: Intent?) {
        val id = intent?.getIntExtra(NotificationHelper.EXTRA_ID_MANTENIMIENTO, -1) ?: -1
        if (id != -1) deepLinkId.value = id
    }

    private fun solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val concedido = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!concedido) {
                permisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun logTokenFcm() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("AutoCareFCM", "Token FCM: ${task.result}")
            } else {
                Log.w("AutoCareFCM", "No se pudo obtener el token FCM", task.exception)
            }
        }
    }
}
