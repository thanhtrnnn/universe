$ErrorActionPreference = "Stop"
$sdk = if ($env:ANDROID_HOME) {
    $env:ANDROID_HOME
} else {
    Join-Path $env:LOCALAPPDATA "Android\Sdk"
}
$adb = Join-Path $sdk "platform-tools\adb.exe"
$apk = Join-Path (Split-Path -Parent $MyInvocation.MyCommand.Path) `
    "app\build\outputs\apk\debug\app-debug.apk"

if (-not (Test-Path $adb)) {
    throw "Không tìm thấy adb tại $adb"
}

& $adb start-server | Out-Null
$devices = & $adb devices
$connected = $devices | Select-String "^\S+\s+device$"
if (-not $connected) {
    throw @"
Không tìm thấy điện thoại Android.

1. Bật Developer options và USB debugging trên điện thoại.
2. Cắm cáp USB, chọn Allow USB debugging.
3. Chạy lại .\connect-phone-usb.ps1
"@
}

& $adb reverse tcp:8080 tcp:8080
if ($LASTEXITCODE -ne 0) {
    throw "Không tạo được adb reverse cho cổng 8080."
}

if (Test-Path $apk) {
    & $adb install -r $apk
    if ($LASTEXITCODE -ne 0) {
        throw "Không cài được APK lên điện thoại."
    }
}

Write-Host "Đã nối cổng API qua USB và cài APK." -ForegroundColor Green
Write-Host "Trong app, nhập URL:" -ForegroundColor Cyan
Write-Host "http://127.0.0.1:8080/api/" -ForegroundColor Green
Write-Host ""
Write-Host "Giữ cáp USB và API trên PC hoạt động trong khi test."

