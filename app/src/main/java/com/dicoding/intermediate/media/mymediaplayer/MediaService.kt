package com.dicoding.intermediate.media.mymediaplayer

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.IBinder
import java.io.IOException

class MediaService : Service(), MediaPlayerCallback {

    private var isReady: Boolean = false
    private var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onPlay() {
        if (!isReady) {
            mediaPlayer?.prepareAsync()
        } else {
            if (mediaPlayer?.isPlaying as Boolean) {
                mediaPlayer?.pause()
            } else {
                mediaPlayer?.start()
            }
        }
    }

    override fun onStop() {
        if (mediaPlayer?.isPlaying as Boolean || isReady) {
            mediaPlayer?.stop()
            isReady = false
        }
    }

    private fun init() {
        mediaPlayer = MediaPlayer()
        val attribute = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        mediaPlayer?.setAudioAttributes(attribute)

        val afd = applicationContext.resources.openRawResourceFd(R.raw.big_thunder)

        try {
            mediaPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        mediaPlayer?.setOnPreparedListener {
            isReady = true
            mediaPlayer?.start()
        }

        mediaPlayer?.setOnErrorListener { _, _, _ -> false }

    }

}