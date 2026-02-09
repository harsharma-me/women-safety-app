# Build and Testing Guide

## Prerequisites

### Required Software
- **Android Studio**: Arctic Fox (2020.3.1) or later recommended
- **JDK**: Version 17 (required for AGP 8.1.0)
- **Android SDK**: API Level 34 (Android 14)
- **Gradle**: 8.2 (included via wrapper)

### SDK Components
Install these via Android Studio SDK Manager:
- Android SDK Platform 34
- Android SDK Build-Tools 34.0.0
- Google Play Services
- Android SDK Command-line Tools

## Building the Project

### Using Android Studio (Recommended)

1. **Open Project**
   ```
   File > Open > Select women-safety-app directory
   ```

2. **Sync Gradle**
   - Android Studio will automatically detect and sync Gradle files
   - Wait for "Gradle sync finished" notification
   - If prompted, accept downloading any missing SDK components

3. **Build the APK**
   ```
   Build > Build Bundle(s) / APK(s) > Build APK(s)
   ```
   
   Output location: `app/build/outputs/apk/debug/app-debug.apk`

4. **Run on Device/Emulator**
   ```
   Run > Run 'app'
   ```
   - Select target device
   - Grant all permissions when prompted

### Using Command Line

1. **Navigate to Project**
   ```bash
   cd women-safety-app
   ```

2. **Build Debug APK**
   ```bash
   ./gradlew assembleDebug
   ```

3. **Build Release APK** (requires signing config)
   ```bash
   ./gradlew assembleRelease
   ```

4. **Install on Device**
   ```bash
   ./gradlew installDebug
   ```

5. **Run Tests** (if tests are added)
   ```bash
   ./gradlew test
   ```

## Common Build Issues

### Issue 1: Gradle Sync Failed
**Error**: "Could not resolve dependencies"
**Solution**: 
- Check internet connection
- Invalidate caches: `File > Invalidate Caches / Restart`
- Update Google Play Services in SDK Manager

### Issue 2: Java Version Mismatch
**Error**: "Unsupported Java version"
**Solution**:
- Ensure JDK 17 is installed
- Set in Android Studio: `File > Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK`

### Issue 3: SDK Not Found
**Error**: "SDK location not found"
**Solution**:
- Set ANDROID_HOME environment variable
- Or create local.properties with: `sdk.dir=/path/to/android/sdk`

### Issue 4: Missing Dependencies
**Error**: "Failed to resolve: [dependency]"
**Solution**:
- Check internet connection
- Ensure repositories in build.gradle are correct
- Try downloading dependencies manually: `./gradlew --refresh-dependencies`

## Testing the Application

### Permissions Testing

The app requires multiple runtime permissions. Test each feature:

1. **Location Permissions**
   - Required for: GPS tracking, location sharing
   - Test: SOS button, automatic location SMS

2. **Phone & SMS Permissions**
   - Required for: Emergency calls, SMS sending
   - Test: Quick dial buttons, SOS emergency actions

3. **Microphone Permission**
   - Required for: Audio recording
   - Test: Enable auto-record in settings, trigger SOS

4. **Contacts Permission**
   - Required for: Contact picker
   - Test: Add contact using "Pick from Contacts"

### Feature Testing

#### 1. SOS Button
- **Single Tap**: Should show confirmation dialog
- **Long Press**: Hold for 3 seconds, should activate automatically
- **Expected Actions**:
  - SMS sent to all contacts
  - Call to primary contact
  - Alarm starts playing
  - Recording starts (if enabled)
  - Notification appears

#### 2. Emergency Contacts
- **Add Contact**: 
  - Manual entry
  - Pick from phone contacts
  - Mark as primary
- **Edit Contact**: Modify existing contact details
- **Delete Contact**: Remove contact with confirmation
- **Limit**: Maximum 5 contacts allowed

#### 3. Shake Detection
- **Enable in Settings**: Toggle shake detection
- **Sensitivity Levels**: Test Low, Medium, High
- **Trigger**: Shake phone vigorously 3+ times
- **Expected**: Same as SOS button activation

#### 4. Fake Call
- **Activation**: Tap "Fake Call" button
- **Expected**: 
  - Full-screen incoming call UI
  - Ringtone plays
  - Phone vibrates
  - Accept/Decline buttons work

#### 5. Quick Dial
- **Test Each Button**:
  - Police (911)
  - Ambulance (911)
  - Fire (911)
  - Women Helpline (1091)
- **Expected**: Dialer opens with number

### Test Scenarios

#### Scenario 1: First Launch
1. App requests all permissions
2. User grants permissions
3. User adds 2-3 emergency contacts
4. User marks one as primary
5. User enables shake detection
6. Test SOS button (confirm dialog)

#### Scenario 2: Emergency Activation
1. User triggers SOS (long press)
2. Verify location is obtained
3. Verify SMS sent to all contacts
4. Verify call initiated to primary
5. Verify alarm playing
6. Verify recording started
7. Verify notification visible

#### Scenario 3: Shake Detection
1. Enable in settings
2. Set sensitivity to High
3. Lock phone
4. Shake phone vigorously
5. Verify emergency activated

#### Scenario 4: No Network/GPS
1. Disable GPS
2. Trigger SOS
3. Verify SMS sent with "Unable to determine location"
4. Verify other actions still work

#### Scenario 5: No Contacts Added
1. Clear all contacts
2. Trigger SOS
3. Verify error message shown
4. Verify no crash

### Dev Mode Testing

To test without actually sending SMS or making calls:

1. Comment out these lines in `EmergencyService.kt`:
   ```kotlin
   // smsHelper.sendEmergencySmsToAll(...)
   // makeEmergencyCall(...)
   ```

2. Replace with debug logs:
   ```kotlin
   Log.d("Emergency", "Would send SMS to ${contactsList.size} contacts")
   Log.d("Emergency", "Would call: ${primaryContact.phoneNumber}")
   ```

## Performance Considerations

### Battery Optimization
- Shake detection service runs continuously in background
- Test battery drain over time
- Verify service restarts after being killed
- Check battery optimization settings

### Memory Usage
- Monitor memory with Android Profiler
- Check for memory leaks
- Test with low-memory devices

### Network Usage
- SMS sending is cellular-only
- Location requires GPS or network
- App functions mostly offline

## Release Build Checklist

Before creating a release build:

- [ ] Remove all debug logs
- [ ] Test on multiple device sizes
- [ ] Test on different Android versions (API 24-34)
- [ ] Verify all permissions are necessary
- [ ] Test with permissions denied
- [ ] Test emergency scenarios end-to-end
- [ ] Update version code and name in build.gradle
- [ ] Generate signed APK with release keystore
- [ ] Test ProGuard/R8 minification (if enabled)
- [ ] Verify APK size is reasonable
- [ ] Test installation and uninstallation

## Debugging Tips

### Logcat Filters
```
Tag: Women Safety, Emergency, SOS, Shake, Location
```

### Common Debug Points
- `MainActivity.activateSos()`: SOS activation
- `EmergencyService.handleEmergency()`: Emergency actions
- `ShakeDetectionService.onSensorChanged()`: Shake detection
- `LocationHelper.getCurrentLocation()`: GPS location
- `SmsHelper.sendEmergencySms()`: SMS sending

### Breakpoint Locations
- `MainActivity:activateSos` - When SOS triggered
- `EmergencyService:handleEmergency` - Emergency flow start
- `ContactsViewModel:insertContact` - Adding contacts
- `ShakeDetectionService:triggerEmergency` - Shake detected

## Known Limitations

1. **Network Dependency**: SMS requires cellular network
2. **GPS Dependency**: Location accuracy depends on GPS signal
3. **Battery**: Shake detection drains battery when enabled
4. **Background Restrictions**: May be killed by aggressive power management
5. **Android 12+**: Additional restrictions on background services

## Support

For build issues or questions:
- Check Android Studio Build Output
- Review Gradle Console
- Check logcat for runtime errors
- Verify all prerequisites are met
