package com.example.smartcaneapp;

import android.app.Service;

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.*

class FallDetectionService : Service() {
    private lateinit var database: FirebaseDatabase
    private lateinit var fallRef: DatabaseReference

    override fun onCreate() {
        super.onCreate()
        database = FirebaseDatabase.getInstance()
        fallRef = database.getReference("fall_detection/Flag")

        createNotificationChannel()
        startForeground(1, getServiceNotification())

        listenForFalls()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "fall_service_channel",
                "Fall Detection Service",
                NotificationManager.IMPORTANCE_MIN
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    private fun getServiceNotification(): Notification {
        return NotificationCompat.Builder(this, "fall_service_channel")
            .setContentTitle("Fall Detection Running")
            .setContentText("Monitoring for fall alerts...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
    }

    private fun listenForFalls() {
        fallRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val fallDetected = snapshot.getValue(String::class.java)

                    if (fallDetected == "true") {
                        showNotification("Fall Alert", "User has fallen! Check immediately.")
                        fallRef.setValue("false") // Reset flag after notifying
                    }
                } catch (e: Exception) {
                    android.util.Log.e("FirebaseError1", "Error parsing fall detection flag", e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("FirebaseError2", "Database error: ${error.message}")
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, "fall_alert_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(this).notify(101, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val isEnabled = sharedPreferences.getBoolean("FallDetectionEnabled", false)

        if (!isEnabled || intent?.action == "STOP_FOREGROUND") {
            stopService()
            return START_NOT_STICKY
        }

        return START_STICKY
    }

    private fun stopService() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
