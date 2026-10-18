# Lancement d'une demonstration locale (PowerShell 5.1+)
# JDK 17+ requis. Maven Wrapper telecharge Maven au premier lancement.
$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot
if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    throw 'Java absent. Installer le JDK 17 ou superieur puis relancer.'
}
if (-not (Test-Path '.\mvnw.cmd')) { throw 'mvnw.cmd introuvable.' }

# Des identifiants aleatoires sont generes seulement pour cette session.
function New-RandomString([int]$length = 48) {
    $bytes = New-Object byte[] $length
    $rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
    try { $rng.GetBytes($bytes) } finally { $rng.Dispose() }
    return [Convert]::ToBase64String($bytes)
}
$env:JWT_SECRET = New-RandomString 48
$env:APP_ADMIN_USERNAME = 'admin'
$env:APP_ADMIN_PASSWORD = New-RandomString 24
$env:SPRING_PROFILES_ACTIVE = 'demo'
Write-Host '=== GlobalNet demo local ==='
Write-Host 'URL: http://localhost:8081/swagger-ui/index.html'
Write-Host "Admin: $env:APP_ADMIN_USERNAME"
Write-Host "Mot de passe TEMPORAIRE : $env:APP_ADMIN_PASSWORD"
Write-Host 'La base H2 est en memoire et sera effacee a l arret.'
& .\mvnw.cmd spring-boot:run
if ($LASTEXITCODE -ne 0) { throw "Echec Maven / Spring Boot (exit $LASTEXITCODE)" }
