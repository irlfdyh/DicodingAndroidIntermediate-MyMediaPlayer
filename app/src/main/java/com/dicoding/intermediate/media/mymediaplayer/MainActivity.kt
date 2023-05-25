package com.dicoding.intermediate.media.mymediaplayer

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var isReady: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

        val btnPlay = findViewById<Button>(R.id.btn_play)
        val btnStop = findViewById<Button>(R.id.btn_stop)

        btnPlay.setOnClickListener {
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

        btnStop.setOnClickListener {
            if (mediaPlayer?.isPlaying as Boolean || isReady) {
                mediaPlayer?.stop()
                isReady = false
            }
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