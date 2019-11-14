package com.tadiuzzz.timerwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.RemoteViews
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Implementation of App Widget functionality.
 */
class NewAppWidget : AppWidgetProvider() {

    val defaultTime: Long = 10000
    lateinit var prefs: SharedPreferences

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        prefs = context.getSharedPreferences("prefs", 0)
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, getTimeValue(appWidgetId))
        }
    }

    fun getTimeValue(widgetId: Int): Long {
        if (!prefs.contains(PREF_TIME_SETTINGS + widgetId))
            with(prefs.edit()) {
                putLong(PREF_TIME_SETTINGS + widgetId, defaultTime)
                apply()
            }

        return prefs.getLong(PREF_TIME_SETTINGS + widgetId, defaultTime)

    }

    override fun onAppWidgetOptionsChanged(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {
        private val defaultPlusTime: Long = 1000L
        private val defaultMinusTime: Long = -1000L

        internal fun updateAppWidget(
            context: Context, appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            time: Long
        ) {

            val views = RemoteViews(context.packageName, R.layout.widget_timer)

            views.setOnClickPendingIntent(
                R.id.btn_plus,
                getSetTimeIntent(context, appWidgetId, defaultPlusTime)
            )
            views.setOnClickPendingIntent(
                R.id.btn_minus,
                getSetTimeIntent(context, appWidgetId, defaultMinusTime)
            )
            views.setOnClickPendingIntent(
                R.id.btn_start_stop,
                getStartTimerIntent(context, appWidgetId)
            )

            views.setTextViewText(R.id.tv_timer, getTime(time))

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun getStartTimerIntent(context: Context, appWidgetId: Int): PendingIntent {
            val intent = Intent(context, MyIntentService::class.java).apply {
                action = ACTION_START_TIMER
                putExtra(EXTRA_WIDGET_ID, appWidgetId)
            }
            val pendingIntent = PendingIntent.getService(
                context,
                appWidgetId,
                intent,
                0
            )
            return pendingIntent
        }

        private fun getSetTimeIntent(
            context: Context,
            appWidgetId: Int,
            plusTime: Long
        ): PendingIntent {

            val actionConst = if (plusTime >= 0) ACTION_PLUS_TIME else ACTION_MINUS_TIME

            val intent = Intent(context, MyIntentService::class.java).apply {
                action = actionConst
                putExtra(EXTRA_PLUS_TIME, plusTime)
                putExtra(EXTRA_WIDGET_ID, appWidgetId)
            }
            val pendingIntent = PendingIntent.getService(
                context,
                appWidgetId,
                intent,
                0
            )
            return pendingIntent
        }

        private fun getTime(time: Long): String {
            val textTime = String.format(
                Locale.getDefault(), "%02d : %02d",
                TimeUnit.MILLISECONDS.toMinutes(time) % 60,
                TimeUnit.MILLISECONDS.toSeconds(time) % 60
            )
            return textTime
        }

    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent?.action.equals(ACTION_PLUS_TIME)) {
            val intent = Intent()
        }
    }

}

