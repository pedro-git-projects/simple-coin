package dev.niltptr

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FrankfurterApiService {
    @GET("latest")
    suspend fun getLatestRates(
        @Query("base") base: String,
        @Query("amount") amount: Double = 1.0
    ): Response<CurrencyResponse>
}