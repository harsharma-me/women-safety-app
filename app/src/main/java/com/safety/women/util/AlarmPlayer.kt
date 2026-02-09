package com.safety.women.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build

/**
 * Helper class to play alarm/siren sound
 */
class AlarmPlayer(private val context: Context) {
    
    private var mediaPlayer: MediaPlayer? = null
    private var audioManager: AudioManager? = null
    private var previousVolume: Int = 0
    
    /**
     * Start playing alarm sound
     */
    fun startAlarm() {
        if (mediaPlayer?.isPlaying == true) {
            return
        }
        
        try {
            audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            
            // Save current volume
            previousVolume = audioManager?.getStreamVolume(AudioManager.STREAM_ALARM) ?: 0
            
            // Set to maximum volume
            val maxVolume = audioManager?.getStreamMaxVolume(AudioManager.STREAM_ALARM) ?: 0
            audioManager?.setStreamVolume(
                AudioManager.STREAM_ALARM,
                maxVolume,
                0
            )
            
            // Initialize MediaPlayer
            mediaPlayer = MediaPlayer()
            
            // Use default alarm sound
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            
            mediaPlayer?.apply {
                setDataSource(context, alarmUri)
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                } else {
                    @Suppress("DEPRECATION")
                    setAudioStreamType(AudioManager.STREAM_ALARM)
                }
                
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Stop playing alarm sound
     */
    fun stopAlarm() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                reset()
                release()
            }
            mediaPlayer = null
            
            // Restore previous volume
            audioManager?.setStreamVolume(
                AudioManager.STREAM_ALARM,
                previousVolume,
                0
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Check if alarm is currently playing
     */
    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }
}
