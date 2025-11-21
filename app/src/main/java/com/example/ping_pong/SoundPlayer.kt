package com.example.ping_pong

import android.content.Context
import android.media.MediaPlayer

object SoundPlayer {
    fun playPop(context: Context) {
            val popSound = MediaPlayer.create(context, R.raw.pingpongv1)
        popSound.setOnCompletionListener {
            it.release()
        }
        popSound.start()
    }
    fun playPop2(context: Context) {
        val popSound2 = MediaPlayer.create(context, R.raw.pingpongv2)
        popSound2.setOnCompletionListener {
            it.release()
        }
        popSound2.start()
    }
}
