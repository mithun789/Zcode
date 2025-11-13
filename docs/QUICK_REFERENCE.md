# Zcode - Quick Reference Guide

## 🚀 Quick Start

```bash
# Clone the project
cd C:\Users\User\Documents\Zcode

# Sync Gradle
./gradlew sync

# Build
./gradlew build

# Run
./gradlew installDebug
```

## 📱 App Structure

```
MainScreen
├── Terminal (tab 1)
├── Files (tab 2)
├── System Info (tab 3)
└── Settings (tab 4) ✅ Implemented
    ├── Theme Selector
    ├── Blur Intensity Slider
    ├── Transparency Slider
    └── Glassmorphism Toggle
```

## 🎨 Theme Usage

### Apply Theme to Content
```kotlin
@Composable
fun App() {
    ZcodeTheme(darkTheme = false, amoledTheme = false) {
        // Your content
    }
}
```

### Change Theme in Settings
```kotlin
viewModel.setThemeMode(ThemeMode.DARK)  // Light, Dark, AMOLED
```

### Access Current Theme
```kotlin
val theme by viewModel.currentTheme.collectAsState()
```

## 💾 Database Operations

### Initialize Preferences
```kotlin
themeManager.initializeDefaultPreferences()
```

### Update Theme
```kotlin
themeManager.setThemeMode(ThemeMode.DARK)
```

### Update Visual Effects
```kotlin
themeManager.setBlurIntensity(15f)           // 0-20
themeManager.setTransparencyLevel(0.9f)      // 0.0-1.0
themeManager.setGlassmorphismEnabled(true)
```

### Get Network Settings
```kotlin
val natMode = dao.getUserPreferencesSync()?.natBridgeMode
```

## 🌐 Network Operations

### Get IP Addresses
```kotlin
val ipv4 = ipAddressHandler.getIPv4Address()
val ipv6 = ipAddressHandler.getIPv6Address()
val info = ipAddressHandler.getNetworkInfo()
```

### NAT Bridge
```kotlin
natBridgeManager.setNATMode(NATBridgeMode.IPv4)
natBridgeManager.addPortForwardingRule(8080, "192.168.1.1", 80)
val status = natBridgeManager.getStatus()
```

## 📂 File Operations

### Browse Files
```kotlin
val files = fileExplorer.listFiles(path, showHidden = true)
fileExplorer.navigateToDirectory("/sdcard")
fileExplorer.navigateToParent()
```

### File Operations
```kotlin
fileExplorer.copyFile(source, destination)
fileExplorer.moveFile(source, destination)
fileExplorer.deleteFile(path, recursive = true)
fileExplorer.createDirectory(path)
```

### Bookmarks
```kotlin
fileExplorer.addBookmark("/sdcard/Documents")
val bookmarks = fileExplorer.getBookmarks()
fileExplorer.removeBookmark("/sdcard/Documents")
```

### Search
```kotlin
val results = fileExplorer.searchFiles("*.apk", maxResults = 50)
```

## 📊 System Information

### Get System Info
```kotlin
val systemInfo = fastfetch.getSystemInfo()
// Returns: SystemInfo with OS, CPU, RAM, Storage, Battery, etc.
```

### Format Output
```kotlin
val output = fastfetch.formatAsNeofetch(systemInfo)
// Neofetch-style output string
```

## 🎛️ UI Effects

### Apply Blur
```kotlin
val blurRadius = GlassmorphismRenderer.getBlurRadiusFromIntensity(10f)
val modifier = GlassmorphismRenderer.glassPanel(
    blurRadius = blurRadius,
    alpha = 0.85f
)
```

### Transparency
```kotlin
val color = TransparencyManager.getColorWithAlpha(Color.Black, 0.8f)
val blended = TransparencyManager.blendColors(fg, bg, 0.5f)
```

## 🏗️ Dependency Injection

### Inject Services
```kotlin
@AndroidEntryPoint
class MyActivity : AppCompatActivity() {
    private val themeManager: ThemeManager by inject()
    private val fileExplorer: FileExplorer by inject()
}
```

### In ViewModel
```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val themeManager: ThemeManager,
    private val ipHandler: IPAddressHandler
) : ViewModel()
```

### In Composable
```kotlin
@Composable
fun MyScreen(
    viewModel: MyViewModel = hiltViewModel()
) {
    // Use viewModel
}
```

## 📁 File Locations

| Purpose | Path |
|---------|------|
| Main Activity | `app/src/main/java/com/example/zcode/MainActivity.kt` |
| Theme System | `app/src/main/java/com/example/zcode/ui/theme/` |
| Database | `app/src/main/java/com/example/zcode/data/database/` |
| UI Effects | `app/src/main/java/com/example/zcode/ui/effects/` |
| Network | `app/src/main/java/com/example/zcode/network/` |
| Files | `app/src/main/java/com/example/zcode/file_explorer/` |
| System Info | `app/src/main/java/com/example/zcode/fastfetch/` |
| DI Config | `app/src/main/java/com/example/zcode/di/` |
| Resources | `app/src/main/res/` |

## 🔧 Debugging

### Check Database
```bash
adb shell sqlite3 /data/data/com.example.zcode/databases/zcode_database
sqlite> SELECT * FROM user_preferences;
```

### View Logs
```bash
adb logcat | grep zcode
```

### Check Network
```bash
adb shell ifconfig
adb shell netstat
```

## 📦 Key Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| Jetpack Compose | 2024.10.01 | UI Framework |
| Material Design 3 | 1.2.1 | Design System |
| Room | 2.6.1 | Database |
| Hilt | 2.50 | Dependency Injection |
| Kotlin Coroutines | 1.9 | Async Operations |
| Navigation Compose | 2.8.0 | Screen Navigation |

## 🎯 Common Tasks

### Add New Screen
1. Create composable in `ui/screens/`
2. Add to `NavigationItem` sealed class
3. Add route to `ZcodeNavGraph`
4. Add bottom nav item

### Add New Setting
1. Add property to `UserPreferences` entity
2. Add DAO method in `UserPreferencesDao`
3. Add manager method in `ThemeManager`
4. Add UI control in `SettingsScreen`

### Add New Feature
1. Create package in `app/src/main/java/com/example/zcode/`
2. Create Hilt module in `di/`
3. Inject in activity/viewmodel
4. Use in composables

## ⚠️ Common Issues

| Issue | Solution |
|-------|----------|
| Gradle sync fails | Run `./gradlew clean` then sync |
| Hilt errors | Add `@AndroidEntryPoint` to activities |
| Database errors | Clear app data in settings |
| Navigation not working | Check route names match |
| Theme not changing | Verify Flow observer is active |
| Network errors | Check permissions in manifest |
| File operations fail | Check file permissions |

## 📚 Documentation Links

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Material Design 3](https://m3.material.io/)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Hilt Injection](https://developer.android.com/training/dependency-injection/hilt-android)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

## 🆘 Need Help?

1. Check `PHASE2_COMPLETE.md` for detailed info
2. Check `IMPLEMENTATION_GUIDE.md` for setup
3. Review KDoc comments in source files
4. Check inline code comments

---

**Last Updated**: November 13, 2025
**Version**: 1.0 - Phase 2 Complete

