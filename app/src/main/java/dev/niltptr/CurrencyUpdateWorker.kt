package dev.niltptr

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import retrofit2.Response

class CurrencyUpdateWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        try {
            // Fetch the latest exchange rates for USD
            val response: Response<CurrencyResponse> =
                RetrofitClient.apiService.getLatestRates(base = "USD")

            if (response.isSuccessful) {
                val currencyResponse = response.body()
                // Extract the BRL rate from the response (or "N/A" if missing)
                val rate = currencyResponse?.rates?.get("BRL")?.toString() ?: "N/A"

                // Get all widget instances and update them
                val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
                val widgetComponent = ComponentName(applicationContext, CurrencyWidgetProvider::class.java)
                val widgetIds = appWidgetManager.getAppWidgetIds(widgetComponent)
                for (widgetId in widgetIds) {
                    CurrencyWidgetProvider.updateAppWidget(applicationContext, appWidgetManager, widgetId, rate)
                }
                return Result.success()
            } else {
                // Retry if the API response wasn’t successful
                return Result.retry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.retry()
        }
    }
}
