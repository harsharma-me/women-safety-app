package com.safety.women.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.safety.women.R
import com.safety.women.ui.MainActivity
import com.safety.women.util.PreferencesManager
import kotlin.math.sqrt

/**
 * Background service to detect phone shake and trigger emergency
 */
class ShakeDetectionService : Service(), SensorEventListener {
    
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var preferencesManager: PreferencesManager
    
    private var lastShakeTime: Long = 0
    private var shakeCount = 0
    
    companion object {
        const val NOTIFICATION_ID = 1002
        const val CHANNEL_ID = "shake_detection_channel"
        private const val SHAKE_THRESHOLD_DEFAULT = 15f
        private const val SHAKE_TIME_WINDOW = 2000L // 2 seconds
        private const val SHAKE_COUNT_THRESHOLD = 3 // Number of shakes needed
        private const val SHAKE_COOLDOWN = 5000L // 5 seconds cooldown after triggering
    }
    
    override fun onCreate() {
        super.onCreate()
        preferencesManager = PreferencesManager(this)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        startShakeDetection()
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    private fun startForegroundService() {
        val notification = createNotification()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }
    
    private fun startShakeDetection() {
        accelerometer?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }
    
    private fun stopShakeDetection() {
        sensorManager.unregisterListener(this)
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            
            // Calculate acceleration magnitude
            val acceleration = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH
            
            // Get shake threshold from preferences
            val threshold = preferencesManager.getShakeSensitivityThreshold()
            
            if (acceleration > threshold) {
                val currentTime = System.currentTimeMillis()
                
                // Check if within time window
                if (currentTime - lastShakeTime < SHAKE_TIME_WINDOW) {
                    shakeCount++
                } else {
                    shakeCount = 1 // Reset count if outside time window
                }
                
                lastShakeTime = currentTime
                
                // Trigger emergency if threshold met
                if (shakeCount >= SHAKE_COUNT_THRESHOLD) {
                    if (currentTime - lastShakeTime > SHAKE_COOLDOWN) {
                        triggerEmergency()
                        shakeCount = 0
                    }
                }
            }
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for this implementation
    }
    
    private fun triggerEmergency() {
        // Start emergency service
        val intent = Intent(this, EmergencyService::class.java).apply {
            action = EmergencyService.ACTION_START_EMERGENCY
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        
        // Update notification
        updateNotification("Shake Detected!", "Emergency response activated")
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Shake Detection",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Background shake detection service"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Shake Detection Active")
            .setContentText("Shake your phone to trigger emergency alert")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    
    private fun updateNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopShakeDetection()
    }
}
