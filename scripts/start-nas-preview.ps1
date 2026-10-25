[CmdletBinding()]
param(
    [string]$KeyFile = "$env:USERPROFILE/.ssh/basel_homefixer_nas_ed25519",
    [ValidateRange(1024,65535)][int]$LocalPort = 8081
)
$ErrorActionPreference = 'Stop'
if (!(Test-Path -LiteralPath $KeyFile)) { throw "NAS SSH key not found: $KeyFile" }
$api = "http://127.0.0.1:$LocalPort"
$pidFile = Join-Path $env:TEMP "basel-homefixer-nas-preview-$LocalPort.pid"
$logFile = Join-Path $env:TEMP "basel-homefixer-nas-preview-$LocalPort.log"
$existing = $null
try { $existing = Invoke-RestMethod "$api/_nas-preview/health" -TimeoutSec 1 } catch { }
if ($existing -and $existing.mode -eq 'nas-content-preview') {
    $check = Invoke-RestMethod "$api/api/projects/featured?lang=de" -TimeoutSec 10
    Write-Output "NAS preview already connected: $api ($($check.items.Count) featured projects)"
    return
}
$script = Join-Path $PSScriptRoot 'nas-preview/nas-content-proxy.cjs'
$arguments = @(('"'+$script+'"'),('"'+$KeyFile+'"'),[string]$LocalPort)
$process = Start-Process -FilePath (Get-Command node.exe).Source -ArgumentList $arguments -WindowStyle Hidden -RedirectStandardError $logFile -RedirectStandardOutput "$logFile.out" -PassThru
$process.Id | Set-Content $pidFile
for ($attempt = 0; $attempt -lt 10; $attempt++) {
    Start-Sleep -Milliseconds 300
    if ($process.HasExited) { throw "Preview proxy failed. See $logFile. The local port may already be occupied." }
    try {
        $check = Invoke-RestMethod "$api/api/projects/featured?lang=de" -TimeoutSec 10
        Write-Output "NAS preview connected: $api ($($check.items.Count) featured projects)"
        Write-Output 'Frontend: http://127.0.0.1:5180/projects (start the frontend separately if needed).'
        Write-Output "Proxy process: $($process.Id). Stop with: Stop-Process -Id $($process.Id)"
        return
    } catch { }
}
if (!$process.HasExited) { $process.Kill() }
throw "Preview API did not become available. Check $logFile and the NAS preview container."

