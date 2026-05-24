# Restaurante Doña Lola - Script de Inicio
# PowerShell Script para iniciar Backend y Frontend

$Host.UI.RawUI.WindowTitle = "Restaurante Doña Lola - Sistema POS"
$Host.UI.RawUI.BackgroundColor = "Black"
$Host.UI.RawUI.ForegroundColor = "Green"
Clear-Host

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   RESTAURANTE DOÑA LOLA - SISTEMA POS" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Función para verificar si un puerto está en uso
function Test-Port {
    param([int]$Port)
    $connection = Test-NetConnection -ComputerName localhost -Port $Port -WarningAction SilentlyContinue
    return $connection.TcpTestSucceeded
}

# Función para esperar que un puerto esté disponible
function Wait-ForPort {
    param([int]$Port, [int]$TimeoutSeconds = 60)
    $elapsed = 0
    while (-not (Test-Port -Port $Port) -and $elapsed -lt $TimeoutSeconds) {
        Start-Sleep -Seconds 2
        $elapsed += 2
        Write-Host "." -NoNewline
    }
    Write-Host ""
    return (Test-Port -Port $Port)
}

# Verificar Java
Write-Host "[CHECK] Verificando Java..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "  ✓ Java encontrado: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "  ✗ Java no encontrado. Por favor instale Java 17 o superior." -ForegroundColor Red
    Read-Host "Presione Enter para salir"
    exit 1
}

# Verificar Node.js
Write-Host "[CHECK] Verificando Node.js..." -ForegroundColor Yellow
try {
    $nodeVersion = node -v
    Write-Host "  ✓ Node.js encontrado: $nodeVersion" -ForegroundColor Green
} catch {
    Write-Host "  ✗ Node.js no encontrado. Por favor instale Node.js." -ForegroundColor Red
    Read-Host "Presione Enter para salir"
    exit 1
}

# Verificar PostgreSQL
Write-Host "[CHECK] Verificando PostgreSQL..." -ForegroundColor Yellow
try {
    $pgStatus = pg_isready -h localhost -p 5432 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  ✓ PostgreSQL está corriendo" -ForegroundColor Green
    } else {
        Write-Host "  ⚠ PostgreSQL no responde. Intentando iniciar..." -ForegroundColor Yellow
        Start-Service -Name "postgresql-x64-14" -ErrorAction SilentlyContinue
        Start-Sleep -Seconds 3
    }
} catch {
    Write-Host "  ⚠ No se pudo verificar PostgreSQL" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   INICIANDO SERVICIOS" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Verificar si los puertos están ocupados
if (Test-Port -Port 8080) {
    Write-Host "⚠ Puerto 8080 ya está en uso. Deteniendo proceso..." -ForegroundColor Yellow
    $process = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess
    if ($process) {
        Stop-Process -Id $process -Force -ErrorAction SilentlyContinue
        Start-Sleep -Seconds 2
    }
}

if (Test-Port -Port 3000) {
    Write-Host "⚠ Puerto 3000 ya está en uso. Deteniendo proceso..." -ForegroundColor Yellow
    $process = Get-NetTCPConnection -LocalPort 3000 -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess
    if ($process) {
        Stop-Process -Id $process -Force -ErrorAction SilentlyContinue
        Start-Sleep -Seconds 2
    }
}

# Iniciar Backend
Write-Host "[1/3] Iniciando Backend (Spring Boot)..." -ForegroundColor Cyan
$backendPath = $PSScriptRoot
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$backendPath'; Write-Host 'Backend - Restaurante Doña Lola' -ForegroundColor Green; mvn spring-boot:run"

Write-Host "      Esperando que el backend inicie" -NoNewline -ForegroundColor Yellow
if (Wait-ForPort -Port 8080 -TimeoutSeconds 60) {
    Write-Host "  ✓ Backend iniciado correctamente" -ForegroundColor Green
} else {
    Write-Host "  ✗ Backend no pudo iniciar en el tiempo esperado" -ForegroundColor Red
}

# Iniciar Frontend
Write-Host "[2/3] Iniciando Frontend (React + Vite)..." -ForegroundColor Cyan
$frontendPath = Join-Path $PSScriptRoot "tialola-frontend"
if (Test-Path $frontendPath) {
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$frontendPath'; Write-Host 'Frontend - Restaurante Doña Lola' -ForegroundColor Green; npm run dev"
    
    Write-Host "      Esperando que el frontend inicie" -NoNewline -ForegroundColor Yellow
    if (Wait-ForPort -Port 3000 -TimeoutSeconds 30) {
        Write-Host "  ✓ Frontend iniciado correctamente" -ForegroundColor Green
    } else {
        Write-Host "  ✗ Frontend no pudo iniciar en el tiempo esperado" -ForegroundColor Red
    }
} else {
    Write-Host "  ✗ Carpeta del frontend no encontrada: $frontendPath" -ForegroundColor Red
}

# Abrir navegador
Write-Host "[3/3] Abriendo navegador..." -ForegroundColor Cyan
Start-Sleep -Seconds 3
Start-Process "http://localhost:3000"

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   SISTEMA INICIADO CORRECTAMENTE" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Backend:  http://localhost:8080" -ForegroundColor White
Write-Host "Frontend: http://localhost:3000" -ForegroundColor White
Write-Host ""
Write-Host "El sistema está corriendo." -ForegroundColor Green
Write-Host "NO CIERRE ESTA VENTANA." -ForegroundColor Yellow
Write-Host ""
Write-Host "Para detener el sistema:" -ForegroundColor White
Write-Host "  1. Cierre las ventanas de Backend y Frontend" -ForegroundColor Gray
Write-Host "  2. O presione Ctrl+C en cada ventana" -ForegroundColor Gray
Write-Host ""

Read-Host "Presione Enter para salir de este asistente (los servicios seguirán corriendo)"
