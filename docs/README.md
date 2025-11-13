# 🚀 Zcode Terminal Emulator

> An advanced Android terminal emulator with modern UI effects, networking features, and comprehensive file management.

![Status](https://img.shields.io/badge/Status-Phase%202%20Complete-success)
![Version](https://img.shields.io/badge/Version-1.0-blue)
![License](https://img.shields.io/badge/License-MIT-green)
![Android](https://img.shields.io/badge/Android-21+-brightgreen)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple)

## ✨ Features

### 🎨 User Interface
- **Bottom Navigation** - 4 main screens for easy navigation
- **Material Design 3** - Modern design system implementation
- **3 Complete Themes** - Light, Dark, and AMOLED options
- **Glassmorphism Effects** - Modern frosted glass UI elements
- **Responsive Design** - Adapts to all screen sizes

### 🛠️ Settings & Customization
- **Theme Switching** - Instant theme changes with persistence
- **Visual Effects Control** - Adjust blur, transparency, and effects
- **User Preferences** - All settings automatically saved
- **Settings Screen** - Complete UI for preferences

### 🌐 Network Features
- **IP Address Detection** - Get IPv4 and IPv6 addresses
- **Network Monitoring** - Real-time network status
- **NAT Bridge Manager** - IPv4/IPv6 mode switching
- **Port Forwarding** - Configure custom port rules

### 📂 File Management
- **File Explorer** - Browse directories with sorting
- **File Operations** - Copy, move, delete, create
- **Search Functionality** - Recursive file search
- **Bookmarks System** - Quick access to favorites
- **Permissions** - Smart permission checking

### 📊 System Information
- **Fastfetch Integration** - Display system specs
- **OS Information** - Android version and build details
- **Device Details** - Model, manufacturer info
- **Hardware Info** - CPU cores, RAM, storage
- **Neofetch Format** - Traditional system info display

### 🎯 Architecture
- **MVVM Pattern** - Clean separation of concerns
- **Jetpack Compose** - Modern declarative UI
- **Room Database** - Local data persistence
- **Hilt DI** - Dependency injection framework
- **Coroutines** - Asynchronous operations

---

## 🚀 Quick Start

### Prerequisites
- Android Studio Giraffe or newer
- JDK 11 or higher
- Android SDK 21+ (API Level 21)
- Gradle 8.0+

### Installation

1. **Clone the repository**
   ```bash
   cd C:\Users\User\Documents\Zcode
   ```

2. **Open in Android Studio**
   ```
   File → Open → Select Zcode folder
   ```

3. **Sync Gradle**
   ```
   File → Sync Now
   ```

4. **Build the project**
   ```bash
   ./gradlew clean build
   ```

5. **Run on emulator/device**
   ```bash
   ./gradlew installDebug
   ```

---

## 📁 Project Structure

```
Zcode/
├── app/                                    # Main application module
│   ├── src/main/
│   │   ├── java/com/example/zcode/
│   │   │   ├── MainActivity.kt             # Main entry point
│   │   │   ├── ZcodeApplication.kt         # App initialization
│   │   │   ├── ui/
│   │   │   │   ├── screens/                # Screen composables
│   │   │   │   ├── theme/                  # Material Design 3
│   │   │   │   └── viewmodel/              # State management
│   │   │   ├── data/
│   │   │   │   ├── database/               # Room database
│   │   │   │   └── manager/                # Business logic
│   │   │   ├── ui/effects/                 # Visual effects
│   │   │   ├── network/                    # Network features
│   │   │   ├── file_explorer/              # File management
│   │   │   ├── fastfetch/                  # System info
│   │   │   └── di/                         # Dependency injection
│   │   └── res/                            # Resources
│   └── build.gradle.kts                    # Build config
├── gradle/
│   └── libs.versions.toml                  # Dependency versions
├── docs/
│   └── README.md                           # This file
└── README.md                               # This file
```

---

## 🎯 Current Features Status

| Feature | Status | Details |
|---------|--------|---------|
| Bottom Navigation | ✅ Complete | 4 screens implemented |
| Theme System | ✅ Complete | Light, Dark, AMOLED |
| Settings Screen | ✅ Complete | Full UI with controls |
| Visual Effects | ✅ Complete | Blur, transparency, glass effects |
| Network Module | ✅ Complete | IP detection, NAT bridge |
| File Explorer | ✅ Complete | Browse, search, bookmark |
| System Info | ✅ Complete | Fastfetch integration ready |
| Terminal Screen | 🔄 In Progress | Placeholder ready |
| File Explorer UI | 🔄 In Progress | Placeholder ready |
| System Info UI | 🔄 In Progress | Placeholder ready |

---

## 💾 Technology Stack

### Framework
- **Jetpack Compose** - Modern declarative UI framework
- **Material Design 3** - Latest material design system
- **Android 12+** - Target API 36

### Data & Storage
- **Room Database** - SQLite wrapper for type-safe access
- **Kotlin Coroutines** - Asynchronous operations
- **Flow** - Reactive streams

### Architecture
- **MVVM Pattern** - Separation of concerns
- **Repository Pattern** - Data abstraction
- **Hilt** - Dependency injection
- **ViewModel** - State management

### Libraries
```toml
composeBom = "2024.10.01"
composeMaterial3 = "1.2.1"
room = "2.6.1"
hilt = "2.50"
lifecycleVersion = "2.8.4"
navigationCompose = "2.8.0"
kotlin = "2.0.21"
```

---

## 🎨 Theme System

### Available Themes
- **Light Theme** - Clean, bright interface for daytime use
- **Dark Theme** - Low-light optimized colors
- **AMOLED Theme** - True black background for OLED displays

### Visual Effects
- **Blur Intensity** - Adjustable 0-20dp
- **Transparency** - Adjustable 0.0-1.0
- **Glassmorphism** - Frosted glass effects toggle

### Usage
```kotlin
@Composable
fun App() {
    ZcodeTheme(darkTheme = false, amoledTheme = false) {
        // Your content
    }
}
```

---

## 🗄️ Database

### Persistence
- User preferences stored in Room database
- Automatic initialization with defaults
- Reactive updates via Flow

### Data Stored
- Theme preference (Light/Dark/AMOLED)
- Visual effects settings (blur, transparency)
- Network settings (NAT mode)
- File explorer preferences
- System info settings

---

## 🌐 Network Features

### IP Address Handler
```kotlin
val ipv4 = ipAddressHandler.getIPv4Address()
val ipv6 = ipAddressHandler.getIPv6Address()
val networkInfo = ipAddressHandler.getNetworkInfo()
```

### NAT Bridge Manager
```kotlin
natBridgeManager.setNATMode(NATBridgeMode.IPv4)
natBridgeManager.addPortForwardingRule(8080, "192.168.1.1", 80)
```

---

## 📂 File Operations

### File Explorer
```kotlin
val files = fileExplorer.listFiles(path, showHidden = true)
fileExplorer.copyFile(source, destination)
fileExplorer.searchFiles("*.apk", maxResults = 50)
```

---

## 🛠️ Debugging

### View Logs
```bash
adb logcat | grep com.example.zcode
```

### Check Database
```bash
adb shell sqlite3 /data/data/com.example.zcode/databases/zcode_database
sqlite> SELECT * FROM user_preferences;
```

---

## 🤝 Contributing

Contributions are welcome! Please:
1. Follow the existing code style
2. Add documentation for new features
3. Test thoroughly before submitting
4. Update relevant documentation

### Code Guidelines
- Use Kotlin conventions
- Add KDoc comments
- Implement error handling
- Use type safety
- Proper null handling

---

## 📝 License

This project is licensed under the MIT License - see LICENSE file for details.

---

## 🙏 Acknowledgments

Built with:
- Android Framework and Libraries
- Jetpack Components
- Material Design
- Kotlin Language
- Community best practices

---

## 📊 Project Statistics

| Metric | Value |
|--------|-------|
| Kotlin Files | 18+ |
| Lines of Code | 2,200+ |
| Data Classes | 8 |
| Composables | 15+ |
| Documentation | 1,000+ lines |
| Supported APIs | 21+ |
| Min SDK | 21 |
| Target SDK | 36 |

---

## 🎯 Roadmap

### Phase 2 ✅ COMPLETE
- ✅ UI Framework with Compose
- ✅ Material Design 3 Implementation
- ✅ Theme System with Persistence
- ✅ Settings Screen
- ✅ Core Feature Modules

### Phase 3 🔄 IN PROGRESS
- 🔄 Terminal Emulator Implementation
- 🔄 File Explorer UI
- 🔄 System Info Display
- 🔄 Network Monitoring UI

### Phase 4 ⏳ PLANNED
- ⏳ Advanced Terminal Features
- ⏳ Custom Themes Creator
- ⏳ Cloud Integration
- ⏳ Advanced Networking

### Phase 5 ⏳ PLANNED
- ⏳ Performance Optimization
- ⏳ Extended Testing
- ⏳ Release Preparation

---

## 📞 Support

For issues or questions:
1. Check inline code documentation
2. Review code comments
3. See project structure

---

## ⭐ Star History

Help us grow! Give this project a ⭐ if you find it useful.

---

## 📱 Supported Devices

- **Minimum SDK**: Android 5.0 (API 21)
- **Target SDK**: Android 15 (API 36)
- **Recommended**: Android 12+ (API 31+)
- **Screen Sizes**: All (phones, tablets, foldables)

---

## 🎉 Getting Started

```bash
# Clone
cd C:\Users\User\Documents\Zcode

# Build
./gradlew clean build

# Run
./gradlew installDebug
```

**That's it! Enjoy Zcode!** 🚀

---

**Last Updated**: November 13, 2025
**Version**: 1.0 - Phase 2 Complete
**Status**: Ready for Testing and Phase 3 Implementation

Made with ❤️ for the Android community

