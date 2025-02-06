package dev.niltptr

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import androidx.work.ExistingPeriodicWorkPolicy
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
        // You can trigger an immediate update here if desired.
        // For now, the WorkManager will handle periodic updates.
    }

    override fun onEnabled(context: Context) {
        // Called when the first widget is added.
        scheduleWidgetUpdates(context)
    }

    override fun onDisabled(context: Context) {
        // Called when the last widget is removed.
        WorkManager.getInstance(context).cancelUniqueWork("WidgetUpdateWork")
    }

    private fun scheduleWidgetUpdates(context: Context) {
        // Schedule a periodic worker to update the widget (e.g., every 1 hour)
        val workRequest = PeriodicWorkRequestBuilder<CurrencyUpdateWorker>(1, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "WidgetUpdateWork",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}