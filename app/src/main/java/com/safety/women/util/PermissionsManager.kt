package com.safety.women.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Helper class to manage runtime permissions
 */
class PermissionsManager(private val activity: Activity) {
    
    companion object {
        const val REQUEST_CODE_LOCATION = 1001
        const val REQUEST_CODE_CALL = 1002
        const val REQUEST_CODE_SMS = 1003
        const val REQUEST_CODE_CONTACTS = 1004
        const val REQUEST_CODE_AUDIO = 1005
        const val REQUEST_CODE_CAMERA = 1006
        const val REQUEST_CODE_STORAGE = 1007
        const val REQUEST_CODE_NOTIFICATION = 1008
        const val REQUEST_CODE_ALL = 1009
        
        val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        
        val CALL_PERMISSIONS = arrayOf(
            Manifest.permission.CALL_PHONE
        )
        
        val SMS_PERMISSIONS = arrayOf(
            Manifest.permission.SEND_SMS
        )
        
        val CONTACTS_PERMISSIONS = arrayOf(
            Manifest.permission.READ_CONTACTS
        )
        
        val AUDIO_PERMISSIONS = arrayOf(
            Manifest.permission.RECORD_AUDIO
        )
        
        val CAMERA_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA
        )
        
        val STORAGE_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        
        val NOTIFICATION_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            emptyArray()
        }
        
        fun hasPermission(context: Context, permission: String): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
        
        fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
            return permissions.all { hasPermission(context, it) }
        }
    }
    
    fun hasLocationPermissions(): Boolean {
        return hasPermissions(activity, LOCATION_PERMISSIONS)
    }
    
    fun hasCallPermissions(): Boolean {
        return hasPermissions(activity, CALL_PERMISSIONS)
    }
    
    fun hasSmsPermissions(): Boolean {
        return hasPermissions(activity, SMS_PERMISSIONS)
    }
    
    fun hasContactsPermissions(): Boolean {
        return hasPermissions(activity, CONTACTS_PERMISSIONS)
    }
    
    fun hasAudioPermissions(): Boolean {
        return hasPermissions(activity, AUDIO_PERMISSIONS)
    }
    
    fun hasCameraPermissions(): Boolean {
        return hasPermissions(activity, CAMERA_PERMISSIONS)
    }
    
    fun hasStoragePermissions(): Boolean {
        return hasPermissions(activity, STORAGE_PERMISSIONS)
    }
    
    fun hasNotificationPermissions(): Boolean {
        return NOTIFICATION_PERMISSIONS.isEmpty() || hasPermissions(activity, NOTIFICATION_PERMISSIONS)
    }
    
    fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(activity, LOCATION_PERMISSIONS, REQUEST_CODE_LOCATION)
    }
    
    fun requestCallPermissions() {
        ActivityCompat.requestPermissions(activity, CALL_PERMISSIONS, REQUEST_CODE_CALL)
    }
    
    fun requestSmsPermissions() {
        ActivityCompat.requestPermissions(activity, SMS_PERMISSIONS, REQUEST_CODE_SMS)
    }
    
    fun requestContactsPermissions() {
        ActivityCompat.requestPermissions(activity, CONTACTS_PERMISSIONS, REQUEST_CODE_CONTACTS)
    }
    
    fun requestAudioPermissions() {
        ActivityCompat.requestPermissions(activity, AUDIO_PERMISSIONS, REQUEST_CODE_AUDIO)
    }
    
    fun requestCameraPermissions() {
        ActivityCompat.requestPermissions(activity, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA)
    }
    
    fun requestStoragePermissions() {
        ActivityCompat.requestPermissions(activity, STORAGE_PERMISSIONS, REQUEST_CODE_STORAGE)
    }
    
    fun requestNotificationPermissions() {
        if (NOTIFICATION_PERMISSIONS.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity, NOTIFICATION_PERMISSIONS, REQUEST_CODE_NOTIFICATION)
        }
    }
    
    fun requestAllPermissions() {
        val allPermissions = mutableListOf<String>()
        allPermissions.addAll(LOCATION_PERMISSIONS)
        allPermissions.addAll(CALL_PERMISSIONS)
        allPermissions.addAll(SMS_PERMISSIONS)
        allPermissions.addAll(CONTACTS_PERMISSIONS)
        allPermissions.addAll(AUDIO_PERMISSIONS)
        allPermissions.addAll(CAMERA_PERMISSIONS)
        allPermissions.addAll(STORAGE_PERMISSIONS)
        allPermissions.addAll(NOTIFICATION_PERMISSIONS)
        
        ActivityCompat.requestPermissions(
            activity,
            allPermissions.toTypedArray(),
            REQUEST_CODE_ALL
        )
    }
    
    fun hasAllCriticalPermissions(): Boolean {
        return hasLocationPermissions() && hasCallPermissions() && hasSmsPermissions()
    }
}
