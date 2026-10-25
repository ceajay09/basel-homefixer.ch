[CmdletBinding()]
param([Parameter(Mandatory=$true)][string]$OutputDirectory,[switch]$SkipBuild)
$ErrorActionPreference='Stop'
$taskRepo=[IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../..'))
$taskOut=[IO.Path]::GetFullPath($OutputDirectory)
if(Test-Path -LiteralPath $taskOut){throw 'Output directory already exists; no overwrite/delete is performed.'}
if(-not $SkipBuild){
  Push-Location (Join-Path $taskRepo 'My-App_Backend')
  try{ & .\mvnw.cmd -o -B verify; if($LASTEXITCODE -ne 0){throw 'Backend verify failed'} }finally{Pop-Location}
  Push-Location (Join-Path $taskRepo 'My-App_Frontend')
  $taskPreviousApi=$env:VITE_API_URL
  try{
    $env:VITE_API_URL=''
    & npm run typecheck; if($LASTEXITCODE -ne 0){throw 'Typecheck failed'}
    & npx --no-install vitest run src/admin/admin.test.tsx src/home/blog.test.ts; if($LASTEXITCODE -ne 0){throw 'Frontend tests failed'}
    & npm run build; if($LASTEXITCODE -ne 0){throw 'Frontend build failed'}
  }finally{$env:VITE_API_URL=$taskPreviousApi;Pop-Location}
}
$taskJar=Join-Path $taskRepo 'My-App_Backend/target/my-app-0.0.1-SNAPSHOT.jar'
$taskDist=Join-Path $taskRepo 'My-App_Frontend/dist'
if(!(Test-Path -LiteralPath $taskJar) -or !(Test-Path -LiteralPath (Join-Path $taskDist 'index.html'))){throw 'Build artefacts missing'}
New-Item -ItemType Directory -Path (Join-Path $taskOut 'backend'),(Join-Path $taskOut 'frontend') | Out-Null
& java --source 17 (Join-Path $PSScriptRoot 'PrepareJar.java') $taskJar (Join-Path $taskOut 'backend/app.jar')
if($LASTEXITCODE -ne 0){throw 'Release JAR preparation failed'}
Copy-Item -LiteralPath (Join-Path $PSScriptRoot 'Dockerfile.backend') -Destination (Join-Path $taskOut 'backend/Dockerfile')
Copy-Item -LiteralPath (Join-Path $PSScriptRoot 'Dockerfile.frontend') -Destination (Join-Path $taskOut 'frontend/Dockerfile')
Copy-Item -LiteralPath (Join-Path $PSScriptRoot 'nginx.production.conf') -Destination (Join-Path $taskOut 'frontend/nginx.conf')
Copy-Item -LiteralPath $taskDist -Destination (Join-Path $taskOut 'frontend/dist') -Recurse
@('*','!app.jar','!Dockerfile') | Set-Content -LiteralPath (Join-Path $taskOut 'backend/.dockerignore') -Encoding utf8
@('*','!dist/','!dist/**','!nginx.conf','!Dockerfile') | Set-Content -LiteralPath (Join-Path $taskOut 'frontend/.dockerignore') -Encoding utf8
$taskManifest=Get-ChildItem -LiteralPath $taskOut -Recurse -File | ForEach-Object {
  [pscustomobject]@{path=[IO.Path]::GetRelativePath($taskOut,$_.FullName).Replace([IO.Path]::DirectorySeparatorChar,[char]'/');sha256=(Get-FileHash -LiteralPath $_.FullName -Algorithm SHA256).Hash.ToLowerInvariant()}
}
$taskManifest | ConvertTo-Json -Depth 3 | Set-Content -LiteralPath (Join-Path $taskOut 'sha256.json') -Encoding utf8
Write-Output 'Isolated build contexts prepared. No image built/pushed, no database or NAS contacted.'

