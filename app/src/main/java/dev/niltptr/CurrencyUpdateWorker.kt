package dev.niltptr

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import retrofit2.Response
import kotlin.coroutines.cancellation.CancellationException

class CurrencyUpdateWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        try {
            Log.d("CurrencyUpdateWorker", "doWork started")
            // Perform your network call
            val response: Response<CurrencyResponse> =
                RetrofitClient.apiService.getLatestRates(base = "USD")
            Log.d("CurrencyUpdateWorker", "Response code: ${response.code()}")

            if (response.isSuccessful) {
                val currencyResponse = response.body()
                val rate = currencyResponse?.rates?.get("BRL")?.toString() ?: "N/A"
                Log.d("CurrencyUpdateWorker", "Fetched BRL rate: $rate")

                val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
                val widgetComponent =
                    ComponentName(applicationContext, CurrencyWidgetProvider::class.java)
                val widgetIds = appWidgetManager.getAppWidgetIds(widgetComponent)
                for (widgetId in widgetIds) {
                    CurrencyWidgetProvider.updateAppWidget(
                        applicationContext,
                        appWidgetManager,
                        widgetId,
                        rate
                    )
                    Log.d("CurrencyUpdateWorker", "Updated widget $widgetId with rate: $rate")
                }
                return Result.success()
            } else {
                Log.e("CurrencyUpdateWorker", "API response not successful")
                return Result.retry()
            }
        } catch (ce: CancellationException) {
            // Cancellation is expected sometimes – rethrow so that WorkManager handles it correctly.
            Log.d("CurrencyUpdateWorker", "Work was cancelled")
            throw ce
        } catch (e: Exception) {
            Log.e("CurrencyUpdateWorker", "Exception in doWork", e)
            return Result.retry()
        }
    }
}