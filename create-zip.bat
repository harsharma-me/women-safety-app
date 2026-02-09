@echo off
REM Women Safety App - Project Zip Creator (Windows)
REM This script creates a distributable zip archive of the project

setlocal enabledelayedexpansion

REM Configuration
set PROJECT_NAME=women-safety-app
set VERSION=1.0.0
set OUTPUT_DIR=dist
set ZIP_NAME=%PROJECT_NAME%-v%VERSION%.zip

echo ========================================
echo Women Safety App - Zip Archive Creator
echo ========================================
echo.

REM Create output directory if it doesn't exist
if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"

REM Remove old zip if exists
if exist "%OUTPUT_DIR%\%ZIP_NAME%" (
    echo Removing old zip file...
    del "%OUTPUT_DIR%\%ZIP_NAME%"
)

echo Creating zip archive: %ZIP_NAME%
echo.

REM Check if PowerShell is available for compression
where powershell >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: PowerShell is required but not found.
    echo Please install PowerShell or use a zip utility manually.
    pause
    exit /b 1
)

echo Preparing files...

REM Create temporary directory
set TEMP_DIR=%TEMP%\%PROJECT_NAME%_temp_%RANDOM%
if exist "%TEMP_DIR%" rmdir /s /q "%TEMP_DIR%"
mkdir "%TEMP_DIR%\%PROJECT_NAME%"

REM Copy files excluding build artifacts
echo Copying project files...
xcopy /E /I /Q /EXCLUDE:zip-exclude.txt . "%TEMP_DIR%\%PROJECT_NAME%" >nul

REM Create zip using PowerShell
echo Creating zip archive...
powershell -Command "Compress-Archive -Path '%TEMP_DIR%\%PROJECT_NAME%' -DestinationPath '%OUTPUT_DIR%\%ZIP_NAME%' -Force"

REM Cleanup
rmdir /s /q "%TEMP_DIR%"

echo.
echo ✅ Success!
echo.
echo 📦 Archive created: %OUTPUT_DIR%\%ZIP_NAME%
echo.
echo 📂 Contents included:
echo    - All source code (Kotlin files)
echo    - All XML layouts and resources
echo    - All documentation files
echo    - Gradle build configuration
echo    - README and guides
echo.
echo 🚫 Excluded (as per .gitignore):
echo    - Git history (.git)
echo    - Build artifacts (build/, .gradle/)
echo    - IDE files (.idea/, *.iml)
echo    - Compiled files (*.class, *.dex, *.apk)
echo.
echo You can now distribute: %OUTPUT_DIR%\%ZIP_NAME%
echo ========================================
echo.
pause
