package com.tadiuzzz.timerwidget

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool


/**
 * Created by Simonov.vv on 13.11.2019.
 */
class SoundPoolPlayer(val context: Context) {
    val audioAttributes: AudioAttributes = AudioAttributes.Builder()
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .build()

    var soundPool: SoundPool = SoundPool.Builder()
        .setAudioAttributes(audioAttributes)
        .setMaxStreams(4)
        .build()

    var soundId: Int = 0

    fun loadSound() {
        soundId = soundPool.load(context, R.raw.bell, 1)
    }

    fun playSound() {
//        soundPool.setOnLoadCompleteListener(SoundPool.OnLoadCompleteListener { soundPool, sampleId, status ->  soundPool.play(soundId, 1.0f, 1.0f, 1,0, 1.0f)})
        if (soundId != 0)
            soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
    }

    fun clear() {
        soundPool.unload(soundId)
        soundPool.release()
    }


}