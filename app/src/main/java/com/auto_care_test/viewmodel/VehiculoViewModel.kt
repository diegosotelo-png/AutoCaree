package com.auto_care_test.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auto_care_test.data.repository.AutoCareRepository
import com.auto_care_test.domain.model.Vehiculo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VehiculoViewModel(private val repository: AutoCareRepository) : ViewModel() {

    private val _vehiculos = MutableStateFlow<List<Vehiculo>>(emptyList())
    val vehiculos: StateFlow<List<Vehiculo>> = _vehiculos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _consejoApi = MutableStateFlow<String?>(null)
    val consejoApi: StateFlow<String?> = _consejoApi.asStateFlow()

    init {
        cargarVehiculos()
    }

    fun cargarVehiculos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.obtenerVehiculos().collect { lista ->
                    _vehiculos.value = lista
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                // Sin sesión o error de Firestore (p. ej. al cerrar sesión): no crashear.
                _vehiculos.value = emptyList()
                _isLoading.value = false
            }
        }
    }

    fun agregarVehiculo(v: Vehiculo) {
        viewModelScope.launch {
            repository.insertVehiculo(v)
        }
    }

    fun eliminarVehiculo(id: Int) {
        viewModelScope.launch {
            repository.deleteVehiculo(id)
        }
    }

    fun obtenerDatosTecnicos(marca: String, modelo: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val resultado = repository.obtenerDatosTecnicos(marca, modelo)
                _consejoApi.value = resultado
            } catch (e: Exception) {
                _consejoApi.value = "No se pudo cargar información técnica del vehículo."
            } finally {
                _isLoading.value = false
            }
        }
    }
}
