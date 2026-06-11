$ErrorActionPreference = "Stop"

$lanIp = (
    ipconfig |
    Select-String "IPv4 Address|IPv4" |
    ForEach-Object { if ($_.Line -match '(\d{1,3}(?:\.\d{1,3}){3})') { $Matches[1] } } |
    Where-Object { $_ -notlike "127.*" -and $_ -notlike "169.254.*" } |
    Select-Object -First 1
)

if (-not $lanIp) {
    throw "Không tìm thấy IPv4 LAN. Hãy kết nối Wi-Fi/Ethernet trước."
}

Write-Host "Nhập địa chỉ sau vào ô Địa chỉ API trên điện thoại:" -ForegroundColor Cyan
Write-Host "http://${lanIp}:8080/api/" -ForegroundColor Green
Write-Host ""
Write-Host "Điện thoại và PC phải cùng Wi-Fi." -ForegroundColor Yellow
Write-Host "Chạy open-firewall.ps1 bằng PowerShell Administrator nếu nút kiểm tra vẫn lỗi."

