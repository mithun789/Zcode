# Zcode Terminal - Testing Guide

This guide explains how to test the Zcode terminal application to verify it provides a real Linux environment on Android devices.

## Test Categories

### 1. Unit Tests

#### Terminal Emulator Tests (`terminal-emulator/src/test`)
- **TerminalEmulatorTest.kt**: Tests core terminal emulation functionality
  - ANSI escape sequence processing
  - Terminal buffer management
  - Cursor positioning
  - Text rendering
  - Control character handling

#### Application Tests (`app/src/test`)
- **TerminalCommandsTest.kt**: Tests built-in command functionality
  - File operations (ls, pwd, cd, cat, mkdir, touch, rm, cp, mv)
  - System info commands (date, whoami, uname, ps, df, free)
  - Text processing (grep, find, head, tail, wc)
  - Package management (apt, pkg)

- **LinuxEnvironmentTest.kt**: Tests Linux environment functionality
  - Shell availability
  - Process execution
  - Environment variables
  - Standard I/O streams (stdin, stdout, stderr)
  - Session management

### 2. Running Unit Tests

#### Prerequisites
- Android Studio Arctic Fox or later
- JDK 11+
- Android SDK with API 27+

#### Command Line
```bash
# Run all unit tests
./gradlew test

# Run specific test class
./gradlew :app:test --tests "com.example.zcode.terminal.TerminalCommandsTest"
./gradlew :terminal-emulator:test --tests "com.termux.terminal.TerminalEmulatorTest"

# Generate test report
./gradlew test jacocoTestReport
```

#### Android Studio
1. Right-click on test file or test class
2. Select "Run 'TestName'"
3. View results in Run window

### 3. Integration Tests

#### Testing Linux Environment on Device

1. **Install APK on Device**
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

2. **Test Built-in Commands**
   - Open the app
   - Navigate to Terminal tab
   - Test basic commands:
     ```bash
     pwd
     ls
     echo "Hello World"
     whoami
     uname -a
     ```

3. **Test Linux Environment Creation**
   - Navigate to Linux tab
   - Tap "Create New Environment"
   - Select a distribution (Alpine recommended for testing)
   - Wait for download and extraction
   - Once complete, go to Terminal tab
   - Verify environment is selected in dropdown
   - Test real Linux commands:
     ```bash
     ls /bin
     which bash
     cat /etc/os-release
     ```

4. **Test Real Shell Commands**
   If using Termux bootstrap or PRoot environment:
   ```bash
   # Test shell
   bash --version
   
   # Test package manager
   apt update  # or pkg update
   
   # Test file operations
   touch testfile.txt
   echo "test content" > testfile.txt
   cat testfile.txt
   rm testfile.txt
   
   # Test directory operations
   mkdir testdir
   cd testdir
   pwd
   cd ..
   rmdir testdir
   
   # Test process management
   ps aux
   echo $$  # show PID
   
   # Test environment variables
   env
   export MY_VAR="test"
   echo $MY_VAR
   ```

5. **Test Advanced Features**
   ```bash
   # Test pipes
   ls -la | grep txt
   
   # Test text processing
   echo -e "line1\nline2\nline3" | grep line2
   
   # Test file search
   find / -name "bash" 2>/dev/null
   
   # Test system info
   df -h
   free -h
   uname -a
   ```

### 4. Performance Testing

#### Terminal Rendering
- Test scrolling with large output:
  ```bash
  seq 1 1000
  ```
- Test rapid output:
  ```bash
  while true; do echo "test"; done  # Ctrl+C to stop
  ```

#### Memory Usage
- Open multiple terminal sessions
- Run commands in each
- Monitor app memory in Android Settings > Developer Options > Running Services

#### Long-running Processes
```bash
# Test background process
sleep 60 &
ps

# Test interactive programs
vi testfile.txt  # if vim is installed
python3  # if python is installed
```

### 5. UI Testing

#### Manual UI Tests
1. **Navigation**
   - Test tab switching between Terminal, Linux, Settings
   - Test terminal session tabs
   - Test creating multiple sessions

2. **Input**
   - Test keyboard input
   - Test special keys (Enter, Backspace, Tab)
   - Test extra keys row
   - Test copy/paste functionality

3. **Display**
   - Test terminal themes
   - Test font size changes
   - Test color schemes
   - Test terminal resizing

4. **Settings**
   - Test theme selection
   - Test font size adjustment
   - Test other configuration options

### 6. Instrumented Tests (Device/Emulator Required)

Run on connected device or emulator:
```bash
./gradlew connectedAndroidTest
```

### 7. Verifying Linux Environment Functionality

To verify the app provides a REAL Linux environment:

1. **Check Shell Type**
   ```bash
   echo $SHELL
   which bash
   bash --version
   ```

2. **Verify Linux Distribution**
   ```bash
   cat /etc/os-release
   lsb_release -a  # if available
   ```

3. **Check Available Commands**
   ```bash
   ls /bin
   ls /usr/bin
   which gcc
   which python3
   which git
   ```

4. **Test Package Installation** (if apt/pkg available)
   ```bash
   apt update
   apt search nano
   apt install nano  # or another small package
   which nano
   ```

5. **Verify File System**
   ```bash
   ls -la /
   ls -la /usr
   ls -la /etc
   cat /proc/version  # if procfs is mounted
   ```

6. **Test Networking** (if available)
   ```bash
   ping -c 3 google.com
   curl -I https://google.com
   ```

### 8. Common Test Scenarios

#### Scenario 1: Fresh Install
1. Install APK
2. Open app
3. Test built-in terminal
4. Create Linux environment
5. Test Linux commands

#### Scenario 2: Multiple Sessions
1. Create multiple terminal sessions
2. Run different commands in each
3. Switch between sessions
4. Verify session isolation

#### Scenario 3: Long-term Usage
1. Use terminal for extended period
2. Run multiple commands
3. Create files and directories
4. Verify no memory leaks
5. Verify command history works

#### Scenario 4: Error Handling
1. Test invalid commands
2. Test permission errors
3. Test file not found errors
4. Verify graceful error messages

### 9. Automated Testing Script

Create a test script for quick validation:
```bash
#!/bin/bash
# zcode_test.sh - Quick validation script

echo "=== Zcode Terminal Test Suite ==="
echo ""

echo "1. Testing basic commands..."
pwd
ls -la
echo "Basic commands: OK"
echo ""

echo "2. Testing file operations..."
touch test_file.txt
echo "test content" > test_file.txt
cat test_file.txt
rm test_file.txt
echo "File operations: OK"
echo ""

echo "3. Testing system info..."
uname -a
whoami
date
echo "System info: OK"
echo ""

echo "4. Testing shell features..."
echo $SHELL
echo $PATH
export TEST_VAR="test"
echo $TEST_VAR
echo "Shell features: OK"
echo ""

echo "=== All tests passed! ==="
```

Save this script and run in the terminal:
```bash
chmod +x zcode_test.sh
./zcode_test.sh
```

### 10. Expected Results

After running all tests, you should verify:

✅ **Unit tests pass** - All JUnit tests should pass
✅ **Terminal renders correctly** - Text displays properly with correct colors
✅ **Commands execute** - Both built-in and Linux commands work
✅ **Sessions work** - Multiple sessions can be created and managed
✅ **File operations work** - Files can be created, read, modified, deleted
✅ **Shell integration works** - Real shell (bash/sh) is accessible
✅ **Environment variables work** - Variables can be set and read
✅ **No crashes** - App remains stable during testing
✅ **Performance acceptable** - Terminal responds quickly to input
✅ **Memory usage reasonable** - No excessive memory consumption

### 11. Reporting Issues

If tests fail, collect the following information:
- Android version and device model
- App version
- Steps to reproduce
- Expected vs actual behavior
- Logcat output: `adb logcat | grep -i "zcode\|terminal\|session"`
- Screenshots if UI issue

## Conclusion

This testing guide ensures the Zcode app provides a real, functional Linux environment on Android. Regular testing with these procedures will maintain app quality and verify Linux environment functionality.
