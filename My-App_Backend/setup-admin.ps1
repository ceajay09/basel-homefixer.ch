[CmdletBinding()]
param(
    [Parameter(Mandatory=$true)][string]$Email,
    [Parameter(Mandatory=$true)][string]$Database,
    [ValidateSet('create-admin','reset-password')][string]$Mode = 'create-admin',
    [switch]$Apply,
    [string]$ConfirmDatabase
)
$ErrorActionPreference = 'Stop'
if ($Apply -and $Database -ne $ConfirmDatabase) { throw 'Apply requires matching Database and ConfirmDatabase.' }
Push-Location $PSScriptRoot
try {
    & .\mvnw.cmd -B -DskipTests compile
    if ($LASTEXITCODE -ne 0) { throw 'Compilation failed.' }
    & .\mvnw.cmd -B org.apache.maven.plugins:maven-dependency-plugin:3.8.1:build-classpath '-DincludeScope=runtime' '-Dmdep.outputFile=target/auth-classpath.txt'
    if ($LASTEXITCODE -ne 0) { throw 'Classpath preparation failed.' }
    $authClasspath = 'target/classes;' + (Get-Content target/auth-classpath.txt -Raw).Trim()
    $setupArguments = @('--mode', $Mode, '--email', $Email, '--database', $Database)
    if ($Apply) { $setupArguments += @('--apply', '--confirm-database', $ConfirmDatabase) }
    # Do not redirect input/output: java.Console reads the password invisibly from the user's terminal.
    & java -cp $authClasspath com.example.myproject.auth.AdminAccountSetupCli @setupArguments
    if ($LASTEXITCODE -ne 0) { throw 'Account setup failed. No automatic retry or permission promotion is performed.' }
} finally { Pop-Location }
