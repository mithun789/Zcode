# Zcode Setup & Troubleshooting Guide

## 🚀 Initial Setup

### Step 1: Open Project in Android Studio

1. Launch Android Studio
2. Click **File → Open**
3. Navigate to `C:\Users\User\Documents\Zcode`
4. Click **Open**
5. Wait for project indexing to complete

### Step 2: Sync Gradle

1. Click **File → Sync Now**
2. Wait for sync to complete
3. If prompted, accept Android SDK downloads
4. Check the Build panel for completion message

### Step 3: Build the Project

```bash
# In Android Studio terminal or command line
./gradlew clean build
```

**Expected Output:**
```
BUILD SUCCESSFUL in X seconds
```

### Step 4: Run on Emulator

1. Select **Run → Run 'app'** or press **Shift+F10**
2. Select or create an emulator
3. App should launch with 4 bottom navigation tabs
4. Settings tab shows theme selector

---

## ✅ Verification Checklist

After running, verify:

- [ ] App launches without errors
- [ ] 4 tabs visible at bottom (Terminal, Files, System, Settings)
- [ ] Settings tab displays:
  - [ ] "Settings" title
  - [ ] "Theme" section with 3 theme cards
  - [ ] "Visual Effects" section
  - [ ] Blur Intensity slider
  - [ ] Transparency slider
  - [ ] Glassmorphism toggle
- [ ] Clicking theme cards changes appearance
- [ ] Sliders respond to touches
- [ ] Toggle switch works
- [ ] No crashes when navigating tabs

---

## 🛠️ Troubleshooting Guide

### Issue 1: Gradle Sync Fails

**Error**: `Could not find com.android.tools.build:gradle:8.13.1`

**Solutions**:
1. Check internet connection
2. Update Android Studio to latest version
3. Clear Gradle cache:
   ```bash
   rm -r ~/.gradle/caches  # On Mac/Linux
   rmdir /s %USERPROFILE%\.gradle\caches  # On Windows
   ```
4. File → Invalidate Caches → Restart

### Issue 2: Hilt Annotation Processing Fails

**Error**: `@HiltAndroidApp not generating code`

**Solutions**:
1. Ensure `ZcodeApplication` has `@HiltAndroidApp` annotation
2. Check AndroidManifest.xml has `android:name=".ZcodeApplication"`
3. Verify KSP plugin is applied in build.gradle.kts
4. Clean and rebuild:
   ```bash
   ./gradlew clean build
   ```

### Issue 3: Theme Not Updating

**Symptoms**: Changing theme doesn't update colors

**Solutions**:
1. Ensure `ZcodeTheme` wraps `setContent()` in MainActivity
2. Verify Flow collector is active in Composables
3. Check database permissions are granted
4. Clear app data and restart:
   ```bash
   adb shell pm clear com.example.zcode
   ```

### Issue 4: Database Errors

**Error**: `E/AndroidRuntime: FATAL EXCEPTION: java.lang.RuntimeException: Cannot create an instance of class UserPreferences`

**Solutions**:
1. Database schema mismatch - clear app data:
   ```bash
   adb shell am force-stop com.example.zcode
   adb shell pm clear com.example.zcode
   ```
2. Verify UserPreferences entity is correct:
   - Has `@Entity` annotation
   - Has `@PrimaryKey` on `id` field
   - All properties have default values
3. Check Room version compatibility

### Issue 5: RenderScript Not Available

**Error**: `RenderScript not found` or blur effects crash

**Symptoms**:
- Blur filter throws exception
- Older Android versions crash

**Solutions**:
1. Add fallback for devices without RenderScript:
   ```kotlin
   try {
       blurFilter.applyBlur(bitmap, radius)
   } catch (e: Exception) {
       // Fallback: return original bitmap
       bitmap
   }
   ```
2. Check Android SDK has RenderScript tools
3. Test on device with newer OS (API 21+)

### Issue 6: File Operations Fail

**Error**: `Permission denied` when accessing files

**Solutions**:
1. Grant runtime permissions for storage:
   ```kotlin
   // Request at runtime for Android 6+
   ActivityCompat.requestPermissions(
       this,
       arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
       PERMISSION_REQUEST_CODE
   )
   ```
2. Check `<uses-permission>` in AndroidManifest.xml
3. On Android 11+, may need scoped storage handling

### Issue 7: Network Operations Don't Work

**Error**: `getIPv4Address()` returns null

**Solutions**:
1. Emulator needs network configuration:
   - Extended controls → Virtual sensors → Check connectivity
2. Grant `ACCESS_NETWORK_STATE` permission
3. Check manifest has permissions:
   ```xml
   <uses-permission android:name="android.permission.INTERNET" />
   <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
   ```
4. Real device should auto-detect network

### Issue 8: Settings Don't Persist

**Symptoms**: Theme changes disappear on app restart

**Solutions**:
1. Ensure `SettingsViewModel.initializeSettings()` is called
2. Verify database is writing data:
   ```bash
   adb shell sqlite3 /data/data/com.example.zcode/databases/zcode_database
   sqlite> SELECT * FROM user_preferences;
   ```
3. Check `themeManager.initializeDefaultPreferences()` is called
4. Verify DAO update methods are being called

### Issue 9: Compose Preview Crashes

**Error**: `Preview crashed` in Android Studio

**Solutions**:
1. Add default parameters to preview:
   ```kotlin
   @Preview
   @Composable
   fun SettingsScreenPreview() {
       SettingsScreen(viewModel = previewViewModel())
   }
   ```
2. Create preview ViewModel that returns static data
3. Restart Android Studio
4. Invalidate caches: File → Invalidate Caches → Restart

### Issue 10: App Crashes on Launch

**Error**: `NullPointerException` or `ClassNotFoundException`

**Solutions**:
1. Check all imports are correct
2. Verify no typos in class names
3. Check Hilt injection is correct:
   - Use `@AndroidEntryPoint` on Activities
   - Use `@HiltViewModel` on ViewModels
   - Inject services with `@Inject` or `hiltViewModel()`
4. Catfish log for root cause:
   ```bash
   adb logcat | grep com.example.zcode
   ```

---

## 🔍 Debugging Tips

### View App Logs
```bash
adb logcat | grep com.example.zcode
```

### Check Database Content
```bash
adb shell sqlite3 /data/data/com.example.zcode/databases/zcode_database

# SQL commands:
sqlite> .tables
sqlite> .schema user_preferences
sqlite> SELECT * FROM user_preferences;
sqlite> UPDATE user_preferences SET themeMode = 'DARK';
sqlite> .exit
```

### View File System
```bash
adb shell
ls -la /data/data/com.example.zcode/
cat /data/data/com.example.zcode/databases/zcode_database
```

### Monitor Network
```bash
adb shell netstat
adb shell ss -tlnp  # Modern Android
```

### Check Permissions
```bash
adb shell dumpsys package com.example.zcode | grep permission
```

### Logcat Filters

```bash
# Theme updates
adb logcat | grep -E "(Theme|Color|Material)"

# Database operations
adb logcat | grep -E "(Room|Database|SQL)"

# Network operations
adb logcat | grep -E "(Network|Socket|IP)"

# File operations
adb logcat | grep -E "(File|Storage|Directory)"

# Errors only
adb logcat *:E | grep com.example.zcode
```

---

## 🔧 Advanced Configuration

### Change Target SDK
In `app/build.gradle.kts`:
```kotlin
targetSdk = 35  // Change version number
```

### Disable ProGuard
In `app/build.gradle.kts`:
```kotlin
buildTypes {
    release {
        isMinifyEnabled = false  // Set to false for debugging
    }
}
```

### Enable Multidex (for large apps)
In `app/build.gradle.kts`:
```kotlin
android {
    defaultConfig {
        multiDexEnabled = true
    }
}

dependencies {
    implementation 'androidx.multidex:multidex:2.0.1'
}
```

### Increase Gradle Heap
In `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxPermSize=512m
```

---

## 📱 Testing on Real Device

### Prerequisites
1. Enable Developer Mode on phone (tap Build Number 7 times)
2. Enable USB Debugging in Developer Options
3. Connect via USB cable
4. Accept USB debugging prompt on phone

### Run on Device
```bash
adb devices  # Verify device is connected

# Run app
./gradlew installDebug

# Or in Android Studio: Run → Select device
```

### ADB Commands
```bash
# List connected devices
adb devices

# Clear app data
adb shell pm clear com.example.zcode

# Force stop app
adb shell am force-stop com.example.zcode

# View detailed logs
adb logcat -v long
```

---

## 📊 Performance Monitoring

### Check Memory Usage
```bash
adb shell dumpsys meminfo com.example.zcode
```

### Monitor CPU
```bash
adb shell top -p $(adb shell pidof com.example.zcode)
```

### Check Battery Impact
```bash
adb shell dumpsys batterystats
```

---

## 🔐 Security Checklist

Before deploying:
- [ ] No hardcoded credentials in code
- [ ] API keys stored securely
- [ ] Permissions properly requested
- [ ] Network traffic encrypted (HTTPS)
- [ ] Sensitive data encrypted
- [ ] Proguard enabled for release
- [ ] Zipalign applied
- [ ] Signed with release key

---

## 📚 Resources

### Official Documentation
- [Android Developers](https://developer.android.com/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose/documentation)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)

### Troubleshooting Docs
- [Android Studio Issues](https://developer.android.com/studio/troubleshoot)
- [Gradle Build Failures](https://developer.android.com/build/troubleshoot-build-errors)
- [ADB Issues](https://developer.android.com/studio/command-line/adb)

### Community Help
- [Stack Overflow Android Tag](https://stackoverflow.com/questions/tagged/android)
- [Android Developers Reddit](https://www.reddit.com/r/androiddev/)
- [Android Issue Tracker](https://issuetracker.google.com/issues?q=componentid:192708)

---

## 🆘 Getting Help

If stuck:

1. **Check logs first**: `adb logcat | grep com.example.zcode`
2. **Search Stack Overflow** with error message
3. **Read the error stack trace** carefully
4. **Try the solutions above**
5. **Check official Android docs**
6. **Ask on Android subreddit** with:
   - Full error message
   - Relevant code snippet
   - Steps to reproduce
   - Device/Android version info

---

## ✨ Success Indicators

### When Everything Works
```
✅ App launches successfully
✅ 4 navigation tabs visible
✅ Settings screen fully functional
✅ Theme changes apply immediately
✅ Sliders respond smoothly
✅ Toggle switches work
✅ Settings persist after restart
✅ No crashes or errors
✅ Navigation is smooth
✅ UI is responsive
```

### Performance Benchmarks
- **Launch Time**: < 2 seconds
- **Theme Switch**: < 100ms
- **Database Query**: < 50ms
- **Memory Usage**: < 100MB
- **CPU**: < 10% idle

---

**Last Updated**: November 13, 2025
**Version**: 1.0 - Phase 2 Setup & Troubleshooting

