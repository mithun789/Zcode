# Zcode Terminal - Quick Start Guide

Welcome to Zcode Terminal! This guide will get you started quickly.

## For Users

### Installing the App

1. **Download APK**
   - Go to [GitHub Releases](https://github.com/mithun789/Zcode/releases/latest)
   - Download `Zcode-v1.0.0.apk` (or latest version)

2. **Install**
   - Open downloaded APK file
   - Tap "Install" (enable "Unknown Sources" if prompted)
   - Open Zcode Terminal

3. **First Run**
   - App opens to Terminal tab
   - Try basic commands:
     ```bash
     pwd
     ls
     echo "Hello World"
     whoami
     ```

### Creating Linux Environment

For full Linux functionality:

1. Go to **Linux** tab
2. Tap **"Create New Environment"**
3. Select **Alpine Linux** (fastest) or another distro
4. Wait for download and installation (requires internet)
5. Return to **Terminal** tab
6. Select your environment from dropdown at top
7. Now you have full Linux shell with bash, apt/pkg, etc!

### Testing Your Installation

Run the verification script to ensure everything works:

1. In Terminal tab, run:
   ```bash
   # Download the verification script
   curl -O https://raw.githubusercontent.com/mithun789/Zcode/main/verify-app.sh
   
   # Or if you have the file locally:
   bash verify-app.sh
   ```

2. The script will test:
   - Shell availability
   - File operations
   - Command execution
   - Environment variables
   - And 50+ other checks

### Quick Command Reference

#### Built-in Commands (work everywhere)
```bash
ls          # List files
pwd         # Show current directory
cd          # Change directory
echo        # Print text
cat         # View file contents
mkdir       # Create directory
touch       # Create file
rm          # Remove file/directory
cp          # Copy
mv          # Move
date        # Show date/time
whoami      # Show username
help        # List all commands
clear       # Clear screen
```

#### Linux Environment Commands (after creating environment)
```bash
bash        # Start bash shell
apt update  # Update package list
apt search  # Search packages
apt install # Install packages
which       # Find command location
uname -a    # System information
ps aux      # List processes
```

## For Developers

### Building from Source

1. **Prerequisites**
   - Android Studio
   - JDK 11+
   - Android SDK 36+

2. **Clone and Build**
   ```bash
   git clone https://github.com/mithun789/Zcode.git
   cd Zcode
   
   # Build debug
   ./gradlew assembleDebug
   
   # Build release
   ./build-release.sh
   ```

3. **Run Tests**
   ```bash
   ./gradlew test
   ```

### Project Structure

```
Zcode/
├── app/                    # Main application
├── terminal-emulator/      # Terminal emulation core
├── terminal-view/          # Compose UI components
├── TESTING_GUIDE.md        # How to test
├── LINUX_ENVIRONMENT_VERIFICATION.md  # Proof of real Linux
├── RELEASE_BUILD_GUIDE.md  # How to build releases
├── DEPLOYMENT_GUIDE.md     # How to deploy
└── build-release.sh        # Automated build script
```

### Key Documentation

- **Testing**: Read `TESTING_GUIDE.md`
- **Building**: Read `RELEASE_BUILD_GUIDE.md`
- **Deploying**: Read `DEPLOYMENT_GUIDE.md`
- **Project Status**: Read `PROJECT_STATUS_SUMMARY.md`
- **Work Done**: Read `WORK_COMPLETED.md`

## Common Tasks

### Change Terminal Theme

1. Go to **Settings** tab
2. Select **Theme**
3. Choose from 10 themes:
   - Dracula
   - Nord
   - Monokai
   - Tokyo Night
   - Gruvbox
   - One Dark
   - Solarized Dark
   - Catppuccin
   - Matrix
   - High Contrast

### Create Multiple Sessions

1. In Terminal tab
2. Tap **"+"** button at top
3. New session tab appears
4. Switch between sessions by tapping tabs

### Install Linux Packages

With Linux environment:
```bash
# Update package list
apt update

# Search for package
apt search nano

# Install package
apt install nano

# Use installed package
nano myfile.txt
```

## Troubleshooting

### App Won't Install
- Enable "Unknown Sources" in Android Settings
- Check you have Android 8.1+ (API 27+)
- Ensure enough storage space (100MB+)

### Commands Don't Work
- Some commands need Linux environment
- Create environment in Linux tab
- Select environment in Terminal tab dropdown
- Try commands again

### Linux Environment Won't Create
- Check internet connection
- Ensure 100MB+ free space
- Try Alpine (smallest distribution)
- Check Android kernel supports PRoot

### Terminal is Slow
- Reduce terminal buffer size in Settings
- Close unused sessions
- Use Alpine instead of Ubuntu/Debian
- Clear app cache

## Getting Help

- **Documentation**: Check markdown files in repository
- **Issues**: [GitHub Issues](https://github.com/mithun789/Zcode/issues)
- **Discussions**: [GitHub Discussions](https://github.com/mithun789/Zcode/discussions)

## Examples

### Example 1: File Management

```bash
# Create directory
mkdir my_project
cd my_project

# Create files
touch README.md
echo "# My Project" > README.md

# View file
cat README.md

# List files
ls -la
```

### Example 2: Text Processing

```bash
# Create test file
echo -e "apple\nbanana\ncherry\napple" > fruits.txt

# Search
grep "apple" fruits.txt

# Count lines
wc -l fruits.txt

# Sort and unique
sort fruits.txt | uniq
```

### Example 3: System Info

```bash
# Show system info
uname -a

# Show user
whoami

# Show date
date

# Show environment
env

# Show PATH
echo $PATH
```

### Example 4: Package Installation

```bash
# With Linux environment
apt update
apt search python3
apt install python3
python3 --version
```

## Next Steps

1. **Explore**: Try different commands
2. **Customize**: Change themes in Settings
3. **Experiment**: Create Linux environment
4. **Learn**: Read documentation files
5. **Contribute**: Submit issues or PRs on GitHub

## Quick Reference

| Task | Command |
|------|---------|
| List files | `ls` or `ls -la` |
| Change directory | `cd directory_name` |
| Show current path | `pwd` |
| Create file | `touch filename` |
| View file | `cat filename` |
| Edit file | `nano filename` (if installed) |
| Remove file | `rm filename` |
| Create directory | `mkdir dirname` |
| Copy | `cp source dest` |
| Move | `mv source dest` |
| Search text | `grep pattern file` |
| Show manual | `man command` (if available) |
| Clear screen | `clear` |
| Exit | `exit` |

## Tips

1. **Tab completion**: Type first letters and tap Tab
2. **Command history**: Use up/down arrows (if available)
3. **Copy/paste**: Long press in terminal
4. **Extra keys**: Use extra keys row for special chars
5. **Multiple sessions**: Keep different tasks in different tabs
6. **Save often**: File changes are persistent
7. **Experiment safely**: Work in your home directory

## Resources

- **Full Testing Guide**: `TESTING_GUIDE.md`
- **Linux Verification**: `LINUX_ENVIRONMENT_VERIFICATION.md`
- **Build Guide**: `RELEASE_BUILD_GUIDE.md`
- **Project Status**: `PROJECT_STATUS_SUMMARY.md`
- **GitHub**: https://github.com/mithun789/Zcode

---

**Welcome to Zcode Terminal!** 🎉

Enjoy your Linux environment on Android!
