param(
    [switch]$Offline
)

$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$sdk = if ($env:ANDROID_HOME) {
    $env:ANDROID_HOME
} elseif (Test-Path "D:\VMs") {
    "D:\VMs"
} else {
    Join-Path $env:LOCALAPPDATA "Android\Sdk"
}
$aapt2 = Join-Path $sdk "build-tools\36.1.0\aapt2.exe"

if (-not (Test-Path $aapt2)) {
    throw "Không tìm thấy aapt2 tại $aapt2. Hãy cài Android SDK Build-Tools 36.1.0."
}

$env:ANDROID_HOME = $sdk
$env:ANDROID_SDK_ROOT = $sdk
$arguments = @(
    "-Pandroid.aapt2FromMavenOverride=$aapt2",
    "testDebugUnitTest",
    "assembleDebug",
    "--no-daemon"
)
if ($Offline) {
    $arguments += "--offline"
}

Push-Location $projectRoot
try {
    & ".\gradlew.bat" @arguments
    exit $LASTEXITCODE
} finally {
    Pop-Location
}

