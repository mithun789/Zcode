# Zcode Terminal - Deployment Guide

This guide walks through the complete process of deploying Zcode Terminal to GitHub Releases.

## Prerequisites

Before deploying, ensure:
- [x] All code changes are committed
- [x] All tests pass
- [x] Build works successfully
- [x] App has been tested on real devices
- [x] Documentation is up-to-date
- [x] Version numbers are correct

## Step-by-Step Deployment Process

### Step 1: Prepare the Release

1. **Update Version Numbers**

   Edit `app/build.gradle.kts`:
   ```kotlin
   defaultConfig {
       versionCode = 1         // Increment for each release
       versionName = "1.0.0"   // Semantic versioning
   }
   ```

2. **Update Documentation**

   Update version references in:
   - `README.md`
   - `RELEASE_NOTES.md`
   - Any other version-specific docs

3. **Run All Tests**
   ```bash
   ./gradlew test
   ./gradlew connectedAndroidTest  # If device available
   ```

4. **Build Debug APK for Testing**
   ```bash
   ./gradlew assembleDebug
   ```

5. **Test on Multiple Devices**
   - Install on at least 2-3 different Android versions
   - Test all major features
   - Verify Linux environment works
   - Check for crashes or bugs

### Step 2: Build Release APK

1. **Clean Previous Builds**
   ```bash
   ./gradlew clean
   ```

2. **Build Release APK**

   Option A - Using automated script:
   ```bash
   ./build-release.sh
   ```

   Option B - Manual build:
   ```bash
   ./gradlew assembleRelease
   ```

3. **Verify Build Output**
   ```bash
   ls -lh app/build/outputs/apk/release/app-release.apk
   ```

4. **Test Release APK**
   ```bash
   # Install on device
   adb install -r app/build/outputs/apk/release/app-release.apk
   
   # Launch and test
   adb shell am start -n com.example.zcode/.MainActivity
   ```

### Step 3: Create Release Tag

1. **Commit All Changes**
   ```bash
   git add .
   git commit -m "Prepare release v1.0.0"
   git push origin main  # or your branch
   ```

2. **Create and Push Tag**
   ```bash
   git tag -a v1.0.0 -m "Zcode Terminal v1.0.0 - Initial Release"
   git push origin v1.0.0
   ```

### Step 4: Create GitHub Release

1. **Navigate to GitHub Repository**
   - Go to https://github.com/mithun789/Zcode
   - Click on "Releases" tab
   - Click "Create a new release"

2. **Configure Release**
   - **Tag**: Select `v1.0.0` (the tag you just pushed)
   - **Release Title**: `Zcode Terminal v1.0.0`
   - **Description**: See template below

3. **Upload APK**
   - Click "Attach binaries by dropping them here or selecting them"
   - Upload `app/build/outputs/apk/release/app-release.apk`
   - Optionally rename to `Zcode-v1.0.0.apk` for clarity

4. **Add Additional Files** (optional)
   - `verify-app.sh` - Verification script for users
   - `RELEASE_NOTES.md` - Detailed changelog
   - Screenshots of the app

### Step 5: Write Release Notes

Use this template for release description:

```markdown
# Zcode Terminal v1.0.0

A powerful Linux terminal emulator for Android with full Linux environment support.

## 🎉 What's New

This is the initial release of Zcode Terminal, featuring:

### Core Features
- **Real Linux Terminal Emulator** with full ANSI/VT100 support
- **Linux Environment Support** via PRoot and Termux integration
- **Multiple Distributions**: Ubuntu, Debian, Alpine, Fedora, Arch Linux
- **Multiple Sessions**: Tab-based terminal sessions
- **10 Beautiful Themes**: Dracula, Nord, Monokai, Tokyo Night, and more
- **Built-in Commands**: 30+ Linux commands work out of the box

### Technical Highlights
- 📱 Modern UI built with Jetpack Compose
- 🎨 Customizable themes and color schemes
- ⚡ Optimized for performance
- 🔒 Sandboxed Linux environments
- 💾 Persistent storage for environments
- 🔄 Session management and restoration

## 📥 Installation

### Requirements
- Android 8.1+ (API 27 or higher)
- 2GB RAM minimum
- 50-100MB storage space

### Steps
1. Download `Zcode-v1.0.0.apk` from the Assets section below
2. Enable "Install from Unknown Sources" in Android Settings
3. Open the downloaded APK file
4. Tap "Install"
5. Open Zcode Terminal

## 🚀 Quick Start

### Using Built-in Terminal
1. Open the app
2. Navigate to **Terminal** tab
3. Start typing commands (ls, pwd, echo, cat, etc.)

### Creating Linux Environment
1. Go to **Linux** tab
2. Tap **"Create New Environment"**
3. Select a distribution (Alpine recommended for beginners)
4. Wait for download and installation
5. Return to **Terminal** tab
6. Select your environment from dropdown
7. Enjoy full Linux shell!

## 📚 Documentation

- **[Testing Guide](TESTING_GUIDE.md)**: How to test and verify functionality
- **[Linux Verification](LINUX_ENVIRONMENT_VERIFICATION.md)**: Proof of real Linux environment
- **[Build Guide](RELEASE_BUILD_GUIDE.md)**: How to build from source

## ✅ Verification

To verify the app works correctly, run the verification script:
1. Download `verify-app.sh` from Assets
2. Copy to Zcode Terminal
3. Run: `bash verify-app.sh`

Or manually test:
```bash
# Test basic commands
pwd
ls -la
echo "Hello World"

# Test file operations
touch test.txt
echo "test" > test.txt
cat test.txt

# Test environment
echo $SHELL
echo $PATH
```

## 🐛 Known Issues

None reported in this release.

## 🔮 Upcoming Features

- SSH/SFTP support
- File synchronization
- Script automation
- Plugin system
- Cloud backup

## 🙏 Credits

- Terminal emulation based on Termux
- ANSI parsing inspired by Xterm
- Theme designs from popular terminal themes

## 📝 Changelog

See [RELEASE_NOTES.md](RELEASE_NOTES.md) for detailed changelog.

## 💬 Support

- **Report Issues**: [GitHub Issues](https://github.com/mithun789/Zcode/issues)
- **Discussions**: [GitHub Discussions](https://github.com/mithun789/Zcode/discussions)

## 📄 License

MIT License - See [LICENSE](LICENSE) file for details.

---

**Download APK**: See Assets section below ⬇️

**Star ⭐ this repo** if you find it useful!

Made with ❤️ for the Android and Linux community
```

### Step 6: Publish Release

1. **Review Everything**
   - Check release title and version
   - Verify description is complete
   - Ensure APK is attached
   - Verify tag is correct

2. **Choose Release Type**
   - ✅ **"Set as the latest release"** for stable releases
   - ⬜ "Set as a pre-release" for beta versions

3. **Publish**
   - Click **"Publish release"**
   - Release is now live!

### Step 7: Post-Release Tasks

1. **Update README**
   
   Add download link:
   ```markdown
   ## Download
   
   Download the latest version from [GitHub Releases](https://github.com/mithun789/Zcode/releases/latest)
   
   Latest version: **v1.0.0**
   ```

2. **Announce Release**
   - Create GitHub Discussion
   - Post on social media (if applicable)
   - Update any related documentation

3. **Monitor Issues**
   - Watch for bug reports
   - Respond to user questions
   - Plan next release based on feedback

4. **Backup Release Artifacts**
   - Save APK locally
   - Save ProGuard mapping files
   - Document any issues found

## Release Checklist

Use this checklist for each release:

### Pre-Release
- [ ] Version numbers updated
- [ ] All tests pass
- [ ] Documentation updated
- [ ] APK built and tested
- [ ] Tested on multiple devices
- [ ] No critical bugs
- [ ] Release notes prepared

### During Release
- [ ] Code committed and pushed
- [ ] Tag created and pushed
- [ ] GitHub release created
- [ ] APK uploaded
- [ ] Release notes added
- [ ] Release published

### Post-Release
- [ ] README updated with download link
- [ ] Release announced
- [ ] Issues monitored
- [ ] Artifacts backed up

## Versioning Scheme

Follow Semantic Versioning (semver):

```
MAJOR.MINOR.PATCH

1.0.0 = Initial release
1.0.1 = Patch (bug fixes)
1.1.0 = Minor (new features, backwards compatible)
2.0.0 = Major (breaking changes)
```

Examples:
- `v1.0.0` - Initial release
- `v1.0.1` - Bug fix release
- `v1.1.0` - New features added
- `v2.0.0` - Major rewrite or breaking changes

## Hotfix Releases

For critical bugs:

1. Create hotfix branch from release tag
   ```bash
   git checkout -b hotfix/1.0.1 v1.0.0
   ```

2. Fix the bug
   ```bash
   # Make fixes
   git commit -m "Fix critical bug"
   ```

3. Update version to 1.0.1
4. Build and test
5. Merge to main and tag
   ```bash
   git checkout main
   git merge --no-ff hotfix/1.0.1
   git tag -a v1.0.1 -m "Hotfix release v1.0.1"
   git push origin main v1.0.1
   ```

6. Create GitHub release as above

## Troubleshooting

### APK Won't Install on Device
- Check minimum Android version (8.1+)
- Enable "Install from Unknown Sources"
- Check device storage space
- Try uninstalling old version first

### Release Tag Already Exists
```bash
# Delete tag locally and remotely
git tag -d v1.0.0
git push origin :refs/tags/v1.0.0

# Recreate tag
git tag -a v1.0.0 -m "Release v1.0.0"
git push origin v1.0.0
```

### APK Size Too Large
- Enable code shrinking
- Remove unused resources
- Use APK splitting for different architectures
- Compress assets

## Best Practices

1. **Always test release APK** before publishing
2. **Keep ProGuard mappings** for crash report analysis
3. **Version consistently** with semver
4. **Document all changes** in release notes
5. **Respond quickly** to issues in new releases
6. **Plan releases** around major features or fixes
7. **Maintain changelog** throughout development

## Next Steps

After successful deployment:

1. Monitor GitHub Issues for bug reports
2. Collect user feedback
3. Plan next release features
4. Maintain codebase quality
5. Update documentation as needed

---

**Ready to deploy?** Follow this guide step-by-step and you'll have a successful release!
