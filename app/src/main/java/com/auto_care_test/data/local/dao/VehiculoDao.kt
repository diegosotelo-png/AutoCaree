package com.auto_care_test.data.local.dao

import androidx.room.*
import com.auto_care_test.data.local.entity.VehiculoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehiculoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vehiculo: VehiculoEntity)

    @Query("SELECT * FROM vehiculos")
    fun getAll(): Flow<List<VehiculoEntity>>

    @Query("SELECT * FROM vehiculos WHERE idVehiculo = :id")
    suspend fun getById(id: Int): VehiculoEntity?

    @Query("DELETE FROM vehiculos WHERE idVehiculo = :id")
    suspend fun deleteById(id: Int)

    @Delete
    suspend fun delete(vehiculo: VehiculoEntity)
}
