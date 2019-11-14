package com.tadiuzzz.timerwidget

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.CountDownTimer
import android.os.IBinder


import android.app.Notification
import android.content.Context
import android.media.AudioAttributes
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlin.collections.HashSet
import android.media.SoundPool
import android.os.Handler


const val PREF_TIME_SETTINGS = "Pref_time_settings:"

const val ACTION_START_TIMER = "com.tadiuzzz.timerwidget.action.ACTION_START_TIMER"
const val ACTION_PLUS_TIME = "com.tadiuzzz.timerwidget.action.ACTION_PLUS_TIME"
const val ACTION_MINUS_TIME = "com.tadiuzzz.timerwidget.action.ACTION_MINUS_TIME"

const val EXTRA_PLUS_TIME = "com.tadiuzzz.timerwidget.extra.PLUS_TIME"
const val EXTRA_WIDGET_ID = "com.tadiuzzz.timerwidget.extra.WIDGET_ID"

class MyIntentService : Service() {

    lateinit var prefs: SharedPreferences
//    lateinit var soundPoolPlayer: SoundPoolPlayer
//    lateinit var mediaPlayer: MediaPlayer
    private val runningServiceIds = HashSet<Int>()
    var soundId: Int = 0

    override fun onCreate() {
        prefs = applicationContext.getSharedPreferences("prefs", 0)
//        soundPoolPlayer = SoundPoolPlayer(applicationContext)
//        soundPoolPlayer.loadSound()
//        mediaPlayer = MediaPlayer.create(this, R.raw.bell)
//        mediaPlayer.isLooping = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {
            ACTION_START_TIMER -> {
                val widgetId = intent.getIntExtra(EXTRA_WIDGET_ID, 0)
                if (!checkIsRunning(widgetId)) {
                    runningServiceIds.add(widgetId)
                    val time = prefs.getLong(PREF_TIME_SETTINGS + widgetId, 0)
                    val notification = getNotification()
                    startTimer(time, widgetId)
                    startForeground(widgetId, notification)
                }
            }
            ACTION_PLUS_TIME, ACTION_MINUS_TIME -> {
                val widgetId = intent.getIntExtra(EXTRA_WIDGET_ID, 0)
                if (!checkIsRunning(widgetId)) {
                    val plusTime = intent.getLongExtra(EXTRA_PLUS_TIME, 0)
                    updateSettingsOfWidget(widgetId, plusTime)
                    updateWidget(prefs.getLong(PREF_TIME_SETTINGS + widgetId, 0), widgetId)
                }
            }
        }
        return START_STICKY
    }

    private fun checkIsRunning(widgetId: Int): Boolean {
        Log.d("TAG", runningServiceIds.toString())
        return runningServiceIds.contains(widgetId)
    }

    private fun updateSettingsOfWidget(widgetId: Int, plusTime: Long) {
        val timeSettings = getTimeSettingsOfWidget(widgetId) + plusTime
        with (prefs.edit()) {
            putLong(PREF_TIME_SETTINGS + widgetId, timeSettings)
            apply()
        }
    }

    private fun getTimeSettingsOfWidget(widgetId: Int): Long {
        val timeSettings = prefs.getLong(PREF_TIME_SETTINGS + widgetId, 0)
        return timeSettings
    }

    private fun startTimer(time: Long, widgetId: Int) {
        val timer = object: CountDownTimer(time, 100) {
            override fun onTick(millisUntilFinished: Long) {
                updateWidget(millisUntilFinished, widgetId)
            }

            override fun onFinish() {
//                mediaPlayer.start()
//                soundPoolPlayer.playSound()
                val soundPool = getSoundPool()
                loadSound(soundPool, applicationContext)
                soundPool.setOnLoadCompleteListener { soundPool, sampleId, status ->
                    playSound(soundPool, applicationContext)



                }

                Handler().postDelayed({
                    clearSoundPool(soundPool)
                }, 3000)


                runningServiceIds.remove(widgetId)
                updateWidget(time, widgetId)
                if (runningServiceIds.isEmpty()) {
//                    clearSoundPool(soundPool)
                    stopSelf()
                }
            }

        }
        timer.start()
    }

    private fun getNotification(): Notification {
        return NotificationCompat.Builder(applicationContext, "timer")
            .setContentTitle("Таймер")
            .setSmallIcon(R.drawable.ic_timer_black_24dp)
            .setContentText("Таймер запущен")
            .setOngoing(true).build()
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    private fun updateWidget(time: Long, widgetId: Int) {
        val appWidgetManager = AppWidgetManager.getInstance(this)

        NewAppWidget.updateAppWidget(this, appWidgetManager, widgetId, time)
    }

    val audioAttributes: AudioAttributes = AudioAttributes.Builder()
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .build()

    fun getSoundPool(): SoundPool {
        val soundPool: SoundPool = SoundPool.Builder()
            .setAudioAttributes(audioAttributes)
            .setMaxStreams(4)
            .build()
        return soundPool;
    }

    fun loadSound(soundPool: SoundPool, context: Context) {
        soundId = soundPool.load(context, R.raw.bell, 1)
    }

    fun playSound(soundPool: SoundPool, context: Context) {
//        soundPool.setOnLoadCompleteListener(SoundPool.OnLoadCompleteListener { soundPool, sampleId, status ->  soundPool.play(soundId, 1.0f, 1.0f, 1,0, 1.0f)})
        if (soundId != 0)
            soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
    }

    fun clearSoundPool(soundPool: SoundPool) {
        soundPool.unload(soundId)
        soundPool.release()
    }





}
