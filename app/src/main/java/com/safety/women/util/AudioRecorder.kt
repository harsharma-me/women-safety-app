package com.safety.women.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import androidx.core.app.ActivityCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Helper class to record audio
 */
class AudioRecorder(private val context: Context) {
    
    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var isRecording = false
    
    /**
     * Start audio recording
     */
    fun startRecording(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        
        if (isRecording) {
            return false
        }
        
        return try {
            // Create output file
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "emergency_audio_$timestamp.3gp"
            outputFile = File(context.getExternalFilesDir(null), fileName)
            
            // Initialize MediaRecorder
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }
            
            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(outputFile?.absolutePath)
                prepare()
                start()
            }
            
            isRecording = true
            true
        } catch (e: Exception) {
            e.printStackTrace()
            releaseRecorder()
            false
        }
    }
    
    /**
     * Stop audio recording
     */
    fun stopRecording(): File? {
        if (!isRecording) {
            return null
        }
        
        return try {
            mediaRecorder?.apply {
                stop()
                reset()
            }
            isRecording = false
            val file = outputFile
            releaseRecorder()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            releaseRecorder()
            null
        }
    }
    
    /**
     * Check if currently recording
     */
    fun isCurrentlyRecording(): Boolean = isRecording
    
    /**
     * Release media recorder resources
     */
    private fun releaseRecorder() {
        mediaRecorder?.release()
        mediaRecorder = null
        isRecording = false
    }
    
    /**
     * Get the current output file path
     */
    fun getCurrentFilePath(): String? = outputFile?.absolutePath
}
