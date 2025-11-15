# Zcode Terminal - Linux Shell on Android

A powerful Android terminal emulator with integrated Linux environment support, allowing you to run real Linux commands directly on your Android device.

## Features

✨ **Core Features:**
- 🖥️ **Real Terminal Emulator** - Full ANSI escape sequence support with proper rendering
- 🐧 **Linux Environments** - Support for Alpine, Ubuntu, Debian, Fedora, and Arch Linux
- 🎨 **Multiple Themes** - 10 beautiful terminal themes (Dracula, Nord, Monokai, Tokyo Night, etc.)
- 📱 **Responsive Design** - Optimized for both phones and tablets
- ⚡ **Performance Optimized** - Smooth scrolling and fast rendering
- 🎯 **Process-based Execution** - Real Linux commands via integrated shell

## Getting Started

### Installation

1. Download the latest APK from [GitHub Releases](https://github.com/mithun789/Zcode/releases)
2. Install on your Android device (API 27+)
3. Grant necessary permissions when prompted

### Creating Your First Linux Environment

1. Open Zcode and navigate to the **Linux** tab
2. Tap the **Create** button
3. Select a Linux distribution (Alpine recommended for minimal size)
4. Wait for the environment to be created and extracted
5. Go back to **Terminal** tab and select your environment
6. Start using Linux commands!

### Available Commands

The terminal supports both built-in commands and real Linux utilities:

**File Operations:**
```
ls, ll, pwd, cd, cat, mkdir, touch, rm, cp, mv, find, grep, tree, head, tail, wc
```

**System Info:**
```
date, whoami, uname, ps, df, free, neofetch, env, history
```

**Package Management:**
```
apt, pkg (package managers for Ubuntu/Debian)
```

## Architecture

### Terminal System
- **Dual-mode Terminal**: Built-in command interpreter + Real Linux shell via PRoot/Direct shell
- **TerminalSession**: Handles I/O streams, command processing, and ANSI rendering
- **TerminalView**: Jetpack Compose UI with optimized rendering and input handling
- **TerminalEmulator**: Core ANSI escape sequence parser and screen buffer

### Linux Environments
- **PRoot Containerization**: Sandboxed Linux environment support
- **Fallback Mechanisms**: Works without PRoot using direct shell
- **Multiple Distributions**: Alpine, Ubuntu, Debian, Fedora, Arch
- **Minimal Footprint**: Alpine rootfs is only ~3MB

### Data & Theme System
- **Room Database**: Persistent storage for themes and preferences
- **ThemeManager**: Centralized theme management
- **Theme Persistence**: Terminal theme preferences saved automatically
- **10 Pre-built Themes**: Professionally designed color schemes

## Technologies

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Database**: Room
- **Dependency Injection**: Hilt
- **Architecture**: MVVM with Repository pattern
- **Terminal Emulation**: Custom ANSI parser with 80x24 default grid

## Project Structure

```
Zcode/
├── app/                          # Main application module
│   ├── src/main/java/.../
│   │   ├── MainActivity.kt       # Navigation hub
│   │   ├── ZcodeApplication.kt   # App initialization
│   │   ├── data/
│   │   │   ├── database/         # Room entities & DAOs
│   │   │   └── manager/          # ThemeManager
│   │   ├── linux/
│   │   │   └── LinuxEnvironmentManager.kt  # Environment management
│   │   ├── ui/
│   │   │   ├── screens/
│   │   │   │   ├── TerminalScreen.kt      # Terminal UI
│   │   │   │   ├── LinuxEnvironmentsScreen.kt
│   │   │   │   └── other screens...
│   │   │   ├── theme/            # Themes & styling
│   │   │   └── viewmodel/        # ViewModels
│   │   └── di/
│   │       └── HiltModules.kt    # DI configuration
│   └── build.gradle.kts
├── terminal-emulator/            # ANSI terminal emulator core
│   └── src/main/java/.../
│       ├── TerminalSession.kt     # Session management
│       └── TerminalEmulator.kt    # ANSI parser
├── terminal-view/                # Jetpack Compose UI
│   └── src/main/java/.../
│       └── TerminalView.kt        # Terminal display component
└── build.gradle.kts
```

## Key Features In-Depth

### Terminal Emulator
- Full ANSI escape sequence support
- 80x24 default grid (configurable)
- Color support (16-256 colors)
- Cursor positioning and control
- Text attributes (bold, italic, underline, reverse)

### Linux Environments
- **Alpine Linux**: Lightweight (~3MB), ideal for phones
- **Ubuntu/Debian**: Full package management with apt
- **Fedora/Arch**: RPM/Pacman support
- **Environment Isolation**: Each environment is self-contained
- **Persistent Storage**: Environments saved between sessions

### Theme System
Built-in themes:
- Dracula
- Nord
- Monokai
- Gruvbox
- One Dark
- Tokyo Night
- Solarized Dark
- Catppuccin
- Matrix
- High Contrast

### Performance Optimizations
- Lazy terminal buffer rendering
- Hash-based change detection
- Optimized input handling
- Memory-efficient screen buffering
- Code shrinking and resource optimization in release builds

## Building from Source

### Prerequisites
- Android Studio (latest)
- Android SDK 36+
- Kotlin 1.9.22+
- JDK 11+

### Build Steps

```bash
# Clone repository
git clone https://github.com/mithun789/Zcode.git
cd Zcode

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Or use the automated build script
./build-release.sh

# Run tests
./gradlew test
```

Build output:
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release.apk`

### Detailed Build Instructions

See [RELEASE_BUILD_GUIDE.md](RELEASE_BUILD_GUIDE.md) for comprehensive build instructions including:
- Release signing configuration
- APK optimization
- Publishing to GitHub Releases
- Troubleshooting build issues

### Testing

See [TESTING_GUIDE.md](TESTING_GUIDE.md) for comprehensive testing procedures including:
- Unit tests
- Integration tests
- Linux environment verification
- Performance testing
- Manual test scenarios

## Requirements

- **Android**: API 27+ (Android 8.1 and above)
- **RAM**: 2GB minimum recommended
- **Storage**: 50MB for basic installation + ~100MB per Linux environment
- **Network**: Required for downloading Linux environments (can work offline after first setup)

## Troubleshooting

### Linux Environment Won't Start
1. Ensure you have created an environment in the Linux tab
2. Check available storage (need at least 100MB)
3. Verify network connectivity (for first environment creation)
4. The app will fallback to built-in shell if Linux setup fails

### Slow Performance
1. Try reducing terminal grid size
2. Close other applications
3. Clear app cache: Settings > Apps > Zcode > Storage > Clear Cache
4. Consider using Alpine Linux for minimal overhead

### Commands Not Found
1. Make sure you selected a Linux environment (not built-in)
2. Try `echo $PATH` to check PATH
3. Some Android kernels restrict certain system calls

## Limitations

- No real sudo/root access (sandboxed environment)
- PRoot may not work on all Android kernels
- Some system calls may be blocked by SELinux
- Performance varies based on device and Linux environment size

## Future Roadmap

- [ ] SSH/SFTP support
- [ ] File transfer between environments
- [ ] Script execution and automation
- [ ] Terminal session management and restoration
- [ ] Plugin system for custom themes
- [ ] Cloud sync for environment backups
- [ ] Multi-window terminal support
- [ ] Custom keyboard shortcuts

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bugs and feature requests.

## License

This project is licensed under the MIT License - see LICENSE file for details.

## Credits

- Terminal emulation based on Termux terminal-emulator
- ANSI parsing inspired by Xterm standards
- Theme designs adapted from popular terminal themes

## Support

For issues, questions, or suggestions:
1. Open an issue on [GitHub Issues](https://github.com/mithun789/Zcode/issues)
2. Check existing documentation and FAQs
3. Review terminal logs in Debug mode

---

**Zcode v1.0.0** | Made with ❤️ for Android developers and Linux enthusiasts
