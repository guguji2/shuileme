# Local Image Export Script
# Usage: Run .\export-image.ps1 in PowerShell

$ErrorActionPreference = "Stop"

Write-Host "=== Slema Backend Image Export Tool ===" -ForegroundColor Green

# Check if Docker is running
try {
    docker ps | Out-Null
} catch {
    Write-Host "Error: Docker Desktop is not running. Please start Docker Desktop first." -ForegroundColor Red
    exit 1
}

# Set paths
$ProjectRoot = "D:\app\slema"
$DockerDir = "$ProjectRoot\docker"
$OutputDir = "$DockerDir\output"
$TarFile = "$OutputDir\slema-backend.tar"
$GzFile = "$OutputDir\slema-backend.tar.gz"

# Create output directory
if (!(Test-Path $OutputDir)) {
    New-Item -ItemType Directory -Path $OutputDir | Out-Null
    Write-Host "Created output directory: $OutputDir" -ForegroundColor Yellow
}

# Step 1: Build image
Write-Host "`n[1/3] Building Docker image..." -ForegroundColor Cyan
cd $ProjectRoot
docker build -f docker/Dockerfile -t slema-backend:latest .

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed!" -ForegroundColor Red
    exit 1
}

# Step 2: Export image
Write-Host "`n[2/3] Exporting image to tar file..." -ForegroundColor Cyan
if (Test-Path $TarFile) {
    Remove-Item $TarFile
}
docker save slema-backend:latest -o $TarFile

# Get file size
$FileSize = (Get-Item $TarFile).Length / 1MB
Write-Host "Image exported: $TarFile ($([math]::Round($FileSize, 2)) MB)" -ForegroundColor Green

# Step 3: Compress (optional)
Write-Host "`n[3/3] Compressing tar file..." -ForegroundColor Cyan
if (Test-Path $GzFile) {
    Remove-Item $GzFile
}

# Use tar to compress (requires Git Bash or WSL)
$TarPath = "C:\Program Files\Git\usr\bin\tar.exe"
if (Test-Path $TarPath) {
    & $TarPath -czf $GzFile -C $OutputDir slema-backend.tar
    $GzSize = (Get-Item $GzFile).Length / 1MB
    $Savings = [math]::Round((1 - $GzSize / $FileSize) * 100, 1)
    Write-Host "Compression complete: $GzFile ($([math]::Round($GzSize, 2)) MB, saved $Savings%)" -ForegroundColor Green
} else {
    Write-Host "tar command not found, skipping compression" -ForegroundColor Yellow
    Write-Host "To compress, please install Git for Windows or WSL" -ForegroundColor Yellow
}

# Done
Write-Host "`n=== Export Complete ===" -ForegroundColor Green
Write-Host "`nNext steps:" -ForegroundColor Yellow
Write-Host "1. Upload $GzFile to server" -ForegroundColor White
Write-Host "2. Extract on server: tar -xzf slema-backend.tar.gz" -ForegroundColor White
Write-Host "3. Load image: docker load -i slema-backend.tar" -ForegroundColor White
Write-Host "`nUpload command example:" -ForegroundColor Yellow
Write-Host "scp $GzFile root@your-server-ip:/root/" -ForegroundColor White
