package com.auto_care_test.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auto_care_test.data.repository.AutoCareRepository
import com.auto_care_test.domain.model.Mantenimiento
import com.auto_care_test.domain.model.Vehiculo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MantenimientoUiState(
    val isLoading: Boolean = false,
    val mantenimientos: List<Mantenimiento> = emptyList(),
    val mantenimientoSeleccionado: Mantenimiento? = null,
    val vehiculoAsociado: Vehiculo? = null,
    val mensajeError: String? = null,
    val consejoApi: String? = null,
    val guardadoExitoso: Boolean = false
)

class MantenimientoViewModel(private val repository: AutoCareRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(MantenimientoUiState())
    val uiState: StateFlow<MantenimientoUiState> = _uiState.asStateFlow()

    init {
        cargarMantenimientos()
    }

    fun cargarMantenimientos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.obtenerMantenimientos().collect { lista ->
                    _uiState.update { it.copy(isLoading = false, mantenimientos = lista) }
                }
            } catch (e: IllegalStateException) {
                // No hay sesión activa todavía; se reintentará tras iniciar sesión.
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun cargarDetalle(idMantenimiento: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, mensajeError = null) }
            // Buscamos primero en la lista ya cargada (Firestore en memoria) y,
            // como respaldo, en la caché local de Room.
            val mantenimiento = _uiState.value.mantenimientos.find { it.idMantenimiento == idMantenimiento }
                ?: repository.getMantenimientoById(idMantenimiento)
            if (mantenimiento != null) {
                _uiState.update { it.copy(isLoading = false, mantenimientoSeleccionado = mantenimiento) }
                val vehiculo = repository.getVehiculoById(mantenimiento.idVehiculo)
                if (vehiculo != null) {
                    _uiState.update { it.copy(vehiculoAsociado = vehiculo) }
                    cargarDatosTecnicos(vehiculo.marca, vehiculo.modelo)
                }
            } else {
                _uiState.update { it.copy(isLoading = false, mensajeError = "Mantenimiento no encontrado") }
            }
        }
    }

    fun guardarMantenimiento(m: Mantenimiento) {
        viewModelScope.launch {
            try {
                repository.insertMantenimiento(m)
                _uiState.update { it.copy(guardadoExitoso = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(mensajeError = "Error al guardar") }
            }
        }
    }

    fun editarMantenimiento(m: Mantenimiento) {
        viewModelScope.launch {
            try {
                repository.updateMantenimiento(m)
                _uiState.update { it.copy(guardadoExitoso = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(mensajeError = "Error al editar") }
            }
        }
    }

    fun eliminarMantenimiento(id: Int) {
        viewModelScope.launch {
            val m = repository.getMantenimientoById(id)
            if (m != null) {
                repository.deleteMantenimiento(m)
            }
        }
    }

    private suspend fun cargarDatosTecnicos(marca: String, modelo: String) {
        try {
            Log.d("API_DEBUG", "Consultando API → make='$marca' model='$modelo'")
            val consejo = repository.obtenerDatosTecnicos(marca, modelo)
            Log.d("API_DEBUG", "Respuesta OK: $consejo")
            _uiState.update { it.copy(consejoApi = consejo) }
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error al cargar datos técnicos: ${e.message}", e)
            // Se guarda en consejoApi (NO en mensajeError) para no tapar el detalle del mantenimiento
            _uiState.update { it.copy(consejoApi = "No se pudo cargar información técnica: ${e.message}") }
        }
    }

    fun resetGuardado() {
        _uiState.update { it.copy(guardadoExitoso = false) }
    }
}
