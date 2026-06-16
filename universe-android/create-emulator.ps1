param(
    [string]$Name = "UniVerse_API_36"
)

$ErrorActionPreference = "Stop"
$sdk = if ($env:ANDROID_HOME) {
    $env:ANDROID_HOME
} elseif (Test-Path "D:\VMs") {
    "D:\VMs"
} else {
    Join-Path $env:LOCALAPPDATA "Android\Sdk"
}
$systemImage = Join-Path $sdk "system-images\android-36\google_apis_playstore\x86_64"
$emulator = Join-Path $sdk "emulator\emulator.exe"
$adb = Join-Path $sdk "platform-tools\adb.exe"
$avdRoot = Join-Path $env:USERPROFILE ".android\avd"
$avdDirectory = "D:\.android\$Name.avd"
$avdIni = Join-Path $avdRoot "$Name.ini"

if (-not (Test-Path $systemImage)) {
    throw "Không tìm thấy system image Android tại $systemImage"
}
if (-not (Test-Path $emulator)) {
    throw "Không tìm thấy Android Emulator tại $emulator"
}

New-Item -ItemType Directory -Path $avdDirectory -Force | Out-Null

$relativePath = "avd\$Name.avd"
@"
avd.ini.encoding=UTF-8
path=$avdDirectory
path.rel=$relativePath
target=android-36
"@ | Set-Content -LiteralPath $avdIni -Encoding ASCII

@"
AvdId=$Name
PlayStore.enabled=true
abi.type=x86_64
avd.ini.displayname=UniVerse Android Test
avd.ini.encoding=UTF-8
disk.dataPartition.size=6G
fastboot.forceColdBoot=no
fastboot.forceFastBoot=yes
hw.accelerometer=yes
hw.audioInput=yes
hw.battery=yes
hw.camera.back=virtualscene
hw.camera.front=emulated
hw.cpu.arch=x86_64
hw.cpu.ncore=4
hw.dPad=no
hw.gps=yes
hw.gpu.enabled=yes
hw.gpu.mode=auto
hw.keyboard=yes
hw.lcd.density=420
hw.lcd.height=2400
hw.lcd.width=1080
hw.mainKeys=no
hw.ramSize=2048
hw.sensors.orientation=yes
hw.sensors.proximity=yes
hw.trackBall=no
image.sysdir.1=system-images\android-36\google_apis_playstore\x86_64\
runtime.network.latency=none
runtime.network.speed=full
sdcard.size=512M
showDeviceFrame=no
skin.dynamic=yes
skin.name=1080x2400
skin.path=_no_skin
tag.display=Google Play
tag.id=google_apis_playstore
target=android-36
vm.heapSize=256
"@ | Set-Content -LiteralPath (Join-Path $avdDirectory "config.ini") -Encoding ASCII

Write-Host "Đã tạo AVD $Name." -ForegroundColor Green
Write-Host "Khởi động bằng:" -ForegroundColor Cyan
Write-Host "& `"$emulator`" -avd $Name"
Write-Host ""
Write-Host "Sau khi emulator mở, cài APK bằng:" -ForegroundColor Cyan
Write-Host "& `"$adb`" install -r app\build\outputs\apk\debug\app-debug.apk"

