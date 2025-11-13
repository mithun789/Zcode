# 🚀 Zcode Terminal Emulator

> A modern Android terminal emulator with advanced UI effects, networking capabilities, and comprehensive file management.

**Developed by [Mithun Kumar](https://github.com/mithun789)** - IT Student at SLIIT, Sri Lanka

[![GitHub](https://img.shields.io/badge/GitHub-mithun789/Zcode-blue)](https://github.com/mithun789/Zcode)
[![Status](https://img.shields.io/badge/Status-Phase%202%20Complete-success)](https://github.com/mithun789/Zcode)
[![Version](https://img.shields.io/badge/Version-1.0.0-blue)](https://github.com/mithun789/Zcode)
[![License](https://img.shields.io/badge/License-MIT-green)](https://github.com/mithun789/Zcode)
[![Android](https://img.shields.io/badge/Android-21+-brightgreen)](https://github.com/mithun789/Zcode)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple)](https://github.com/mithun789/Zcode)

## ✨ Features

### 🎨 Modern UI & Themes
- **Material Design 3** - Latest design system
- **3 Theme Modes** - Light, Dark, AMOLED
- **Glassmorphism Effects** - Blur, transparency, frosted glass
- **Bottom Navigation** - 4 main screens
- **Responsive Design** - All screen sizes supported

### 🛠️ Core Functionality
- **Theme Persistence** - Settings saved in Room database
- **Visual Effects Control** - Adjustable blur & transparency
- **Settings Screen** - Complete preferences UI
- **MVVM Architecture** - Clean code structure

### 🌐 Network & System
- **IP Address Detection** - IPv4/IPv6 support
- **NAT Bridge Manager** - Network mode switching
- **Fastfetch Integration** - System information display
- **Network Monitoring** - Real-time status

### 📂 File Management
- **File Explorer Backend** - Browse, search, operations
- **Bookmark System** - Quick access favorites
- **Permission Handling** - Smart access control

### 🏗️ Architecture
- **Jetpack Compose** - Modern declarative UI
- **Room Database** - Local data persistence
- **Hilt DI** - Dependency injection
- **Coroutines & Flow** - Asynchronous operations

---

## 🚀 Quick Start

### Prerequisites
- **Android Studio** Giraffe or newer
- **JDK** 11+
- **Android SDK** API 21+ (Android 5.0+)

### Installation

```bash
# Clone the repository
git clone https://github.com/mithun789/Zcode.git
cd Zcode

# Open in Android Studio
# File → Open → Select Zcode folder

# Build and run
./gradlew clean build
./gradlew installDebug
```
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

## 📊 Project Status

| Component | Status | Notes |
|-----------|--------|-------|
| **UI Framework** | ✅ Complete | Compose + Material 3 |
| **Theme System** | ✅ Complete | 3 themes with persistence |
| **Settings Screen** | ✅ Complete | Full UI controls |
| **Visual Effects** | ✅ Complete | Blur, transparency |
| **Network Module** | ✅ Complete | IP detection, NAT bridge |
| **File Explorer** | ✅ Complete | Backend operations |
| **System Info** | ✅ Complete | Fastfetch ready |
| **Terminal Emulator** | 🔄 In Progress | Placeholder UI |
| **File Explorer UI** | 🔄 In Progress | Placeholder UI |
| **System Info UI** | 🔄 In Progress | Placeholder UI |

## 💾 Tech Stack

```kotlin
// Core Framework
composeBom = "2024.10.01"
composeMaterial3 = "1.2.1"
kotlin = "2.0.21"

// Data & Architecture
room = "2.6.1"
hilt = "2.50"
lifecycle = "2.8.4"
navigationCompose = "2.8.0"
```

## 📁 Project Structure

```
Zcode/
├── app/src/main/java/com/example/zcode/
│   ├── MainActivity.kt              # App entry point
│   ├── ZcodeApplication.kt          # Hilt application
│   ├── ui/
│   │   ├── screens/                 # Compose screens
│   │   ├── theme/                   # Material 3 themes
│   │   └── viewmodel/               # State management
│   ├── data/
│   │   ├── database/                # Room entities/DAO
│   │   └── manager/                 # Business logic
│   ├── network/                     # IP, NAT handlers
│   ├── file_explorer/               # File operations
│   ├── fastfetch/                   # System info
│   ├── terminal/                    # Terminal logic
│   └── di/                          # Hilt modules
├── terminal-emulator/               # Terminal library
├── terminal-view/                   # Terminal UI library
└── docs/README.md                   # This file
```

## 🎯 Roadmap

### ✅ Phase 2 - Core Framework (COMPLETE)
- Modern UI with Jetpack Compose
- Material Design 3 implementation
- Theme system with persistence
- Settings and visual effects
- Core feature modules

### 🔄 Phase 3 - UI Implementation (IN PROGRESS)
- Terminal emulator interface
- File explorer UI
- System info display
- Network monitoring UI

### ⏳ Phase 4 - Advanced Features (PLANNED)
- Custom theme creator
- Cloud integration
- Advanced terminal features
- Performance optimization

## 🤝 Contributing

We welcome contributions! Please:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

### Development Setup
```bash
# Prerequisites
Android Studio Giraffe+
JDK 11+
Android SDK 21+

# Build commands
./gradlew clean          # Clean build
./gradlew build          # Full build
./gradlew test           # Run tests
./gradlew installDebug   # Install debug APK
```

## 📞 Support & Issues

- **GitHub Issues**: [Report bugs](https://github.com/mithun789/Zcode/issues)
- **Discussions**: [Q&A](https://github.com/mithun789/Zcode/discussions)
- **Documentation**: Check inline code comments

### Debugging
```bash
# View logs
adb logcat | grep com.example.zcode

# Check database
adb shell sqlite3 /data/data/com.example.zcode/databases/zcode_database
```

## 🙏 Acknowledgments

**Developer**: Mithun Kumar ([@mithun789](https://github.com/mithun789))
- IT Student at SLIIT, Sri Lanka
- Passionate about Android development

**Built with**:
- Android Jetpack Components
- Kotlin Programming Language
- Material Design Guidelines
- Open source community

## 📝 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

---

## ⭐ Give it a Star!

If you find Zcode useful, please give it a ⭐ on GitHub!

[![GitHub stars](https://img.shields.io/github/stars/mithun789/Zcode?style=social)](https://github.com/mithun789/Zcode)

---

**Last Updated**: November 13, 2025  
**Version**: 1.0.0 - Phase 2 Complete  
**Repository**: https://github.com/mithun789/Zcode

Made with ❤️ by Mithun Kumar for the Android community 🚀

