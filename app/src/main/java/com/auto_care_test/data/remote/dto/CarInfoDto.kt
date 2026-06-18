package com.auto_care_test.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CarInfoDto(
    val year: Int? = null,
    val cylinders: Int? = null,
    val displacement: Double? = null,
    val drive: String? = null,
    val transmission: String? = null,
    @SerializedName("fuel_type")       val fuelType: String? = null,
    @SerializedName("city_mpg")        val cityMpg: Int? = null,
    @SerializedName("highway_mpg")     val highwayMpg: Int? = null,
    @SerializedName("combination_mpg") val combinationMpg: Int? = null,
    @SerializedName("class")           val vehicleClass: String? = null,
    val make: String? = null,
    val model: String? = null
)
