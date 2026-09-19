# PowerShell script to automatically stage, commit, and push changes to GitHub
# Run this script from the root of the backend folder using: .\auto-commit.ps1

# Set the working directory to the directory where this script is located
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
Set-Location -Path $ScriptDir

# Try to find Git
$gitPath = "git"
if (-not (Get-Command "git" -ErrorAction SilentlyContinue)) {
    # If git is not in global PATH, try standard Windows installation paths
    if (Test-Path "C:\Program Files\Git\cmd\git.exe") {
        $gitPath = "C:\Program Files\Git\cmd\git.exe"
    } elseif (Test-Path "C:\Program Files (x86)\Git\cmd\git.exe") {
        $gitPath = "C:\Program Files (x86)\Git\cmd\git.exe"
    } elseif (Test-Path "$env:LOCALAPPDATA\Programs\Git\cmd\git.exe") {
        $gitPath = "$env:LOCALAPPDATA\Programs\Git\cmd\git.exe"
    } else {
        Write-Error "❌ Git could not be located on your machine. Please make sure Git is installed."
        exit 1
    }
}

Write-Host "🔍 Found Git at: $gitPath" -ForegroundColor Cyan

# 1. Stage all changes
Write-Host "📦 Staging all local changes..." -ForegroundColor Yellow
& $gitPath add -A

# 2. Get current timestamp for commit message
$timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
$commitMessage = "Auto-commit: Updates on $timestamp"

# 3. Commit changes
Write-Host "💾 Committing changes with message: '$commitMessage'..." -ForegroundColor Yellow
& $gitPath commit -m $commitMessage

# 4. Push to remote main branch
Write-Host "🚀 Pushing changes to GitHub (origin main)..." -ForegroundColor Yellow
& $gitPath push -u origin main

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Successfully pushed all changes to GitHub!" -ForegroundColor Green
} else {
    Write-Host "❌ Failed to push changes. Check if you have created the repository and set the remote 'origin' correctly." -ForegroundColor Red
}
