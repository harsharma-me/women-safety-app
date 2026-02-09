# Creating and Using Zip Archives

This guide explains how to create and use zip archives of the Women Safety App project for easy distribution and sharing.

---

## 📦 Quick Start

### For Linux/Mac Users
```bash
# Make script executable
chmod +x create-zip.sh

# Create zip archive
./create-zip.sh
```

### For Windows Users
```batch
# Double-click the file or run from command prompt
create-zip.bat
```

The zip file will be created in the `dist/` directory as `women-safety-app-v1.0.0.zip`.

---

## 🎯 What's Included in the Zip

The zip archive includes everything needed to build and run the app:

✅ **Source Code**
- All Kotlin source files (22 files, 2,081 lines)
- All XML layouts and resources (15 files, 1,010 lines)

✅ **Build Configuration**
- Gradle build files (build.gradle, settings.gradle)
- Gradle wrapper (gradlew, gradlew.bat)
- Gradle properties

✅ **Documentation**
- README.md - Project overview
- BUILD_GUIDE.md - Build instructions
- QUICK_START.md - Quick start guide
- WHAT_TO_DO_NEXT.md - Next steps roadmap
- PROJECT_STRUCTURE.md - Architecture details
- IMPLEMENTATION_SUMMARY.md - Feature list
- DOCUMENTATION_INDEX.md - Doc navigation
- PROJECT_STATUS.md - Status dashboard

✅ **Resources**
- App icon and drawables
- String resources
- Color definitions
- Theme configurations
- Layout files

---

## 🚫 What's Excluded

The zip archive excludes build artifacts and temporary files:

- `.git/` - Git version control history
- `build/` - Compiled build outputs
- `.gradle/` - Gradle cache
- `.idea/` - IDE configuration files
- `*.apk`, `*.aab` - Built APK files
- `*.class`, `*.dex` - Compiled class files
- `local.properties` - Local SDK paths
- `*.log` - Log files

This keeps the archive size small and clean.

---

## 📊 Expected Archive Size

- **Uncompressed:** ~2-3 MB
- **Compressed (zip):** ~500 KB - 1 MB

The exact size depends on the compression algorithm used.

---

## 🔧 Manual Zip Creation

If you prefer to create the zip manually:

### Using Command Line (Linux/Mac/Windows with Git Bash)
```bash
zip -r women-safety-app.zip . \
  -x ".git/*" \
  -x ".gradle/*" \
  -x "build/*" \
  -x "*.apk" \
  -x "*.class" \
  -x ".idea/*" \
  -x "*.iml" \
  -x "local.properties"
```

### Using GUI Tools

**Windows:**
1. Right-click on the project folder
2. Select "Send to > Compressed (zipped) folder"
3. Manually delete unwanted folders (.git, build, .gradle, .idea)

**Mac:**
1. Right-click on the project folder
2. Select "Compress [folder name]"
3. Manually remove .git, build, etc. if needed

**Linux:**
1. Use File Manager's "Compress" option
2. Or use Archive Manager application

---

## 📤 Distributing the Zip

### Upload Options

**1. GitHub Releases**
```bash
# Create a release on GitHub
# Upload the zip as an asset
# Users can download from Releases page
```

**2. Google Drive / Dropbox**
- Upload zip to cloud storage
- Share public link
- Easy for non-technical users

**3. Direct Download**
- Host on a web server
- Provide direct download link
- Example: https://example.com/downloads/women-safety-app-v1.0.0.zip

### Sharing Best Practices

✅ **Include a README in the zip** - Already included!
✅ **Version the filename** - Script uses v1.0.0
✅ **Calculate checksum** - For integrity verification
✅ **Sign the archive** - For security (optional)

---

## 📥 Using the Zip Archive

### For Developers

**Step 1: Extract**
```bash
# Linux/Mac
unzip women-safety-app-v1.0.0.zip

# Windows
# Right-click > Extract All
```

**Step 2: Open in Android Studio**
```bash
# Navigate to extracted folder
cd women-safety-app

# Open in Android Studio
# File > Open > Select the folder
```

**Step 3: Build**
```bash
./gradlew assembleDebug
```

See [BUILD_GUIDE.md](BUILD_GUIDE.md) for detailed instructions.

### For Non-Developers

1. **Extract the zip** to any location
2. **Read README.md** to understand the project
3. **Share with a developer** who can build and install the app
4. Or **learn Android development** using the included documentation

---

## 🔄 Updating the Zip

When you make changes to the project:

1. Update version in scripts:
   - Edit `VERSION` in `create-zip.sh`
   - Edit `VERSION` in `create-zip.bat`

2. Run the script again:
   ```bash
   ./create-zip.sh  # or create-zip.bat
   ```

3. New zip created with updated version number

---

## 🛠️ Advanced Options

### Custom Zip Name
Edit the script to change the output name:
```bash
ZIP_NAME="my-custom-name-v1.0.0.zip"
```

### Custom Output Directory
Change where the zip is saved:
```bash
OUTPUT_DIR="releases"  # Instead of "dist"
```

### Include Additional Files
Modify the rsync/xcopy commands to include specific files:
```bash
# Add specific patterns to include
--include='special-file.txt'
```

### Exclude Additional Files
Add patterns to `zip-exclude.txt` (Windows) or the rsync exclude list (Linux/Mac):
```
my-secret-file.txt
test-data/
```

---

## 🔐 Verifying the Zip

After creating the zip, verify its contents:

### Linux/Mac
```bash
# List contents
unzip -l dist/women-safety-app-v1.0.0.zip | less

# Test integrity
unzip -t dist/women-safety-app-v1.0.0.zip
```

### Windows
```batch
# Using PowerShell
Expand-Archive -Path dist\women-safety-app-v1.0.0.zip -DestinationPath temp-test -Force

# Check contents
dir temp-test\women-safety-app

# Cleanup
rmdir /s /q temp-test
```

---

## 📋 Checklist Before Distributing

Before sharing the zip archive:

- [ ] Remove any sensitive data (API keys, passwords)
- [ ] Update version numbers
- [ ] Test the zip by extracting and building
- [ ] Verify all documentation is up to date
- [ ] Check that README contains accurate info
- [ ] Remove any personal/local configuration files
- [ ] Ensure LICENSE file is included (if applicable)
- [ ] Write release notes

---

## 🐛 Troubleshooting

### "Permission Denied" on Linux/Mac
```bash
# Make script executable
chmod +x create-zip.sh
```

### "Zip Command Not Found" on Linux
```bash
# Install zip utility
sudo apt-get install zip  # Ubuntu/Debian
sudo yum install zip      # CentOS/RHEL
brew install zip          # macOS
```

### "PowerShell Not Found" on Windows
- PowerShell should be pre-installed on Windows 7+
- If missing, download from Microsoft

### Zip is Too Large
- Check that build/ and .gradle/ are excluded
- Verify .git/ is not included
- Run `./gradlew clean` before zipping

### Extraction Fails
- File may be corrupted during transfer
- Try re-creating the zip
- Use `unzip -t` to test integrity

---

## 📚 Related Documentation

- [README.md](README.md) - Project overview
- [BUILD_GUIDE.md](BUILD_GUIDE.md) - How to build the app
- [QUICK_START.md](QUICK_START.md) - Quick setup guide
- [WHAT_TO_DO_NEXT.md](WHAT_TO_DO_NEXT.md) - Next steps after extraction

---

## 💡 Tips

**For Fast Sharing:**
- Upload to GitHub Releases (free, reliable)
- Use Google Drive for large teams
- Include SHA256 checksum for verification

**For Security:**
- Don't include API keys or secrets
- Remove local.properties file
- Scan for sensitive data before sharing

**For Convenience:**
- Include version number in filename
- Add date stamp: `women-safety-app-v1.0.0-2026-02-09.zip`
- Keep previous versions available

---

## 🎓 Learning Resources

Want to learn more about zip archives?

- [Zip File Format](https://en.wikipedia.org/wiki/ZIP_(file_format))
- [7-Zip Documentation](https://www.7-zip.org/)
- [Info-ZIP](http://infozip.sourceforge.net/)

---

## ✨ Summary

Creating a zip archive is simple:
1. Run `./create-zip.sh` (or `.bat` on Windows)
2. Find zip in `dist/` folder
3. Share with others!

The zip contains everything needed to build the app, excluding build artifacts and version control history.

**Questions?** Check the other documentation files or open an issue on GitHub.
