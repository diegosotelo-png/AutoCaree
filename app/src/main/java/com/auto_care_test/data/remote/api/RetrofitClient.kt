package com.auto_care_test.data.remote.api

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.api-ninjas.com/v1/"
    private const val API_KEY = "PXGvpn6kv223tOMvp0w51IlqvdGoet5hxSkOqCyA"


    private val gson = GsonBuilder()
        .registerTypeAdapter(Int::class.javaObjectType, JsonDeserializer<Int?> { json, _, _ ->
            runCatching { json.asInt }.getOrNull()
        })
        .registerTypeAdapter(Double::class.javaObjectType, JsonDeserializer<Double?> { json, _, _ ->
            runCatching { json.asDouble }.getOrNull()
        })
        .create()

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("X-Api-Key", API_KEY)
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val carApiService: CarApiService = retrofit.create(CarApiService::class.java)
}
