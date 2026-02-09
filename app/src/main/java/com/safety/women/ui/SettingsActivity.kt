package com.safety.women.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.safety.women.R
import com.safety.women.databinding.ActivitySettingsBinding
import com.safety.women.service.ShakeDetectionService
import com.safety.women.viewmodel.SettingsViewModel

/**
 * Activity for app settings
 */
class SettingsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        
        setupShakeDetection()
        setupAudioRecording()
        setupPermissions()
        observeSettings()
        
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupShakeDetection() {
        binding.shakeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setShakeEnabled(isChecked)
            
            if (isChecked) {
                startShakeDetectionService()
            } else {
                stopShakeDetectionService()
            }
        }
        
        binding.sensitivityRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val sensitivity = when (checkedId) {
                R.id.lowSensitivity -> 0
                R.id.mediumSensitivity -> 1
                R.id.highSensitivity -> 2
                else -> 2
            }
            viewModel.setShakeSensitivity(sensitivity)
        }
    }
    
    private fun setupAudioRecording() {
        binding.autoRecordSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setAutoRecord(isChecked)
        }
    }
    
    private fun setupPermissions() {
        binding.managePermissionsButton.setOnClickListener {
            // Open app settings
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = android.net.Uri.fromParts("package", packageName, null)
            }
            startActivity(intent)
        }
    }
    
    private fun observeSettings() {
        viewModel.shakeEnabled.observe(this) { enabled ->
            binding.shakeSwitch.isChecked = enabled
        }
        
        viewModel.shakeSensitivity.observe(this) { sensitivity ->
            val radioButtonId = when (sensitivity) {
                0 -> R.id.lowSensitivity
                1 -> R.id.mediumSensitivity
                2 -> R.id.highSensitivity
                else -> R.id.highSensitivity
            }
            binding.sensitivityRadioGroup.check(radioButtonId)
        }
        
        viewModel.autoRecord.observe(this) { enabled ->
            binding.autoRecordSwitch.isChecked = enabled
        }
    }
    
    private fun startShakeDetectionService() {
        val intent = Intent(this, ShakeDetectionService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
    
    private fun stopShakeDetectionService() {
        val intent = Intent(this, ShakeDetectionService::class.java)
        stopService(intent)
    }
}
