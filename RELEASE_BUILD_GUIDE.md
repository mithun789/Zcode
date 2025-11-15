# Zcode Terminal - Release Build Guide

This guide explains how to build production-ready APK files for the Zcode Linux terminal application.

## Prerequisites

Before building a release APK, ensure you have:

1. **Development Environment**
   - Android Studio Arctic Fox or later
   - JDK 11+
   - Android SDK 36 (API 36)
   - Gradle 8.7+

2. **Required Files**
   - Source code from GitHub repository
   - Signing key (optional, for signed releases)

## Build Configuration

The project is already configured for release builds with the following optimizations:

### App-level Configuration (`app/build.gradle.kts`)

```kotlin
buildTypes {
    release {
        isMinifyEnabled = true          // Enable code shrinking
        isShrinkResources = true        // Enable resource shrinking
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
        signingConfig = signingConfigs.getByName("debug")  // Change for production
    }
}
```

### ProGuard Rules

The app includes ProGuard rules to optimize the release build while preserving necessary classes. See `app/proguard-rules.pro` for details.

## Building Debug APK

For testing purposes, build a debug APK:

```bash
# Clean previous builds
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Output location
ls -lh app/build/outputs/apk/debug/app-debug.apk
```

## Building Release APK

### Option 1: Build without Signing (for testing)

```bash
# Clean previous builds
./gradlew clean

# Build release APK (will use debug signing)
./gradlew assembleRelease

# Output location
ls -lh app/build/outputs/apk/release/app-release.apk
```

### Option 2: Build with Custom Signing

1. **Create a Keystore** (one-time setup)
   ```bash
   keytool -genkey -v -keystore zcode-release-key.keystore \
           -alias zcode-key \
           -keyalg RSA \
           -keysize 2048 \
           -validity 10000
   ```

2. **Update Build Configuration**
   
   Add to `app/build.gradle.kts`:
   ```kotlin
   android {
       signingConfigs {
           create("release") {
               storeFile = file("../zcode-release-key.keystore")
               storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "your-password"
               keyAlias = "zcode-key"
               keyPassword = System.getenv("KEY_PASSWORD") ?: "your-password"
           }
       }
       
       buildTypes {
           release {
               signingConfig = signingConfigs.getByName("release")
               // ... rest of config
           }
       }
   }
   ```

3. **Build Signed Release**
   ```bash
   # Set environment variables (recommended)
   export KEYSTORE_PASSWORD="your-keystore-password"
   export KEY_PASSWORD="your-key-password"
   
   # Build signed release
   ./gradlew assembleRelease
   ```

### Option 3: Build via Android Studio

1. **Open Project** in Android Studio
2. **Build > Generate Signed Bundle / APK**
3. **Select APK**
4. **Choose existing keystore or create new**
5. **Enter passwords**
6. **Select release build variant**
7. **Build**

## Verifying the APK

After building, verify the APK:

### 1. Check APK Size
```bash
ls -lh app/build/outputs/apk/release/app-release.apk
# Expected size: 5-15 MB (depending on optimizations)
```

### 2. Inspect APK Contents
```bash
# Using aapt (Android Asset Packaging Tool)
aapt dump badging app/build/outputs/apk/release/app-release.apk

# Check APK signature
apksigner verify app/build/outputs/apk/release/app-release.apk
```

### 3. Install and Test
```bash
# Install on connected device
adb install -r app/build/outputs/apk/release/app-release.apk

# Launch app
adb shell am start -n com.example.zcode/.MainActivity

# Monitor logs
adb logcat | grep -i "zcode"
```

## Release Checklist

Before publishing a release, verify:

- [ ] Version code and version name updated in `build.gradle.kts`
- [ ] All unit tests pass: `./gradlew test`
- [ ] App builds successfully: `./gradlew assembleRelease`
- [ ] APK installs on test devices
- [ ] App launches without crashes
- [ ] Core features work:
  - [ ] Terminal input/output
  - [ ] Command execution (built-in)
  - [ ] Linux environment creation
  - [ ] Real shell execution
  - [ ] File operations
  - [ ] Multiple sessions
  - [ ] Settings/themes
- [ ] No critical bugs
- [ ] ProGuard doesn't break functionality
- [ ] APK size is reasonable (< 20MB)
- [ ] No security vulnerabilities
- [ ] Documentation is up-to-date

## Optimizing Release Build

### Reduce APK Size

1. **Enable Additional Shrinking**
   ```kotlin
   buildTypes {
       release {
           isMinifyEnabled = true
           isShrinkResources = true
           
           // Additional optimizations
           proguardFiles(
               getDefaultProguardFile("proguard-android-optimize.txt"),
               "proguard-rules.pro"
           )
       }
   }
   ```

2. **Remove Unused Resources**
   - Check for unused drawables, layouts, strings
   - Remove debug/test resources from release build

3. **Use APK Splitting** (for Play Store)
   ```kotlin
   android {
       splits {
           abi {
               isEnable = true
               reset()
               include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
               isUniversalApk = true
           }
       }
   }
   ```

### Improve Performance

1. **R8 Optimization** (enabled by default)
   - Code shrinking
   - Resource shrinking
   - Obfuscation
   - Optimization

2. **Native Libraries**
   - Include only necessary architectures
   - Strip debug symbols (already configured)

## Troubleshooting Build Issues

### Network Issues During Build
```bash
# Build with cached dependencies
./gradlew assembleRelease --offline

# Clear Gradle cache if corrupted
./gradlew clean --refresh-dependencies
```

### ProGuard Errors
- Check `app/build/outputs/mapping/release/` for ProGuard mappings
- Add keep rules in `proguard-rules.pro` if needed
- Test with `minifyEnabled = false` to isolate ProGuard issues

### Build Failed
```bash
# Clean and rebuild
./gradlew clean
rm -rf .gradle/
./gradlew assembleRelease --stacktrace
```

### Dependency Resolution Issues
```bash
# Update Gradle wrapper
./gradlew wrapper --gradle-version=8.7

# Sync Gradle files
./gradlew --refresh-dependencies
```

## Publishing to GitHub Releases

### 1. Create Release Tag
```bash
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

### 2. Build Release APK
```bash
./gradlew assembleRelease
```

### 3. Create GitHub Release
1. Go to repository on GitHub
2. Click "Releases" > "Create a new release"
3. Choose the tag (v1.0.0)
4. Enter release title and description
5. Upload APK file:
   - `app/build/outputs/apk/release/app-release.apk`
6. Include:
   - Release notes
   - Changelog
   - Installation instructions
   - Screenshots (optional)
7. Publish release

### 4. Release Notes Template

```markdown
# Zcode Terminal v1.0.0

## What's New
- Full Linux terminal emulator for Android
- Real Linux environment support via PRoot/Termux
- Multiple terminal sessions
- 10 beautiful terminal themes
- Built-in command interpreter
- File operations and system utilities

## Features
- ✨ ANSI terminal emulation with color support
- 🐧 Real Linux shell (bash, sh)
- 📦 Package management (apt, pkg)
- 🎨 Customizable themes
- 📱 Responsive design for phones and tablets
- ⚡ Optimized performance

## Installation
1. Download `app-release.apk`
2. Enable "Install from Unknown Sources" in Android settings
3. Install APK
4. Open Zcode Terminal
5. Create Linux environment (optional)
6. Start using!

## Requirements
- Android 8.1+ (API 27+)
- 2GB RAM minimum
- 50-100MB storage

## Known Issues
- None reported

## Support
Report issues at: https://github.com/mithun789/Zcode/issues
```

## Continuous Integration

For automated builds, consider setting up GitHub Actions:

`.github/workflows/build.yml`:
```yaml
name: Build APK

on:
  push:
    tags:
      - 'v*'

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 11
        uses: actions/setup-java@v3
        with:
          distribution: 'temurin'
          java-version: '11'
      
      - name: Build Release APK
        run: |
          chmod +x ./gradlew
          ./gradlew assembleRelease
      
      - name: Upload APK
        uses: actions/upload-artifact@v3
        with:
          name: app-release
          path: app/build/outputs/apk/release/app-release.apk
```

## Final Steps

1. **Test thoroughly** on multiple devices/Android versions
2. **Update version** in build.gradle.kts
3. **Build release APK**
4. **Verify APK** works correctly
5. **Create GitHub release** with APK attached
6. **Update README** with download link
7. **Announce release** to users

## Build Output Locations

After successful build:
```
app/build/outputs/apk/
├── debug/
│   └── app-debug.apk           # Debug APK
└── release/
    └── app-release.apk          # Release APK (for distribution)

app/build/outputs/mapping/release/
├── mapping.txt                  # ProGuard mapping (save for crash reports)
├── seeds.txt                    # ProGuard seeds
└── usage.txt                    # ProGuard usage
```

## Version Management

Update version before each release in `app/build.gradle.kts`:
```kotlin
defaultConfig {
    versionCode = 2              // Increment for each release
    versionName = "1.0.1"        // Semantic versioning
}
```

## Conclusion

Following this guide ensures a properly built, optimized, and tested release APK ready for distribution via GitHub Releases or other channels.
