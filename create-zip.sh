#!/bin/bash

# Women Safety App - Project Zip Creator
# This script creates a distributable zip archive of the project

set -e

# Configuration
PROJECT_NAME="women-safety-app"
VERSION="1.0.0"
OUTPUT_DIR="dist"
ZIP_NAME="${PROJECT_NAME}-v${VERSION}.zip"

echo "========================================"
echo "Women Safety App - Zip Archive Creator"
echo "========================================"
echo ""

# Create output directory if it doesn't exist
mkdir -p "$OUTPUT_DIR"

# Get absolute path of output directory
OUTPUT_DIR=$(cd "$OUTPUT_DIR" && pwd)

# Remove old zip if exists
if [ -f "$OUTPUT_DIR/$ZIP_NAME" ]; then
    echo "Removing old zip file..."
    rm "$OUTPUT_DIR/$ZIP_NAME"
fi

echo "Creating zip archive: $ZIP_NAME"
echo ""

# Create temporary directory for clean copy
TEMP_DIR=$(mktemp -d)
COPY_DIR="$TEMP_DIR/$PROJECT_NAME"

echo "Preparing files..."

# Copy project files excluding build artifacts and git
rsync -av \
    --exclude='.git' \
    --exclude='.gradle' \
    --exclude='build' \
    --exclude='*.apk' \
    --exclude='*.aab' \
    --exclude='*.dex' \
    --exclude='*.class' \
    --exclude='.idea' \
    --exclude='*.iml' \
    --exclude='local.properties' \
    --exclude='captures' \
    --exclude='*.log' \
    --exclude='.DS_Store' \
    --exclude='dist' \
    --exclude='*.hprof' \
    ./ "$COPY_DIR/" > /dev/null

# Create the zip file
echo "Creating zip archive..."
cd "$TEMP_DIR"
zip -r "$ZIP_NAME" "$PROJECT_NAME" > /dev/null

# Move zip to output directory
mv "$ZIP_NAME" "$OUTPUT_DIR/"

# Calculate size
SIZE=$(du -h "$OUTPUT_DIR/$ZIP_NAME" | cut -f1)

# Cleanup
rm -rf "$TEMP_DIR"

echo ""
echo "✅ Success!"
echo ""
echo "📦 Archive created: $OUTPUT_DIR/$ZIP_NAME"
echo "📊 Size: $SIZE"
echo ""
echo "📂 Contents included:"
echo "   - All source code (Kotlin files)"
echo "   - All XML layouts and resources"
echo "   - All documentation files"
echo "   - Gradle build configuration"
echo "   - README and guides"
echo ""
echo "🚫 Excluded (as per .gitignore):"
echo "   - Git history (.git)"
echo "   - Build artifacts (build/, .gradle/)"
echo "   - IDE files (.idea/, *.iml)"
echo "   - Compiled files (*.class, *.dex, *.apk)"
echo ""
echo "You can now distribute: $OUTPUT_DIR/$ZIP_NAME"
echo "========================================"
