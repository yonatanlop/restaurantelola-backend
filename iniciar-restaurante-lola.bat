@echo off
title Restaurante Doña Lola - Iniciando Sistema
color 0A

echo ========================================
echo    RESTAURANTE DOÑA LOLA - SISTEMA POS
echo ========================================
echo.
echo Iniciando servicios...
echo.

REM Verificar si Java está instalado
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java no está instalado o no está en el PATH
    echo Por favor instale Java 17 o superior
    pause
    exit /b 1
)

REM Verificar si Node.js está instalado
node -v >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Node.js no está instalado o no está en el PATH
    echo Por favor instale Node.js
    pause
    exit /b 1
)

REM Verificar si PostgreSQL está corriendo
pg_isready -h localhost -p 5432 >nul 2>&1
if %errorlevel% neq 0 (
    echo [ADVERTENCIA] PostgreSQL no está corriendo
    echo Intentando iniciar PostgreSQL...
    net start postgresql-x64-14 >nul 2>&1
    timeout /t 3 /nobreak >nul
)

echo [1/3] Iniciando Backend (Spring Boot)...
start "Backend - Restaurante Doña Lola" cmd /k "cd /d "%~dp0" && mvn spring-boot:run"

echo [2/3] Esperando que el backend inicie...
timeout /t 80 /nobreak >nul

echo [3/3] Iniciando Frontend (React)...
start "Frontend - Restaurante Doña Lola" cmd /k "cd /d "%~dp0tialola-frontend" && npm run dev"

echo.
echo Esperando que el frontend inicie...
timeout /t 10 /nobreak >nul

echo.
echo ========================================
echo   SISTEMA INICIADO CORRECTAMENTE
echo ========================================
echo.
echo Backend:  http://localhost:8080
echo Frontend: http://localhost:3000
echo.
echo Abriendo navegador...
timeout /t 3 /nobreak >nul

REM Abrir navegador
start http://localhost:3000

echo.
echo El sistema está corriendo.
echo NO CIERRE ESTA VENTANA.
echo.
echo Para detener el sistema:
echo 1. Cierre las ventanas de Backend y Frontend
echo 2. O presione Ctrl+C en cada ventana
echo.
pause
