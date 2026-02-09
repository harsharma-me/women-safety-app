package com.safety.women.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.telephony.SmsManager
import androidx.core.app.ActivityCompat
import com.safety.women.R
import com.safety.women.data.EmergencyContact
import java.text.SimpleDateFormat
import java.util.*

/**
 * Helper class to send emergency SMS messages
 */
class SmsHelper(private val context: Context) {
    
    /**
     * Send emergency SMS to a single contact
     */
    fun sendEmergencySms(contact: EmergencyContact, location: Location?, userName: String): Boolean {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        
        return try {
            val message = buildEmergencyMessage(userName, location)
            val smsManager = SmsManager.getDefault()
            
            // If message is too long, split it
            val parts = smsManager.divideMessage(message)
            if (parts.size > 1) {
                smsManager.sendMultipartTextMessage(
                    contact.phoneNumber,
                    null,
                    parts,
                    null,
                    null
                )
            } else {
                smsManager.sendTextMessage(
                    contact.phoneNumber,
                    null,
                    message,
                    null,
                    null
                )
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Send emergency SMS to multiple contacts
     */
    fun sendEmergencySmsToAll(contacts: List<EmergencyContact>, location: Location?, userName: String): Int {
        var successCount = 0
        contacts.forEach { contact ->
            if (sendEmergencySms(contact, location, userName)) {
                successCount++
            }
        }
        return successCount
    }
    
    /**
     * Build emergency message text
     */
    private fun buildEmergencyMessage(userName: String, location: Location?): String {
        val timestamp = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
            .format(Date())
        
        return if (location != null) {
            context.getString(
                R.string.emergency_sms_template,
                userName,
                location.latitude,
                location.longitude,
                timestamp
            )
        } else {
            "EMERGENCY ALERT! $userName needs help!\nLocation: Unable to determine location\nTime: $timestamp\nSent from Women Safety App"
        }
    }
}
