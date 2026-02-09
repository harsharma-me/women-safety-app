package com.safety.women.ui

import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.safety.women.databinding.ActivityFakeCallBinding

/**
 * Activity to simulate incoming call
 */
class FakeCallActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityFakeCallBinding
    private var ringtone: Ringtone? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFakeCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Make it appear as a real incoming call
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        
        startRingtone()
        startVibration()
        
        binding.acceptButton.setOnClickListener {
            // Simulate accepting the call
            stopRingtone()
            stopVibration()
            
            // Show a brief "connected" state then close
            Handler(Looper.getMainLooper()).postDelayed({
                finish()
            }, 2000)
        }
        
        binding.declineButton.setOnClickListener {
            // Simulate declining the call
            stopRingtone()
            stopVibration()
            finish()
        }
    }
    
    private fun startRingtone() {
        try {
            val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            ringtone = RingtoneManager.getRingtone(applicationContext, ringtoneUri)
            ringtone?.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun stopRingtone() {
        ringtone?.stop()
    }
    
    private fun startVibration() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as android.os.Vibrator
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val pattern = longArrayOf(0, 1000, 1000)
            vibrator.vibrate(android.os.VibrationEffect.createWaveform(pattern, 0))
        } else {
            @Suppress("DEPRECATION")
            val pattern = longArrayOf(0, 1000, 1000)
            vibrator.vibrate(pattern, 0)
        }
    }
    
    private fun stopVibration() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as android.os.Vibrator
        vibrator.cancel()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopRingtone()
        stopVibration()
    }
}
