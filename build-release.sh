#!/bin/bash

# Zcode Terminal - Release Build Script
# This script automates the process of building a release APK

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Print colored message
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Banner
echo "========================================"
echo "   Zcode Terminal - Release Builder    "
echo "========================================"
echo ""

# Check if running in project directory
if [ ! -f "build.gradle.kts" ]; then
    print_error "This script must be run from the project root directory"
    exit 1
fi

# Check if gradlew exists
if [ ! -f "gradlew" ]; then
    print_error "gradlew not found. Please ensure you're in the correct directory."
    exit 1
fi

# Make gradlew executable
chmod +x gradlew

# Step 1: Clean previous builds
print_info "Cleaning previous builds..."
./gradlew clean
print_success "Clean completed"
echo ""

# Step 2: Check for tests
print_info "Running unit tests..."
if ./gradlew test; then
    print_success "All tests passed"
else
    print_warning "Some tests failed. Continue anyway? (y/N)"
    read -r response
    if [[ ! "$response" =~ ^[Yy]$ ]]; then
        print_error "Build cancelled"
        exit 1
    fi
fi
echo ""

# Step 3: Build debug APK first (faster, for quick testing)
print_info "Building debug APK..."
if ./gradlew assembleDebug; then
    print_success "Debug APK built successfully"
    DEBUG_APK="app/build/outputs/apk/debug/app-debug.apk"
    if [ -f "$DEBUG_APK" ]; then
        DEBUG_SIZE=$(du -h "$DEBUG_APK" | cut -f1)
        print_info "Debug APK location: $DEBUG_APK"
        print_info "Debug APK size: $DEBUG_SIZE"
    fi
else
    print_error "Debug build failed"
    exit 1
fi
echo ""

# Step 4: Ask if user wants to build release
print_info "Build release APK? (Y/n)"
read -r response
if [[ "$response" =~ ^[Nn]$ ]]; then
    print_info "Skipping release build"
    print_success "Build process completed"
    exit 0
fi

# Step 5: Build release APK
print_info "Building release APK (this may take a few minutes)..."
if ./gradlew assembleRelease; then
    print_success "Release APK built successfully"
    RELEASE_APK="app/build/outputs/apk/release/app-release.apk"
    if [ -f "$RELEASE_APK" ]; then
        RELEASE_SIZE=$(du -h "$RELEASE_APK" | cut -f1)
        print_info "Release APK location: $RELEASE_APK"
        print_info "Release APK size: $RELEASE_SIZE"
        
        # Calculate size difference
        DEBUG_SIZE_BYTES=$(stat -f%z "$DEBUG_APK" 2>/dev/null || stat -c%s "$DEBUG_APK")
        RELEASE_SIZE_BYTES=$(stat -f%z "$RELEASE_APK" 2>/dev/null || stat -c%s "$RELEASE_APK")
        REDUCTION=$((100 - (RELEASE_SIZE_BYTES * 100 / DEBUG_SIZE_BYTES)))
        print_info "Size reduction: ${REDUCTION}% (compared to debug)"
    fi
else
    print_error "Release build failed"
    exit 1
fi
echo ""

# Step 6: Verify APK
print_info "Verifying release APK..."
if command -v apksigner &> /dev/null; then
    if apksigner verify "$RELEASE_APK"; then
        print_success "APK signature verified"
    else
        print_warning "APK signature verification failed"
    fi
else
    print_warning "apksigner not found, skipping verification"
fi
echo ""

# Step 7: Show APK info
print_info "Extracting APK information..."
if command -v aapt &> /dev/null; then
    VERSION_NAME=$(aapt dump badging "$RELEASE_APK" | grep "versionName" | sed -n "s/.*versionName='\([^']*\)'.*/\1/p")
    VERSION_CODE=$(aapt dump badging "$RELEASE_APK" | grep "versionCode" | sed -n "s/.*versionCode='\([^']*\)'.*/\1/p")
    MIN_SDK=$(aapt dump badging "$RELEASE_APK" | grep "sdkVersion" | sed -n "s/.*sdkVersion:'\([^']*\)'.*/\1/p")
    TARGET_SDK=$(aapt dump badging "$RELEASE_APK" | grep "targetSdkVersion" | sed -n "s/.*targetSdkVersion:'\([^']*\)'.*/\1/p")
    
    echo "  Version: $VERSION_NAME (code: $VERSION_CODE)"
    echo "  Min SDK: $MIN_SDK"
    echo "  Target SDK: $TARGET_SDK"
else
    print_warning "aapt not found, skipping APK info extraction"
fi
echo ""

# Step 8: Ask if user wants to install on connected device
print_info "Install on connected device? (y/N)"
read -r response
if [[ "$response" =~ ^[Yy]$ ]]; then
    if command -v adb &> /dev/null; then
        # Check if device is connected
        DEVICES=$(adb devices | grep -v "List" | grep "device$" | wc -l)
        if [ "$DEVICES" -eq 0 ]; then
            print_warning "No devices connected"
        else
            print_info "Installing on device..."
            if adb install -r "$RELEASE_APK"; then
                print_success "APK installed successfully"
                
                # Optionally launch the app
                print_info "Launch the app? (y/N)"
                read -r launch_response
                if [[ "$launch_response" =~ ^[Yy]$ ]]; then
                    adb shell am start -n com.example.zcode/.MainActivity
                    print_success "App launched"
                fi
            else
                print_error "Installation failed"
            fi
        fi
    else
        print_warning "adb not found, cannot install"
    fi
fi
echo ""

# Step 9: Summary
echo "========================================"
echo "            Build Summary              "
echo "========================================"
echo ""
print_success "Build completed successfully!"
echo ""
echo "Output files:"
echo "  Debug APK:   $DEBUG_APK ($DEBUG_SIZE)"
echo "  Release APK: $RELEASE_APK ($RELEASE_SIZE)"
echo ""
echo "Next steps:"
echo "  1. Test the APK on various devices"
echo "  2. Create a GitHub release"
echo "  3. Upload the APK to the release page"
echo "  4. Update documentation with download link"
echo ""
echo "To create a GitHub release:"
echo "  git tag -a v1.0.0 -m 'Release v1.0.0'"
echo "  git push origin v1.0.0"
echo ""
print_success "Done!"
