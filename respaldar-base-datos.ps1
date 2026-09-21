# Genera un respaldo (pg_dump) de la base de datos de Restaurante Dona Lola.
#
# Totalmente automatico, sin necesidad de escribir nada:
#   1. Busca donde esta instalado PostgreSQL en este equipo (pg_dump.exe).
#   2. Lee host/puerto/base/usuario/contrasena del application.properties
#      local (la misma configuracion que usa la aplicacion para conectarse).
#   3. Verifica que se pueda conectar.
#   4. Genera el respaldo en formato custom (comprimido, listo para
#      pg_restore) dentro de la carpeta "backups" junto a este script.
#
# Se invoca normalmente desde respaldar-base-datos.bat (doble clic).

$ErrorActionPreference = 'Stop'
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

function Write-Header([string]$text) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host " $text" -ForegroundColor Cyan
    Write-Host "========================================" -ForegroundColor Cyan
}

Write-Header "Respaldo de Base de Datos - Restaurante Dona Lola"

# 1) Ubicar pg_dump.exe --------------------------------------------------
Write-Host "[1/5] Buscando instalacion de PostgreSQL en este equipo..."

$pgDump = $null

$cmd = Get-Command pg_dump.exe -ErrorAction SilentlyContinue
if ($cmd) { $pgDump = $cmd.Source }

if (-not $pgDump) {
    $roots = @("$env:ProgramFiles\PostgreSQL", "${env:ProgramFiles(x86)}\PostgreSQL") |
        Where-Object { Test-Path $_ }

    $versionDirs = foreach ($root in $roots) {
        Get-ChildItem -Path $root -Directory -ErrorAction SilentlyContinue
    }
    # Probar primero la version mas reciente instalada
    $versionDirs = $versionDirs | Sort-Object { [int]($_.Name -replace '\D', '0') } -Descending

    foreach ($dir in $versionDirs) {
        $candidate = Join-Path $dir.FullName "bin\pg_dump.exe"
        if (Test-Path $candidate) { $pgDump = $candidate; break }
    }
}

if (-not $pgDump) {
    Write-Host "[ERROR] No se encontro pg_dump.exe en este equipo." -ForegroundColor Red
    Write-Host "Verifica que PostgreSQL este instalado (normalmente en"
    Write-Host "C:\Program Files\PostgreSQL\<version>\bin)."
    exit 1
}
Write-Host "  Encontrado: $pgDump"
$pgBinDir = Split-Path -Parent $pgDump

# 2) Leer configuracion real de la aplicacion -----------------------------
Write-Host "[2/5] Leyendo configuracion de application.properties..."

$propsPath = Join-Path $scriptDir "src\main\resources\application.properties"
if (-not (Test-Path $propsPath)) {
    Write-Host "[ERROR] No se encontro: $propsPath" -ForegroundColor Red
    Write-Host "Este script debe estar en la raiz del proyecto (junto a pom.xml)."
    exit 1
}

$propsContent = Get-Content -Raw -Path $propsPath

function Get-Prop([string]$name) {
    $pattern = "(?m)^\s*" + [regex]::Escape($name) + "\s*=\s*(.+?)\s*$"
    if ($propsContent -match $pattern) { return $Matches[1] }
    return $null
}

$datasourceUrl = Get-Prop 'spring.datasource.url'
$dbUser = Get-Prop 'spring.datasource.username'
$dbPass = Get-Prop 'spring.datasource.password'

if (-not $datasourceUrl -or -not $dbUser) {
    Write-Host "[ERROR] No se pudo leer spring.datasource.url / username de application.properties" -ForegroundColor Red
    exit 1
}

if ($datasourceUrl -notmatch 'jdbc:postgresql://([^:/]+)(?::(\d+))?/([^?]+)') {
    Write-Host "[ERROR] No se pudo interpretar spring.datasource.url:" -ForegroundColor Red
    Write-Host "  $datasourceUrl"
    exit 1
}
$dbHost = $Matches[1]
$dbPort = if ($Matches[2]) { $Matches[2] } else { '5432' }
$dbName = $Matches[3]

Write-Host "  Base de datos: $dbName en ${dbHost}:${dbPort} (usuario: $dbUser)"

# 3) Verificar que se pueda conectar ---------------------------------------
Write-Host "[3/5] Verificando conexion..."

$pgIsReady = Join-Path $pgBinDir "pg_isready.exe"
$env:PGPASSWORD = $dbPass

if (Test-Path $pgIsReady) {
    & $pgIsReady -h $dbHost -p $dbPort -U $dbUser -d $dbName | Out-Null
    if ($LASTEXITCODE -ne 0) {
        Write-Host "[ERROR] No se pudo conectar a PostgreSQL en ${dbHost}:${dbPort}" -ForegroundColor Red
        Write-Host "Verifica que el servicio de PostgreSQL este corriendo (services.msc)."
        Remove-Item Env:\PGPASSWORD -ErrorAction SilentlyContinue
        exit 1
    }
}
Write-Host "  Conexion OK"

# 4) Generar el respaldo ----------------------------------------------------
Write-Host "[4/5] Generando respaldo (esto puede tardar unos minutos)..."

$backupDir = Join-Path $scriptDir "backups"
if (-not (Test-Path $backupDir)) { New-Item -ItemType Directory -Path $backupDir | Out-Null }

$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$backupFile = Join-Path $backupDir "restaurantelola_$timestamp.dump"

# --no-owner/--no-privileges: el usuario de este equipo puede no existir
# en el servidor destino (el contenedor Postgres de Oracle Cloud usa su
# propio usuario), asi que el respaldo no debe depender de el.
& $pgDump -h $dbHost -p $dbPort -U $dbUser -d $dbName -Fc --no-owner --no-privileges -f $backupFile
$dumpExitCode = $LASTEXITCODE

Remove-Item Env:\PGPASSWORD -ErrorAction SilentlyContinue

if ($dumpExitCode -ne 0 -or -not (Test-Path $backupFile)) {
    Write-Host "[ERROR] pg_dump termino con errores (codigo $dumpExitCode)." -ForegroundColor Red
    exit 1
}

# 5) Resultado ----------------------------------------------------------------
$sizeMB = [math]::Round((Get-Item $backupFile).Length / 1MB, 2)

Write-Header "RESPALDO COMPLETADO"
Write-Host "Archivo:  $backupFile"
Write-Host "Tamano:   $sizeMB MB"
Write-Host ""
Write-Host "Guarda este archivo (USB, carpeta compartida, etc.) para" -ForegroundColor Yellow
Write-Host "subirlo despues al servidor en la nube." -ForegroundColor Yellow
exit 0
