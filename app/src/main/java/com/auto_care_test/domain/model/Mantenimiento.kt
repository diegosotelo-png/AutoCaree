package com.auto_care_test.domain.model

data class Mantenimiento(
    val idMantenimiento: Int = 0,
    val idVehiculo: Int = 0,
    val titulo: String = "",
    val descripcion: String = "",
    val tipoMantenimiento: String = "",
    val fechaProgramada: String = "",
    val estado: String = "",
    val recordatorioActivo: Boolean = false
)
