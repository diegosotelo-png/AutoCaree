package com.auto_care_test.data.remote.api

import com.auto_care_test.data.remote.dto.CarInfoDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CarApiService {
    @GET("cars")
    suspend fun getCars(
        @Query("make") make: String,
        @Query("model") model: String
    ): List<CarInfoDto>
}
