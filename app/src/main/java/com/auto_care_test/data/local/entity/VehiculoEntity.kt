package com.auto_care_test.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehiculos")
data class VehiculoEntity(
    @PrimaryKey(autoGenerate = true)
    val idVehiculo: Int = 0,
    val marca: String,
    val modelo: String,
    val placa: String,
    val tipoVehiculo: String
)
