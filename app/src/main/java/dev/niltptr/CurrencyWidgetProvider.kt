package dev.niltptr

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class CurrencyWidgetProvider : AppWidgetProvider() {
    companion object {
        /**
         * Updates the widget with the latest rate.
         *
         * @param context Application context
         * @param appWidgetManager The AppWidgetManager instance
         * @param appWidgetId The specific widget instance ID
         * @param rate The fetched exchange rate as a String (or null if unavailable)
         */
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            rate: String?
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            // Set the currency label and the fetched rate (or a fallback)
            views.setTextViewText(R.id.tvCurrency, "USD → BRL")
            views.setTextViewText(R.id.tvRate, rate ?: "N/A")
            // Update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.d("CurrencyWidgetProvider", "onUpdate called for widget IDs: ${appWidgetIds.joinToString()}")

        // Schedule periodic updates (if not already scheduled)
        scheduleWidgetUpdates(context)

        // Update the widget with a "Fetching..." placeholder
        for (widgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, widgetId, "Fetching...")
        }

        // Enqueue a unique one-time work request so that only one is active at a time
        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "CurrencyUpdateNow",
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequestBuilder<CurrencyUpdateWorker>().build()
            )
        Log.d("CurrencyWidgetProvider", "Enqueued unique one-time CurrencyUpdateWorker")
    }

    override fun onEnabled(context: Context) {
        scheduleWidgetUpdates(context)
    }

    override fun onDisabled(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork("WidgetUpdateWork")
    }

    private fun scheduleWidgetUpdates(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<CurrencyUpdateWorker>(24, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "WidgetUpdateWork",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}