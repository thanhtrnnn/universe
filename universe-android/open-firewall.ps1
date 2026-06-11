$ErrorActionPreference = "Stop"
$ruleName = "UniVerse Mobile API 8080"

$existing = Get-NetFirewallRule -DisplayName $ruleName -ErrorAction SilentlyContinue
if ($existing) {
    Set-NetFirewallRule -DisplayName $ruleName -Enabled True -Profile Private
    Write-Host "Đã bật lại firewall rule: $ruleName" -ForegroundColor Green
    exit 0
}

New-NetFirewallRule `
    -DisplayName $ruleName `
    -Direction Inbound `
    -Action Allow `
    -Protocol TCP `
    -LocalPort 8080 `
    -Profile Private | Out-Null

Write-Host "Đã cho phép TCP 8080 trên mạng Private." -ForegroundColor Green

