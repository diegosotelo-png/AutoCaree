package com.auto_care_test.data.repository

import android.util.Log
import com.auto_care_test.data.firebase.firestore.FirestoreDataSource
import com.auto_care_test.data.local.dao.MantenimientoDao
import com.auto_care_test.data.local.dao.VehiculoDao
import com.auto_care_test.data.local.entity.MantenimientoEntity
import com.auto_care_test.data.local.entity.VehiculoEntity
import com.auto_care_test.data.remote.api.CarApiService
import com.auto_care_test.domain.model.Mantenimiento
import com.auto_care_test.domain.model.Vehiculo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import retrofit2.HttpException

class AutoCareRepository(
    private val vehiculoDao: VehiculoDao,
    private val mantenimientoDao: MantenimientoDao,
    private val carApiService: CarApiService,
    private val firestoreDataSource: FirestoreDataSource = FirestoreDataSource(),
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private fun uidActual(): String =
        firebaseAuth.currentUser?.uid ?: throw IllegalStateException("No hay un usuario autenticado")

    // --- Vehículos ---
    // Firestore es la fuente de verdad; Room se usa como caché local sincronizada en cada emisión.
    fun obtenerVehiculos(): Flow<List<Vehiculo>> {
        val uid = uidActual()
        return firestoreDataSource.obtenerVehiculos(uid).onEach { vehiculos ->
            vehiculos.forEach { vehiculoDao.insert(it.toEntity()) }
        }
    }

    suspend fun insertVehiculo(vehiculo: Vehiculo) {
        val uid = uidActual()
        val idGenerado = vehiculoDao.insert(vehiculo.toEntity()).toInt()
        val vehiculoConId = vehiculo.copy(idVehiculo = idGenerado)
        firestoreDataSource.agregarVehiculo(uid, vehiculoConId)
    }

    suspend fun deleteVehiculo(id: Int) {
        val uid = uidActual()
        vehiculoDao.deleteById(id)
        firestoreDataSource.eliminarVehiculo(uid, id)
    }

    suspend fun getVehiculoById(id: Int): Vehiculo? {
        return vehiculoDao.getById(id)?.toDomain()
    }

    // --- Mantenimientos ---
    fun obtenerMantenimientos(): Flow<List<Mantenimiento>> {
        val uid = uidActual()
        return firestoreDataSource.obtenerMantenimientos(uid).onEach { mantenimientos ->
            mantenimientos.forEach { mantenimientoDao.insert(it.toEntity()) }
        }
    }

    suspend fun insertMantenimiento(mantenimiento: Mantenimiento) {
        val uid = uidActual()
        val idGenerado = mantenimientoDao.insert(mantenimiento.toEntity()).toInt()
        val mantenimientoConId = mantenimiento.copy(idMantenimiento = idGenerado)
        firestoreDataSource.agregarMantenimiento(uid, mantenimientoConId)
    }

    suspend fun updateMantenimiento(mantenimiento: Mantenimiento) {
        val uid = uidActual()
        mantenimientoDao.update(mantenimiento.toEntity())
        firestoreDataSource.actualizarMantenimiento(uid, mantenimiento)
    }

    suspend fun deleteMantenimiento(mantenimiento: Mantenimiento) {
        val uid = uidActual()
        mantenimientoDao.delete(mantenimiento.toEntity())
        firestoreDataSource.eliminarMantenimiento(uid, mantenimiento.idMantenimiento)
    }

    suspend fun getMantenimientoById(id: Int): Mantenimiento? {
        return mantenimientoDao.getById(id)?.toDomain()
    }

    fun getMantenimientosByVehiculo(idVehiculo: Int): Flow<List<Mantenimiento>> {
        return mantenimientoDao.getByVehiculo(idVehiculo).map { entities ->
            entities.map { it.toDomain() }
        }
    }

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

    private fun VehiculoEntity.toDomain() = Vehiculo(idVehiculo, marca, modelo, placa, tipoVehiculo)
    private fun Vehiculo.toEntity() = VehiculoEntity(idVehiculo, marca, modelo, placa, tipoVehiculo)

    private fun MantenimientoEntity.toDomain() = Mantenimiento(
        idMantenimiento, idVehiculo, titulo, descripcion, tipoMantenimiento, fechaProgramada, estado, recordatorioActivo
    )
    private fun Mantenimiento.toEntity() = MantenimientoEntity(
        idMantenimiento, idVehiculo, titulo, descripcion, tipoMantenimiento, fechaProgramada, estado, recordatorioActivo
    )
}
