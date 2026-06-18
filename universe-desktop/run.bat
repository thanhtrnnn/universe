@echo off
REM Chay UniVerse Desktop (fat jar). Double-click de chay.
cd /d "%~dp0"

if not exist "target\universe-desktop.jar" (
    echo [run.bat] Chua co jar - dang build lan dau...
    call mvnw.cmd -B clean package -DskipTests
    if errorlevel 1 (
        echo [run.bat] Build that bai. Xem log o tren.
        pause
        exit /b 1
    )
)

echo [run.bat] Dang chay UniVerse Desktop...
java -jar "target\universe-desktop.jar"
echo.
echo [run.bat] App da dong. Nhan phim bat ky de thoat.
pause
