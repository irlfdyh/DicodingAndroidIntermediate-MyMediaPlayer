package com.dicoding.intermediate.media.mymediaplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private const val POST_NOTIFICATION = android.Manifest.permission.POST_NOTIFICATIONS
    }

    private var serviceMessenger: Messenger? = null
    private lateinit var boundServiceIntent: Intent
    private var serviceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            serviceMessenger = Messenger(service)
            serviceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceMessenger = null
            serviceBound = false
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                Toast.makeText(this, "Akses notifikasi telah diberikan", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Akses notifikasi telah ditolak", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= 33) {
            setupNotification()
        }

        val btnPlay = findViewById<Button>(R.id.btn_play)
        val btnStop = findViewById<Button>(R.id.btn_stop)

        btnPlay.setOnClickListener {
            if (serviceBound) {
                try {
                    serviceMessenger?.send(Message.obtain(null, MediaService.PLAY, 0, 0))
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
        }

        btnStop.setOnClickListener {
            if (serviceBound) {
                try {
                    serviceMessenger?.send(Message.obtain(null, MediaService.STOP, 0, 0))
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
        }

        boundServiceIntent = Intent(this@MainActivity, MediaService::class.java)
        boundServiceIntent.action = MediaService.ACTION_CREATE

        startService(boundServiceIntent)
        bindService(boundServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        unbindService(serviceConnection)
        boundServiceIntent.action = MediaService.ACTION_DESTROY

        startService(boundServiceIntent)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupNotification() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                POST_NOTIFICATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Already granted
            }
            shouldShowRequestPermissionRationale(POST_NOTIFICATION) -> {
                // Show rationale message
            }
            else -> {
                if (Build.VERSION.SDK_INT >= 33) {
                    requestPermissionLauncher.launch(POST_NOTIFICATION)
                }
            }
        }
    }

}