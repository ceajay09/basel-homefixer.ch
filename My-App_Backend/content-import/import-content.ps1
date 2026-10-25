[CmdletBinding()]
param(
    [Parameter(Mandatory=$true)][string]$MediaRoot,
    [string]$Manifest = "$PSScriptRoot/projects.json",
    [string]$SourceRoot = "$PSScriptRoot/../../design/Kundenpaket_20261024/Kundenpaket/assets",
    [switch]$Apply,
    [string]$Database,
    [string]$ConfirmDatabase,
    [switch]$UpdateDrafts,
    [switch]$AllowPublish
)
$ErrorActionPreference = 'Stop'
if ($Apply -and ([string]::IsNullOrWhiteSpace($Database) -or $Database -ne $ConfirmDatabase)) {
    throw 'Apply requires matching explicit Database and ConfirmDatabase.'
}
Push-Location (Join-Path $PSScriptRoot '..')
try {
    & .\mvnw.cmd -B -DskipTests compile
    if ($LASTEXITCODE -ne 0) { throw 'Compilation failed.' }
    & .\mvnw.cmd -B org.apache.maven.plugins:maven-dependency-plugin:3.8.1:build-classpath '-Dmdep.outputFile=target/content-classpath.txt'
    if ($LASTEXITCODE -ne 0) { throw 'Classpath preparation failed.' }
    $contentClasspath = 'target/classes;' + (Get-Content target/content-classpath.txt -Raw).Trim()
    $importArguments = @('--manifest', $Manifest, '--source-root', $SourceRoot, '--media-root', $MediaRoot)
    if ($Apply) { $importArguments += @('--apply', '--database', $Database, '--confirm-database', $ConfirmDatabase) }
    if ($UpdateDrafts) { $importArguments += '--update-drafts' }
    if ($AllowPublish) { $importArguments += '--allow-publish' }
    & java -cp $contentClasspath com.example.myproject.content.ContentImportCli @importArguments
    if ($LASTEXITCODE -ne 0) { throw 'Import validation/application failed.' }
} finally { Pop-Location }
