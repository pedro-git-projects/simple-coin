package dev.niltptr

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.frankfurter.dev/v1/"

    val apiService: FrankfurterApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(FrankfurterApiService::class.java)
    }
}