package com.safety.women.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.safety.women.R
import com.safety.women.data.AppDatabase
import com.safety.women.data.EmergencyContact
import com.safety.women.ui.MainActivity
import com.safety.women.util.*
import kotlinx.coroutines.*

/**
 * Foreground service to handle emergency actions
 */
class EmergencyService : Service() {
    
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private lateinit var locationHelper: LocationHelper
    private lateinit var smsHelper: SmsHelper
    private lateinit var audioRecorder: AudioRecorder
    private lateinit var alarmPlayer: AlarmPlayer
    private lateinit var preferencesManager: PreferencesManager
    
    companion object {
        const val ACTION_START_EMERGENCY = "com.safety.women.START_EMERGENCY"
        const val ACTION_STOP_ALARM = "com.safety.women.STOP_ALARM"
        const val ACTION_STOP_RECORDING = "com.safety.women.STOP_RECORDING"
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "emergency_channel"
    }
    
    override fun onCreate() {
        super.onCreate()
        locationHelper = LocationHelper(this)
        smsHelper = SmsHelper(this)
        audioRecorder = AudioRecorder(this)
        alarmPlayer = AlarmPlayer(this)
        preferencesManager = PreferencesManager(this)
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_EMERGENCY -> {
                startForegroundService()
                handleEmergency()
            }
            ACTION_STOP_ALARM -> {
                alarmPlayer.stopAlarm()
                updateNotification("Emergency Active", "Alarm stopped")
            }
            ACTION_STOP_RECORDING -> {
                audioRecorder.stopRecording()
                updateNotification("Emergency Active", "Recording stopped")
            }
        }
        
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    private fun startForegroundService() {
        val notification = createNotification("Emergency Alert", "Initiating emergency response...")
        
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
    
    private fun handleEmergency() {
        serviceScope.launch {
            try {
                // Step 1: Get location
                updateNotification("Emergency Alert", "Getting your location...")
                val location = locationHelper.getCurrentLocation()
                
                // Step 2: Get emergency contacts
                val database = AppDatabase.getDatabase(applicationContext)
                val contacts = database.emergencyContactDao().getAllContacts()
                
                var contactsList: List<EmergencyContact> = emptyList()
                contacts.collect { list ->
                    contactsList = list
                }
                
                if (contactsList.isEmpty()) {
                    updateNotification("Emergency Alert", "No emergency contacts found")
                    delay(3000)
                    stopSelf()
                    return@launch
                }
                
                // Step 3: Send SMS to all contacts
                updateNotification("Emergency Alert", "Sending emergency messages...")
                val userName = preferencesManager.userName
                val sentCount = smsHelper.sendEmergencySmsToAll(contactsList, location, userName)
                
                // Step 4: Call primary contact
                val primaryContact = contactsList.firstOrNull { it.isPrimary }
                    ?: contactsList.firstOrNull()
                
                if (primaryContact != null) {
                    updateNotification("Emergency Alert", "Calling ${primaryContact.name}...")
                    makeEmergencyCall(primaryContact.phoneNumber)
                }
                
                // Step 5: Start audio recording if enabled
                if (preferencesManager.isAutoRecordEnabled) {
                    updateNotification("Emergency Alert", "Starting audio recording...")
                    audioRecorder.startRecording()
                }
                
                // Step 6: Play alarm
                updateNotification("Emergency Alert", "Alarm activated")
                alarmPlayer.startAlarm()
                
                // Keep service running
                updateNotification(
                    "Emergency Active",
                    "Sent SMS to $sentCount contacts. Alarm playing."
                )
                
            } catch (e: Exception) {
                e.printStackTrace()
                updateNotification("Emergency Alert", "Error: ${e.message}")
            }
        }
    }
    
    private fun makeEmergencyCall(phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$phoneNumber")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Emergency Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for emergency situations"
                setShowBadge(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(title: String, message: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val stopAlarmIntent = Intent(this, EmergencyService::class.java).apply {
            action = ACTION_STOP_ALARM
        }
        val stopAlarmPendingIntent = PendingIntent.getService(
            this,
            1,
            stopAlarmIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(
                android.R.drawable.ic_media_pause,
                getString(R.string.stop_alarm),
                stopAlarmPendingIntent
            )
            .build()
    }
    
    private fun updateNotification(title: String, message: String) {
        val notification = createNotification(title, message)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        alarmPlayer.stopAlarm()
        audioRecorder.stopRecording()
        serviceScope.cancel()
    }
}
