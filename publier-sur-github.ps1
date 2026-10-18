# Publication initiale - necessite Git et GitHub CLI (gh) connecte a baaahaaaa.
# Ne modifie aucun depot preexistant ; s'arrete si le nom est deja pris.
param([ValidateSet('private','public')][string]$Visibility = 'private')
$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot
foreach ($cmd in @('git','gh')) {
    if (-not (Get-Command $cmd -ErrorAction SilentlyContinue)) {
        throw "$cmd introuvable. Installer Git et GitHub CLI (gh)."
    }
}
& gh auth status
if ($LASTEXITCODE -ne 0) { throw 'Connectez-vous avec : gh auth login' }
$target = 'baaahaaaa/globalnet-agency-management'
& gh repo view $target --json name 2>$null | Out-Null
if ($LASTEXITCODE -eq 0) {
    throw "Le depot $target existe deja. Rien n'a ete ecrase."
}
if (-not (Test-Path '.git')) { & git init -b main; if ($LASTEXITCODE -ne 0) { throw 'Echec git init' } }
& git add .
if ($LASTEXITCODE -ne 0) { throw 'Echec git add' }
$tracked = @(& git diff --cached --name-only)
if ($tracked | Where-Object { $_ -match '(^|/)(\.env$|\.env\.local$|target/|\.git/|node_modules/)' }) {
    throw 'Fichiers sensibles ou generes detectes dans les fichiers indexes. Publication annulee.'
}
if (-not $tracked -and -not (git rev-parse --verify HEAD 2>$null)) { throw 'Aucun fichier a publier.' }
& git diff --cached --quiet
if ($LASTEXITCODE -ne 0) {
    & git commit -m 'feat: publish secured GlobalNet agency-management backend'
    if ($LASTEXITCODE -ne 0) { throw 'Echec du commit : configurez git user.name et user.email.' }
}
$visOpt = if ($Visibility -eq 'public') { '--public' } else { '--private' }
& gh repo create $target $visOpt --source . --remote origin --push --description 'GlobalNet Internship - Agency management REST API (Spring Boot / JWT)'
if ($LASTEXITCODE -ne 0) { throw 'Publication impossible : verifier les droits GitHub et le nom du depot.' }
Write-Host "Depot cree : https://github.com/$target"
Write-Host 'Verifier les tests GitHub Actions et la securite avant de partager le lien dans le CV.'
