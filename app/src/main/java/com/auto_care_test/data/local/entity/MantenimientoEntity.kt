package com.auto_care_test.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "mantenimientos",
    foreignKeys = [
        ForeignKey(
            entity = VehiculoEntity::class,
            parentColumns = ["idVehiculo"],
            childColumns = ["idVehiculo"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["idVehiculo"])]
)
data class MantenimientoEntity(
    @PrimaryKey(autoGenerate = true)
    val idMantenimiento: Int = 0,
    val idVehiculo: Int,
    val titulo: String,
    val descripcion: String,
    val tipoMantenimiento: String,
    val fechaProgramada: String,
    val estado: String,
    val recordatorioActivo: Boolean
)
