package com.tadiuzzz.timerwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.*
import android.widget.RemoteViews
import java.util.*


/**
 * Created by Simonov.vv on 05.11.2019.
 */

class TimerWidgetProvider : AppWidgetProvider() {

    var time = Date().time.toString()
    val clickOnTimer = "clickOnTimer"

    private fun updateTimer() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun getPendingSelfIntent(context: Context?, action: String) : PendingIntent {
        val intent = Intent(context, javaClass)
        intent.setAction(action)
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }


    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {

        appWidgetIds?.forEach {


        val thisWidget = ComponentName(context, javaClass)

//        val clickIntent = Intent(context, TimerWidgetProvider::class.java)
//        clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWigetId)

//        val intent = Intent(context, MainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val remoteViews = RemoteViews(context?.packageName, R.layout.widget_timer)

        remoteViews.setOnClickPendingIntent(R.id.tv_timer, getPendingSelfIntent(context, clickOnTimer))
        remoteViews.setTextViewText(R.id.tv_timer, time)

        appWidgetManager?.updateAppWidget(it, remoteViews)
        }

    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.equals(clickOnTimer)) {
            time = Date().time.toString()
        }
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
    }

}