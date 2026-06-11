param(
    [switch]$SkipDatabaseCheck
)

$ErrorActionPreference = "Stop"
$apiRoot = Join-Path (Split-Path -Parent $MyInvocation.MyCommand.Path) "mobile-api"

if (-not $SkipDatabaseCheck) {
    $database = Test-NetConnection -ComputerName "127.0.0.1" -Port 5433 `
        -InformationLevel Quiet -WarningAction SilentlyContinue
    if (-not $database) {
        throw @"
PostgreSQL chưa chạy tại localhost:5433.

Hãy bật Docker/PostgreSQL trước:
  1. Khởi động Docker Desktop.
  2. Chạy:
     cd ..\universe-desktop
     docker compose up -d postgres

Sau đó chạy lại .\run-api.ps1
"@
    }
}

$lanIp = (
    ipconfig |
    Select-String "IPv4 Address|IPv4" |
    ForEach-Object { if ($_.Line -match '(\d{1,3}(?:\.\d{1,3}){3})') { $Matches[1] } } |
    Where-Object { $_ -notlike "127.*" -and $_ -notlike "169.254.*" } |
    Select-Object -First 1
)

Push-Location $apiRoot
try {
    mvn package
    if ($LASTEXITCODE -ne 0) {
        exit $LASTEXITCODE
    }
    Write-Host ""
    Write-Host "API cho Android Emulator: http://10.0.2.2:8080/api/" -ForegroundColor Cyan
    if ($lanIp) {
        Write-Host "API cho điện thoại thật: http://${lanIp}:8080/api/" -ForegroundColor Cyan
    }
    Write-Host "Giữ cửa sổ này mở trong khi dùng app." -ForegroundColor Yellow
    Write-Host ""
    java -jar "target\universe-mobile-api.jar"
} finally {
    Pop-Location
}
