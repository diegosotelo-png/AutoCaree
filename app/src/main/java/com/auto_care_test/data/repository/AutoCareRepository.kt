package com.auto_care_test.data.repository

import android.util.Log
import com.auto_care_test.data.local.dao.MantenimientoDao
import com.auto_care_test.data.local.dao.VehiculoDao
import com.auto_care_test.data.local.entity.MantenimientoEntity
import com.auto_care_test.data.local.entity.VehiculoEntity
import com.auto_care_test.data.remote.api.CarApiService
import com.auto_care_test.domain.model.Mantenimiento
import com.auto_care_test.domain.model.Vehiculo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException

class AutoCareRepository(
    private val vehiculoDao: VehiculoDao,
    private val mantenimientoDao: MantenimientoDao,
    private val carApiService: CarApiService
) {
    // --- Vehículos ---
    val allVehiculos: Flow<List<Vehiculo>> = vehiculoDao.getAll().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun insertVehiculo(vehiculo: Vehiculo) {
        vehiculoDao.insert(vehiculo.toEntity())
    }

    suspend fun deleteVehiculo(id: Int) {
        vehiculoDao.deleteById(id)
    }

    suspend fun getVehiculoById(id: Int): Vehiculo? {
        return vehiculoDao.getById(id)?.toDomain()
    }

    // --- Mantenimientos ---
    val allMantenimientos: Flow<List<Mantenimiento>> = mantenimientoDao.getAll().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun insertMantenimiento(mantenimiento: Mantenimiento) {
        mantenimientoDao.insert(mantenimiento.toEntity())
    }

    suspend fun updateMantenimiento(mantenimiento: Mantenimiento) {
        mantenimientoDao.update(mantenimiento.toEntity())
    }

    suspend fun deleteMantenimiento(mantenimiento: Mantenimiento) {
        mantenimientoDao.delete(mantenimiento.toEntity())
    }

    suspend fun getMantenimientoById(id: Int): Mantenimiento? {
        return mantenimientoDao.getById(id)?.toDomain()
    }

    fun getMantenimientosByVehiculo(idVehiculo: Int): Flow<List<Mantenimiento>> {
        return mantenimientoDao.getByVehiculo(idVehiculo).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // --- API Externa ---
    suspend fun obtenerDatosTecnicos(marca: String, modelo: String): String {
        val makeParam  = marca.trim().lowercase()
        val modelParam = modelo.trim().lowercase()
        Log.d("API_DEBUG", "GET /v1/cars?make=$makeParam&model=$modelParam")
        return try {
            val response = carApiService.getCars(make = makeParam, model = modelParam)
            Log.d("API_DEBUG", "Respuesta: ${response.size} resultados")
            if (response.isNotEmpty()) {
                val car = response[0]
                buildString {
                    car.year?.let          { appendLine("Año: $it") }
                    car.vehicleClass?.let  { appendLine("Clase: ${it.replaceFirstChar { c -> c.uppercase() }}") }
                    car.cylinders?.let     { appendLine("Cilindros: $it") }
                    car.displacement?.let  { appendLine("Desplazamiento: ${it}L") }
                    car.fuelType?.let      { appendLine("Combustible: ${it.replaceFirstChar { c -> c.uppercase() }}") }
                    car.transmission?.let  { appendLine("Transmisión: ${if (it == "a") "Automática" else "Manual"}") }
                    car.drive?.let         { appendLine("Tracción: ${it.uppercase()}") }
                    val c = car.cityMpg; val h = car.highwayMpg
                    if (c != null && h != null) appendLine("MPG: $c ciudad / $h carretera")
                }.trimEnd()
            } else {
                "No se encontró información técnica para este modelo en la base de datos."
            }
        } catch (e: HttpException) {
            Log.e("API_ERROR", "HTTP ${e.code()} — ${e.message()}\nBody: ${e.response()?.errorBody()?.string()}", e)
            throw Exception("Error HTTP ${e.code()}: ${e.message()}")
        } catch (e: Exception) {
            Log.e("API_ERROR", "Excepción de red: ${e.javaClass.simpleName} — ${e.message}", e)
            throw Exception("Error de red: ${e.message}")
        }
    }

    // --- Mappers ---
    private fun VehiculoEntity.toDomain() = Vehiculo(idVehiculo, marca, modelo, placa, tipoVehiculo)
    private fun Vehiculo.toEntity() = VehiculoEntity(idVehiculo, marca, modelo, placa, tipoVehiculo)

    private fun MantenimientoEntity.toDomain() = Mantenimiento(
        idMantenimiento, idVehiculo, titulo, descripcion, tipoMantenimiento, fechaProgramada, estado, recordatorioActivo
    )
    private fun Mantenimiento.toEntity() = MantenimientoEntity(
        idMantenimiento, idVehiculo, titulo, descripcion, tipoMantenimiento, fechaProgramada, estado, recordatorioActivo
    )
}
