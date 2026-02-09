# Women Safety App - Project Structure

## Directory Structure

```
women-safety-app/
├── app/
│   ├── build.gradle                    # App module build configuration
│   ├── proguard-rules.pro             # ProGuard/R8 rules
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml     # App manifest with permissions
│           ├── java/com/safety/women/
│           │   ├── data/              # Data layer
│           │   │   ├── AppDatabase.kt           # Room database
│           │   │   ├── EmergencyContact.kt      # Contact entity
│           │   │   └── EmergencyContactDao.kt   # Data access object
│           │   ├── service/           # Background services
│           │   │   ├── EmergencyService.kt      # Handles SOS actions
│           │   │   └── ShakeDetectionService.kt # Shake detection
│           │   ├── ui/                # User interface
│           │   │   ├── MainActivity.kt          # Home screen
│           │   │   ├── ContactsActivity.kt      # Contacts management
│           │   │   ├── SettingsActivity.kt      # App settings
│           │   │   └── FakeCallActivity.kt      # Fake call screen
│           │   ├── util/              # Utilities
│           │   │   ├── AlarmPlayer.kt          # Alarm sound
│           │   │   ├── AudioRecorder.kt        # Audio recording
│           │   │   ├── LocationHelper.kt       # GPS location
│           │   │   ├── PermissionsManager.kt   # Runtime permissions
│           │   │   ├── PreferencesManager.kt   # Settings storage
│           │   │   └── SmsHelper.kt            # SMS sending
│           │   └── viewmodel/         # ViewModels (MVVM)
│           │       ├── ContactsViewModel.kt
│           │       └── SettingsViewModel.kt
│           └── res/                   # Resources
│               ├── drawable/          # Drawable resources
│               │   └── sos_button_background.xml
│               ├── layout/            # XML layouts
│               │   ├── activity_main.xml
│               │   ├── activity_contacts.xml
│               │   ├── activity_settings.xml
│               │   ├── activity_fake_call.xml
│               │   ├── item_contact.xml
│               │   └── dialog_add_contact.xml
│               ├── menu/              # Menu resources
│               │   └── main_menu.xml
│               ├── mipmap-*/          # Launcher icons
│               ├── values/            # Value resources
│               │   ├── colors.xml
│               │   ├── strings.xml
│               │   └── themes.xml
│               └── xml/               # XML configs
│                   ├── backup_rules.xml
│                   └── data_extraction_rules.xml
├── gradle/
│   └── wrapper/                       # Gradle wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── .gitignore                         # Git ignore rules
├── build.gradle                       # Project build configuration
├── gradle.properties                  # Gradle properties
├── gradlew                           # Gradle wrapper script (Unix)
├── gradlew.bat                       # Gradle wrapper script (Windows)
├── settings.gradle                   # Project settings
├── README.md                         # Project documentation
└── BUILD_GUIDE.md                    # Build instructions
```

## Architecture Overview

### MVVM Pattern
```
View (Activity/Fragment) <-> ViewModel <-> Repository <-> Database/Network
```

### Component Responsibilities

#### Activities (UI Layer)
- **MainActivity**: 
  - Displays SOS button, quick actions, and quick dial
  - Handles SOS activation (long press or confirmation)
  - Manages permissions
  - Starts emergency service
  
- **ContactsActivity**:
  - Lists emergency contacts in RecyclerView
  - Add/Edit/Delete contacts
  - Contact picker integration
  - Mark primary contact
  
- **SettingsActivity**:
  - Configure shake detection
  - Adjust sensitivity
  - Enable/disable auto-recording
  - Manage permissions
  
- **FakeCallActivity**:
  - Full-screen fake call UI
  - Ringtone and vibration
  - Accept/Decline actions

#### Services (Background Layer)
- **EmergencyService** (Foreground):
  - Runs in foreground with notification
  - Coordinates all emergency actions
  - Gets GPS location
  - Sends SMS to all contacts
  - Makes emergency call
  - Starts recording
  - Plays alarm
  
- **ShakeDetectionService** (Foreground):
  - Monitors accelerometer sensor
  - Detects shake gestures
  - Triggers emergency when threshold met
  - Configurable sensitivity

#### ViewModels (Presentation Layer)
- **ContactsViewModel**:
  - Manages contact CRUD operations
  - Exposes LiveData for UI observation
  - Handles database operations via coroutines
  
- **SettingsViewModel**:
  - Manages app settings
  - Reads/writes to SharedPreferences
  - Exposes LiveData for UI binding

#### Data Layer
- **AppDatabase**:
  - Room database singleton
  - Manages emergency contacts
  - Thread-safe operations
  
- **EmergencyContact**:
  - Entity class for contacts
  - Fields: id, name, phone, relationship, isPrimary
  
- **EmergencyContactDao**:
  - Database operations
  - CRUD methods
  - Flow-based queries for reactive updates

#### Utilities
- **PermissionsManager**:
  - Centralized permission handling
  - Permission check methods
  - Permission request methods
  - Supports all Android versions
  
- **PreferencesManager**:
  - SharedPreferences wrapper
  - Type-safe getters/setters
  - Settings: shake enabled, sensitivity, auto-record, etc.
  
- **LocationHelper**:
  - FusedLocationProviderClient wrapper
  - Gets current location
  - Generates Google Maps links
  - Handles location permissions
  
- **SmsHelper**:
  - Sends emergency SMS
  - Formats emergency message
  - Handles long messages (splits if needed)
  - Includes location and timestamp
  
- **AudioRecorder**:
  - MediaRecorder wrapper
  - Starts/stops recording
  - Saves to external files directory
  - Handles audio permissions
  
- **AlarmPlayer**:
  - MediaPlayer wrapper
  - Plays alarm at max volume
  - Loops until stopped
  - Works even on silent mode

## Data Flow

### SOS Activation Flow
```
1. User triggers SOS (MainActivity)
   ↓
2. Check permissions
   ↓
3. Start EmergencyService
   ↓
4. EmergencyService:
   a. Get GPS location (LocationHelper)
   b. Load contacts from database
   c. Send SMS to all (SmsHelper)
   d. Call primary contact (Intent)
   e. Start recording (AudioRecorder)
   f. Play alarm (AlarmPlayer)
   g. Show notification
```

### Contact Management Flow
```
1. User adds contact (ContactsActivity)
   ↓
2. Input validation
   ↓
3. Create EmergencyContact object
   ↓
4. Call ContactsViewModel.insertContact()
   ↓
5. ViewModel uses coroutine to insert in DB
   ↓
6. Database update triggers Flow
   ↓
7. LiveData updates in Activity
   ↓
8. RecyclerView refreshes
```

### Shake Detection Flow
```
1. ShakeDetectionService started
   ↓
2. Register accelerometer listener
   ↓
3. Monitor sensor events
   ↓
4. Calculate acceleration magnitude
   ↓
5. Compare with threshold
   ↓
6. If threshold exceeded multiple times:
   ↓
7. Trigger emergency (start EmergencyService)
```

## Key Design Decisions

### 1. Room Database vs SharedPreferences
- **Room**: Used for emergency contacts
  - Structured data
  - Multiple records
  - Relationships (primary flag)
  - Type-safe queries
  
- **SharedPreferences**: Used for settings
  - Simple key-value pairs
  - No relationships
  - Faster access
  - Smaller data size

### 2. Foreground Services
- Both EmergencyService and ShakeDetectionService run as foreground
- Required for Android O+ to avoid being killed
- Show persistent notifications
- User aware of active services

### 3. MVVM Architecture
- Separation of concerns
- Testable ViewModels
- Lifecycle-aware components
- Reactive UI updates with LiveData

### 4. Kotlin Coroutines
- Async operations (database, network, location)
- Structured concurrency
- Exception handling
- No callback hell

### 5. Material Design 3
- Modern UI components
- Consistent design language
- Accessibility features
- Theming support

## Permission Handling

### Runtime Permissions (Android 6.0+)
All dangerous permissions are requested at runtime:
- Location: Fine & Coarse
- Phone: Call Phone
- SMS: Send SMS
- Contacts: Read Contacts
- Microphone: Record Audio
- Camera: Camera
- Storage: Read/Write (or Media permissions on Android 13+)
- Notifications: Post Notifications (Android 13+)

### Permission Strategy
1. **First Launch**: Request all permissions
2. **Feature Use**: Request specific permission if not granted
3. **Settings**: Allow user to manage permissions
4. **Graceful Degradation**: App works with partial permissions

## Security Considerations

### Data Security
- All data stored locally
- Room database encrypted (can be enhanced with SQLCipher)
- No data sent to external servers
- Contact information never leaves device

### Permission Security
- Minimum required permissions
- Clear permission rationale
- User consent required
- Can revoke anytime

### Emergency Data
- SMS includes location (with consent)
- Audio recordings stored locally
- User controls what data is shared

## Performance Optimizations

### 1. Database
- Room with LiveData for reactive updates
- Async operations with coroutines
- Indexed queries
- Minimal database access

### 2. UI
- ViewBinding for efficient view access
- RecyclerView with ViewHolder pattern
- Lazy loading where applicable
- Smooth animations

### 3. Background Work
- Foreground services only when needed
- Shake detection uses efficient sensor sampling
- Location updates only when emergency triggered
- Stop services when not needed

### 4. Memory
- Proper lifecycle management
- Release resources in onDestroy
- No memory leaks (checked with LeakCanary recommended)
- Efficient bitmap handling

## Testing Strategy

### Unit Tests (Recommended)
- ViewModels: Test business logic
- Utilities: Test helper functions
- Database: Test DAO operations

### Integration Tests (Recommended)
- Service behavior
- Database operations
- Permission handling

### UI Tests (Recommended)
- User flows
- Button clicks
- Dialog interactions

### Manual Testing
- Device compatibility
- Permission scenarios
- Network conditions
- GPS availability

## Extensibility

### Easy to Add
1. **Video Recording**: Extend AudioRecorder
2. **Cloud Backup**: Add repository pattern
3. **Real-time Tracking**: Add location service
4. **Multiple Languages**: Add string resources
5. **Dark Theme**: Extend themes.xml
6. **Biometric Lock**: Add BiometricPrompt
7. **Custom Alarm Sounds**: Add audio resources

### Architecture Supports
- New features without breaking existing code
- Additional ViewModels for new screens
- New utilities for new functionality
- Database migrations for schema changes

## Maintenance

### Version Control
- Use semantic versioning (MAJOR.MINOR.PATCH)
- Tag releases
- Maintain changelog

### Dependencies
- Keep dependencies updated
- Monitor security vulnerabilities
- Test after updates

### Code Quality
- Follow Kotlin style guide
- Use meaningful variable names
- Comment complex logic
- Remove unused code

## Future Enhancements

### Planned Features
- [ ] Video recording capability
- [ ] Cloud sync for contacts
- [ ] Real-time location tracking
- [ ] PIN/Biometric app lock
- [ ] Trusted contacts notification
- [ ] Emergency chat feature
- [ ] Incident reporting
- [ ] Community safety alerts

### Technical Improvements
- [ ] Add comprehensive test suite
- [ ] Implement Dependency Injection (Hilt)
- [ ] Add repository pattern
- [ ] Implement WorkManager for background tasks
- [ ] Add Firebase integration (optional)
- [ ] Optimize battery usage
- [ ] Add analytics (privacy-respecting)

This structure provides a solid foundation for a production-ready women's safety application while maintaining code quality, security, and extensibility.
