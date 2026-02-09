package com.safety.women.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.safety.women.util.PreferencesManager

/**
 * ViewModel for app settings
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val preferencesManager = PreferencesManager(application)
    
    private val _shakeEnabled = MutableLiveData<Boolean>()
    val shakeEnabled: LiveData<Boolean> = _shakeEnabled
    
    private val _shakeSensitivity = MutableLiveData<Int>()
    val shakeSensitivity: LiveData<Int> = _shakeSensitivity
    
    private val _autoRecord = MutableLiveData<Boolean>()
    val autoRecord: LiveData<Boolean> = _autoRecord
    
    private val _alarmVolume = MutableLiveData<Int>()
    val alarmVolume: LiveData<Int> = _alarmVolume
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        _shakeEnabled.value = preferencesManager.isShakeEnabled
        _shakeSensitivity.value = preferencesManager.shakeSensitivity
        _autoRecord.value = preferencesManager.isAutoRecordEnabled
        _alarmVolume.value = preferencesManager.alarmVolume
    }
    
    fun setShakeEnabled(enabled: Boolean) {
        preferencesManager.isShakeEnabled = enabled
        _shakeEnabled.value = enabled
    }
    
    fun setShakeSensitivity(sensitivity: Int) {
        preferencesManager.shakeSensitivity = sensitivity
        _shakeSensitivity.value = sensitivity
    }
    
    fun setAutoRecord(enabled: Boolean) {
        preferencesManager.isAutoRecordEnabled = enabled
        _autoRecord.value = enabled
    }
    
    fun setAlarmVolume(volume: Int) {
        preferencesManager.alarmVolume = volume
        _alarmVolume.value = volume
    }
}
