param(
    [string]$BaseUrl = "http://127.0.0.1:8080/api/"
)

$ErrorActionPreference = "Stop"
if (-not $BaseUrl.EndsWith("/")) {
    $BaseUrl += "/"
}

Write-Host "Kiểm tra $BaseUrl" -ForegroundColor Cyan
$health = Invoke-RestMethod -Uri ($BaseUrl + "health") -TimeoutSec 5
if ($health.status -ne "ok") {
    throw "Health endpoint không trả về trạng thái ok."
}
Write-Host "[OK] API đang chạy." -ForegroundColor Green

$loginBody = @{
    username = "student"
    password = "123456"
} | ConvertTo-Json

try {
    $login = Invoke-RestMethod `
        -Method Post `
        -Uri ($BaseUrl + "auth/login") `
        -ContentType "application/json; charset=utf-8" `
        -Body $loginBody `
        -TimeoutSec 10
    Write-Host "[OK] PostgreSQL và đăng nhập demo hoạt động: $($login.student.fullName)" `
        -ForegroundColor Green
} catch {
    Write-Host "[FAIL] API chạy nhưng không đăng nhập được." -ForegroundColor Red
    Write-Host "Kiểm tra PostgreSQL tại localhost:5433 và dữ liệu seed student/123456."
    throw
}

