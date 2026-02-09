package com.safety.women.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.button.MaterialButton
import com.safety.women.R
import com.safety.women.databinding.ActivityMainBinding
import com.safety.women.service.EmergencyService
import com.safety.women.service.ShakeDetectionService
import com.safety.women.util.PermissionsManager
import com.safety.women.util.PreferencesManager

/**
 * Main Activity - Home screen with SOS button and quick access features
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var preferencesManager: PreferencesManager
    
    private var sosButtonPressTime: Long = 0
    private val sosHoldHandler = Handler(Looper.getMainLooper())
    private var isSosActivating = false
    
    companion object {
        private const val SOS_HOLD_DURATION = 3000L // 3 seconds
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        
        permissionsManager = PermissionsManager(this)
        preferencesManager = PreferencesManager(this)
        
        setupSosButton()
        setupQuickActions()
        setupQuickDial()
        
        // Request permissions on first launch
        if (preferencesManager.isFirstLaunch) {
            requestAllPermissions()
            preferencesManager.isFirstLaunch = false
        }
        
        // Start shake detection service if enabled
        if (preferencesManager.isShakeEnabled) {
            startShakeDetectionService()
        }
    }
    
    private fun setupSosButton() {
        binding.sosButton.setOnLongClickListener {
            sosButtonPressTime = System.currentTimeMillis()
            isSosActivating = true
            
            // Start animation or visual feedback
            binding.sosButton.alpha = 0.7f
            
            sosHoldHandler.postDelayed({
                if (isSosActivating) {
                    activateSos()
                }
            }, SOS_HOLD_DURATION)
            
            true
        }
        
        binding.sosButton.setOnClickListener {
            if (!isSosActivating) {
                // Show confirmation dialog for single tap
                showSosConfirmationDialog()
            }
        }
        
        // Reset when user releases button
        binding.sosButton.setOnTouchListener { v, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP || 
                event.action == android.view.MotionEvent.ACTION_CANCEL) {
                isSosActivating = false
                sosHoldHandler.removeCallbacksAndMessages(null)
                (v as MaterialButton).alpha = 1.0f
            }
            false
        }
    }
    
    private fun showSosConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.confirm_sos)
            .setMessage(R.string.confirm_sos_message)
            .setPositiveButton(R.string.yes) { _, _ ->
                activateSos()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }
    
    private fun activateSos() {
        // Check if we have required permissions
        if (!permissionsManager.hasAllCriticalPermissions()) {
            Toast.makeText(this, R.string.permission_required, Toast.LENGTH_SHORT).show()
            requestAllPermissions()
            return
        }
        
        // Start emergency service
        val intent = Intent(this, EmergencyService::class.java).apply {
            action = EmergencyService.ACTION_START_EMERGENCY
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        
        Toast.makeText(this, R.string.sos_activated, Toast.LENGTH_LONG).show()
    }
    
    private fun setupQuickActions() {
        binding.emergencyContactsButton.setOnClickListener {
            startActivity(Intent(this, ContactsActivity::class.java))
        }
        
        binding.fakeCallButton.setOnClickListener {
            startActivity(Intent(this, FakeCallActivity::class.java))
        }
    }
    
    private fun setupQuickDial() {
        binding.policeButton.setOnClickListener {
            makePhoneCall(getString(R.string.police_number))
        }
        
        binding.ambulanceButton.setOnClickListener {
            makePhoneCall(getString(R.string.ambulance_number))
        }
        
        binding.fireButton.setOnClickListener {
            makePhoneCall(getString(R.string.fire_number))
        }
        
        binding.womenHelplineButton.setOnClickListener {
            makePhoneCall(getString(R.string.women_helpline_number))
        }
    }
    
    private fun makePhoneCall(phoneNumber: String) {
        if (!permissionsManager.hasCallPermissions()) {
            permissionsManager.requestCallPermissions()
            return
        }
        
        try {
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, R.string.error_call_failed, Toast.LENGTH_SHORT).show()
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
    
    private fun requestAllPermissions() {
        permissionsManager.requestAllPermissions()
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        
        if (!allGranted) {
            Toast.makeText(
                this,
                "Some permissions were denied. App may not function properly.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
