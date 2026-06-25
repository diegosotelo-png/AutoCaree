package com.auto_care_test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.auto_care_test.data.local.database.AutoCareDatabase
import com.auto_care_test.data.remote.api.RetrofitClient
import com.auto_care_test.data.repository.AutoCareRepository
import com.auto_care_test.ui.navigation.NavGraph
import com.auto_care_test.ui.theme.AutocaretestTheme
import com.auto_care_test.viewmodel.AuthViewModel
import com.auto_care_test.viewmodel.MantenimientoViewModel
import com.auto_care_test.viewmodel.VehiculoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. Inicialización de la base de datos
        val database = AutoCareDatabase.getDatabase(this)
        
        // 2. Inicialización del Repositorio
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
                
                // 4. Obtención de ViewModels usando el Factory
                val mantenimientoViewModel: MantenimientoViewModel = viewModel(factory = viewModelFactory)
                val vehiculoViewModel: VehiculoViewModel = viewModel(factory = viewModelFactory)
                val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)

                // 5. Configuración de la Navegación
                NavGraph(
                    navController = navController,
                    mantenimientoViewModel = mantenimientoViewModel,
                    vehiculoViewModel = vehiculoViewModel,
                    authViewModel = authViewModel
                )
            }
        }
    }
}
