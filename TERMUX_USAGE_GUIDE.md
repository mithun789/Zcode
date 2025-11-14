# Zcode Terminal - Termux Integration Usage Guide

## Overview

Zcode Terminal now includes full Termux integration, providing a real Linux environment on Android with a beautiful, custom UI. This guide explains how to use all the new features.

## Table of Contents

1. [Getting Started](#getting-started)
2. [Terminal Sessions](#terminal-sessions)
3. [Extra Keys Row](#extra-keys-row)
4. [Linux Environment Setup](#linux-environment-setup)
5. [Package Management](#package-management)
6. [Advanced Features](#advanced-features)
7. [Tips and Tricks](#tips-and-tricks)
8. [Troubleshooting](#troubleshooting)

---

## Getting Started

### First Launch

When you first open Zcode Terminal:

1. The app starts with a built-in terminal (simulated commands)
2. To get full Linux functionality, you need to install Termux bootstrap
3. Go to **Linux** tab → **Bootstrap Installer**
4. Tap **Install Bootstrap**
5. Wait for download and extraction (may take 5-10 minutes)
6. Once complete, return to **Terminal** tab

### Switching to Termux Environment

After bootstrap installation:

1. Open **Terminal** tab
2. Tap the menu icon (⋮) → **Linux Environments**
3. Select the installed environment
4. Terminal will restart with full Linux shell

---

## Terminal Sessions

### Multiple Sessions (Like Termux)

Zcode supports multiple terminal sessions running simultaneously, just like Termux tabs.

#### Creating New Sessions

1. **From Tab Bar**: Tap the **+** button on the right side of the tab bar
2. **From Menu**: Menu → **New Session**
3. **Keyboard Shortcut**: Swipe left on terminal (if enabled)

#### Switching Between Sessions

1. **Tap on Tab**: Click any session tab to switch to it
2. **Swipe Gestures**: Swipe left/right on terminal (if enabled)
3. **Compact Mode**: Use ◀ and ▶ arrows to navigate

#### Closing Sessions

1. **Tab Close Button**: Tap the ✕ on the tab
2. **Long Press**: Long press on tab → Close
3. **Command**: Type `exit` in the terminal

#### Session Management

- Each session runs independently
- Sessions persist until explicitly closed
- Sessions can use different Linux environments
- Session titles can be customized

---

## Extra Keys Row

The Extra Keys Row provides quick access to special keys that are difficult to type on mobile keyboards.

### Layout

#### Standard Layout (3 Rows)

**Row 1: Modifiers and Special Keys**
```
ESC  TAB  CTRL  ALT  ⇧  -  /  |
```

**Row 2: Navigation**
```
↑  ↓  ←  →  HOME  END  PgUp  PgDn
```

**Row 3: Compact view** (if needed)

### Using Modifier Keys

#### CTRL Key
1. Tap **CTRL** (it highlights)
2. Tap any letter key (e.g., **C**)
3. Sends Ctrl+C (interrupt)

Common Ctrl combinations:
- **Ctrl+C**: Interrupt current command
- **Ctrl+D**: EOF / Logout
- **Ctrl+Z**: Suspend process
- **Ctrl+L**: Clear screen
- **Ctrl+R**: Reverse search history

#### ALT Key
1. Tap **ALT** (it highlights)
2. Tap any key
3. Sends ESC + key sequence

Useful for:
- Alt+B: Back one word
- Alt+F: Forward one word
- Alt+D: Delete word

#### SHIFT Key
1. Tap **⇧** (it highlights)
2. Tap letter
3. Sends uppercase letter

### Arrow Keys

- **↑**: Previous command (history)
- **↓**: Next command (history)
- **←**: Move cursor left
- **→**: Move cursor right

### Toggle Extra Keys

- Tap the **keyboard icon** in the top bar to show/hide
- Settings → Terminal → Show Extra Keys

### Compact Mode

For small screens, use compact mode:
- Menu → **Compact Keys**
- Shows only essential keys in one row

---

## Linux Environment Setup

### Installing Termux Bootstrap

The bootstrap provides a full Linux environment with bash, coreutils, and package manager.

#### Installation Steps

1. Navigate to **Linux** tab
2. Tap **Bootstrap Installer**
3. Tap **Install Bootstrap**
4. Monitor progress:
   - Checking: Verifying system
   - Downloading: Getting bootstrap package (~40MB)
   - Extracting: Unpacking files (~500 files)
   - Configuring: Setting up environment

#### What Gets Installed

- **bash**: Full bash shell
- **coreutils**: ls, cat, grep, find, etc.
- **apt/pkg**: Package manager
- **Basic tools**: tar, gzip, curl, wget
- **Python**: (optional, via pkg install)
- **Git**: (optional, via pkg install)

#### Storage Requirements

- Minimum: 100MB free space
- Recommended: 500MB+ for packages
- Full install with dev tools: 1-2GB

### Post-Installation Setup

After bootstrap installation:

```bash
# Update package lists
apt update

# Install essential tools
pkg install git python nodejs vim

# Install development tools
pkg install gcc clang cmake make

# Install network tools
pkg install openssh curl wget
```

---

## Package Management

### Using apt/pkg

Zcode provides full apt/pkg package management through the Termux repositories.

#### Update Package Lists

```bash
apt update
# or
pkg update
```

#### Install Packages

```bash
# Single package
apt install git

# Multiple packages
apt install python nodejs vim

# With confirmation
apt install -y curl
```

#### Search Packages

```bash
apt search python
pkg search editor
```

#### Show Package Info

```bash
apt show nodejs
pkg show vim
```

#### List Installed Packages

```bash
apt list --installed
pkg list-installed
```

#### Remove Packages

```bash
apt remove vim
pkg uninstall nodejs
```

#### Upgrade All Packages

```bash
apt upgrade
# or
pkg upgrade
```

#### Clean Cache

```bash
apt clean
pkg clean
```

### Recommended Packages

#### Development
```bash
pkg install git python nodejs gcc clang make cmake
```

#### Editors
```bash
pkg install vim nano emacs
```

#### Network Tools
```bash
pkg install openssh curl wget rsync
```

#### System Tools
```bash
pkg install htop tmux tree ncdu
```

#### Languages
```bash
pkg install python ruby perl lua
pkg install nodejs golang rust
```

---

## Advanced Features

### Session Configuration

#### Environment Variables

Set in terminal:
```bash
export PATH=$PREFIX/bin:$PATH
export EDITOR=vim
export LANG=en_US.UTF-8
```

Add to `~/.bashrc` for persistence:
```bash
echo 'export EDITOR=vim' >> ~/.bashrc
source ~/.bashrc
```

### Custom Aliases

Create shortcuts in `~/.bashrc`:
```bash
alias ll='ls -lah'
alias update='apt update && apt upgrade'
alias cls='clear'
alias ..='cd ..'
```

### SSH Server

Run SSH server to access terminal from desktop:

```bash
# Install OpenSSH
pkg install openssh

# Start SSH server
sshd

# Check IP address
ip addr show

# Connect from desktop
ssh user@<phone-ip> -p 8022
```

### Running Background Processes

```bash
# Start in background
./myserver &

# Detach from terminal (use tmux)
tmux new -s myapp
./myapp
# Ctrl+B, D to detach
```

### File Transfer

#### From Phone to Desktop
```bash
# Install Python HTTP server
python -m http.server 8080

# Access from desktop browser
http://<phone-ip>:8080
```

#### From Desktop to Phone
```bash
# Install rsync
pkg install rsync

# Sync from desktop
rsync -avz desktop-folder/ user@phone:/data/data/com.example.zcode/files/
```

---

## Tips and Tricks

### Productivity Tips

1. **Use Command History**
   - Press ↑ to recall previous commands
   - Ctrl+R for reverse search
   - `history` to see all commands

2. **Tab Completion**
   - Press TAB to auto-complete commands and paths
   - Press TAB twice to see all options

3. **Multiple Sessions**
   - Keep one session for editing
   - Another for running tests
   - Another for monitoring logs

4. **Quick Navigation**
   - `cd -` to go back to previous directory
   - `pushd` and `popd` for directory stack

5. **Aliases**
   - Create shortcuts for long commands
   - Add to `~/.bashrc` for persistence

### Keyboard Shortcuts

- **Ctrl+C**: Stop current command
- **Ctrl+D**: Exit/EOF
- **Ctrl+L**: Clear screen
- **Ctrl+A**: Go to line start
- **Ctrl+E**: Go to line end
- **Ctrl+U**: Clear line before cursor
- **Ctrl+K**: Clear line after cursor
- **Ctrl+W**: Delete word before cursor

### Performance Tips

1. **Limit Output**
   - Use `| head` or `| tail` for large outputs
   - Use `less` instead of `cat` for files

2. **Background Jobs**
   - Use `&` to run commands in background
   - `jobs` to list background jobs
   - `fg` to bring to foreground

3. **Resource Management**
   - Close unused sessions
   - Clean package cache regularly
   - Monitor with `htop`

---

## Troubleshooting

### Bootstrap Won't Install

**Problem**: Download fails or extraction errors

**Solutions**:
1. Check internet connection
2. Ensure sufficient storage (100MB+)
3. Clear app cache: Settings → Apps → Zcode → Clear Cache
4. Try again after device restart

### Commands Not Found

**Problem**: Commands like `git`, `python` not working

**Solutions**:
1. Check if bootstrap is installed: `which bash`
2. Install the package: `pkg install git`
3. Update PATH: `export PATH=$PREFIX/bin:$PATH`
4. Check environment: `echo $PATH`

### Permission Denied

**Problem**: Can't execute files or access directories

**Solutions**:
1. Set executable: `chmod +x filename`
2. Check file permissions: `ls -l filename`
3. Ensure running in correct directory
4. Some system directories are restricted by Android

### Terminal Frozen

**Problem**: Terminal not responding

**Solutions**:
1. Try Ctrl+C to interrupt
2. Try Ctrl+Q to resume (if paused with Ctrl+S)
3. Close and reopen session
4. Force stop app if necessary

### Slow Performance

**Problem**: Terminal is slow or laggy

**Solutions**:
1. Close unused sessions
2. Reduce terminal buffer size
3. Clean package cache: `pkg clean`
4. Limit output with `head` or `tail`
5. Use lighter theme

### Package Installation Fails

**Problem**: apt/pkg can't install packages

**Solutions**:
1. Update lists: `apt update`
2. Check internet connection
3. Check storage space: `df -h`
4. Clear cache: `apt clean`
5. Try different mirror

### Extra Keys Not Working

**Problem**: Extra keys not inserting characters

**Solutions**:
1. Ensure session is active
2. Check keyboard focus
3. Restart terminal session
4. Toggle extra keys off and on

---

## Getting Help

### In-App Help

- Type `help` in terminal for built-in commands
- Menu → **About** for app information
- Menu → **Documentation** for this guide

### Community

- GitHub Issues: Report bugs or request features
- Termux Wiki: For package-specific help
- Linux documentation: For command usage

### Useful Commands

```bash
# Get help for a command
man ls
help cd
git --help

# Search for commands
apropos search-term

# Command description
whatis ls
```

---

## Conclusion

Zcode Terminal with Termux integration provides a powerful, full-featured Linux environment on Android. With multiple sessions, extra keys, and full package management, you can run almost any Linux command or tool directly on your phone.

Enjoy your mobile Linux terminal! 🚀

---

**Version**: 1.0  
**Last Updated**: 2025-01-14  
**For**: Zcode Terminal v1.0.0+
