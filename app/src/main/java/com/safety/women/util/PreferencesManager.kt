package com.safety.women.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Manager for app preferences using SharedPreferences
 */
class PreferencesManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "women_safety_prefs"
        
        // Keys
        private const val KEY_SHAKE_ENABLED = "shake_enabled"
        private const val KEY_SHAKE_SENSITIVITY = "shake_sensitivity"
        private const val KEY_AUTO_RECORD = "auto_record"
        private const val KEY_ALARM_VOLUME = "alarm_volume"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        
        // Default values
        const val DEFAULT_SHAKE_SENSITIVITY = 2 // 0=Low, 1=Medium, 2=High
        const val DEFAULT_ALARM_VOLUME = 100
    }
    
    var isShakeEnabled: Boolean
        get() = sharedPreferences.getBoolean(KEY_SHAKE_ENABLED, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_SHAKE_ENABLED, value).apply()
    
    var shakeSensitivity: Int
        get() = sharedPreferences.getInt(KEY_SHAKE_SENSITIVITY, DEFAULT_SHAKE_SENSITIVITY)
        set(value) = sharedPreferences.edit().putInt(KEY_SHAKE_SENSITIVITY, value).apply()
    
    var isAutoRecordEnabled: Boolean
        get() = sharedPreferences.getBoolean(KEY_AUTO_RECORD, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_AUTO_RECORD, value).apply()
    
    var alarmVolume: Int
        get() = sharedPreferences.getInt(KEY_ALARM_VOLUME, DEFAULT_ALARM_VOLUME)
        set(value) = sharedPreferences.edit().putInt(KEY_ALARM_VOLUME, value).apply()
    
    var userName: String
        get() = sharedPreferences.getString(KEY_USER_NAME, "User") ?: "User"
        set(value) = sharedPreferences.edit().putString(KEY_USER_NAME, value).apply()
    
    var isFirstLaunch: Boolean
        get() = sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_FIRST_LAUNCH, value).apply()
    
    fun getShakeSensitivityThreshold(): Float {
        return when (shakeSensitivity) {
            0 -> 20f  // Low
            1 -> 15f  // Medium
            2 -> 12f  // High
            else -> 15f
        }
    }
}
