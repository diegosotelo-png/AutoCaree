package com.auto_care_test.data.local.dao

import androidx.room.*
import com.auto_care_test.data.local.entity.MantenimientoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MantenimientoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mantenimiento: MantenimientoEntity)

    @Update
    suspend fun update(mantenimiento: MantenimientoEntity)

    @Delete
    suspend fun delete(mantenimiento: MantenimientoEntity)

    @Query("SELECT * FROM mantenimientos")
    fun getAll(): Flow<List<MantenimientoEntity>>

    @Query("SELECT * FROM mantenimientos WHERE idMantenimiento = :id")
    suspend fun getById(id: Int): MantenimientoEntity?

    @Query("SELECT * FROM mantenimientos WHERE idVehiculo = :idVehiculo")
    fun getByVehiculo(idVehiculo: Int): Flow<List<MantenimientoEntity>>

    @Query("SELECT * FROM mantenimientos WHERE estado = :estado")
    fun getByEstado(estado: String): Flow<List<MantenimientoEntity>>
}
