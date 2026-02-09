# Women Safety App

An Android emergency application built with Kotlin to help ensure women's safety in distress situations.

## Features

### Core Features
- **SOS/Panic Button**: Large, prominent emergency button with 3-second hold or confirmation dialog
- **Emergency Contacts Management**: Add, edit, and delete up to 5 trusted emergency contacts with Room database
- **Real-time Location Sharing**: Automatic GPS location tracking and sharing via SMS with Google Maps links
- **Automatic Emergency Actions**: 
  - Sends SMS to all emergency contacts
  - Makes call to primary contact
  - Plays loud alarm/siren
  - Starts audio recording (if enabled)
- **Fake Call Feature**: Realistic incoming call simulation to escape uncomfortable situations
- **Shake to Alert**: Accelerometer-based shake detection with configurable sensitivity
- **Quick Dial**: One-tap calling for emergency services (Police, Ambulance, Fire, Women's Helpline)
- **Audio Recording**: Automatic or manual audio recording during emergencies

## Technical Stack

### Architecture
- **Pattern**: MVVM (Model-View-ViewModel)
- **Language**: Kotlin
- **UI**: Material Design 3 components
- **Database**: Room Database for persistent storage
- **Async**: Kotlin Coroutines and Flow
- **Min SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)

### Key Components

#### Activities
- `MainActivity` - Home screen with SOS button and quick access features
- `ContactsActivity` - Manage emergency contacts with RecyclerView
- `SettingsActivity` - Configure app settings
- `FakeCallActivity` - Simulate incoming call

#### Services
- `EmergencyService` - Foreground service handling emergency actions
- `ShakeDetectionService` - Background service for shake detection

#### ViewModels
- `ContactsViewModel` - Manages emergency contacts data
- `SettingsViewModel` - Manages app settings

#### Utilities
- `PermissionsManager` - Runtime permission handling
- `PreferencesManager` - SharedPreferences management
- `LocationHelper` - GPS location with Google Play Services
- `SmsHelper` - Emergency SMS sending
- `AudioRecorder` - Audio recording functionality
- `AlarmPlayer` - Alarm/siren sound player

### Dependencies
- AndroidX Core KTX
- Material Design Components
- Room Database with KTX
- Google Play Services Location
- Kotlin Coroutines
- Lifecycle components (ViewModel, LiveData)
- ViewBinding

## Permissions

The app requires the following permissions:
- Location (Fine & Coarse) - For sharing location in emergencies
- Call Phone - To make emergency calls
- Send SMS - To send emergency messages
- Read Contacts - To pick contacts from phone
- Record Audio - For audio recording during emergencies
- Camera - For video recording (future feature)
- Storage - To save recordings
- Foreground Service - For background emergency service
- Post Notifications - For emergency alerts

## Setup Instructions

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 17
- Android SDK with API 34

### Build Instructions

1. Clone the repository:
```bash
git clone https://github.com/harsharma-me/women-safety-app.git
cd women-safety-app
```

2. Open the project in Android Studio

3. Sync Gradle files

4. Build the project:
```bash
./gradlew build
```

5. Run on device/emulator:
```bash
./gradlew installDebug
```

### Configuration

The app works offline and doesn't require any API keys. However, you may want to:
- Customize emergency phone numbers in `strings.xml`
- Adjust shake sensitivity thresholds in `PreferencesManager.kt`
- Modify SOS hold duration in `MainActivity.kt`

## Usage

### First Launch
1. Grant all requested permissions for full functionality
2. Add at least one emergency contact (mark one as primary)
3. Configure shake detection and other settings

### Emergency Activation
- **Hold SOS Button**: Press and hold for 3 seconds
- **Single Tap**: Shows confirmation dialog
- **Shake Phone**: Shake vigorously (if enabled in settings)

When activated, the app will:
1. Get your current GPS location
2. Send SMS with location to all emergency contacts
3. Call the primary emergency contact
4. Start audio recording (if enabled)
5. Play loud alarm sound
6. Show persistent notification

### Managing Contacts
- Tap "Emergency Contacts" from home screen
- Use floating action button (+) to add contacts
- Pick from phone contacts or enter manually
- Mark one contact as "Primary" for automatic calling
- Edit or delete contacts as needed

### Fake Call
- Tap "Fake Call" from home screen
- Realistic incoming call screen appears
- Accept or decline to dismiss
- Use to escape uncomfortable situations discreetly

### Settings
- Access from toolbar menu (⚙️)
- Enable/disable shake detection
- Adjust shake sensitivity (Low/Medium/High)
- Enable/disable auto-recording
- Manage app permissions

## Security & Privacy
- All data stored locally on device
- No data sent to external servers
- Secure Room database for contacts
- User consent required for all permissions
- App can be used completely offline

## Known Limitations
- Requires active location services for GPS
- SMS sending requires cellular network
- Shake detection drains battery when enabled
- Some features may not work without permissions

## Future Enhancements
- Video recording capability
- Cloud backup of emergency data
- Trusted contacts can track location in real-time
- PIN/biometric lock for app access
- Multiple language support
- Integration with local emergency services APIs

## Testing
- Test emergency features in safe environment
- Use dev mode to avoid actual calls/SMS during testing
- Verify all permissions are granted
- Test with and without network/GPS

## Contributing
Contributions are welcome! Please feel free to submit pull requests or open issues.

## License
This project is intended for educational and safety purposes.

## Disclaimer
This app is designed to assist in emergency situations but should not be the sole means of emergency response. Always call local emergency services (911, etc.) when in immediate danger.

## Support
For issues or questions, please open an issue on GitHub.