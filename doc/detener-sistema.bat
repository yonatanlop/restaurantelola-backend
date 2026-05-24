@echo off
title Detener Sistema - Restaurante Doña Lola
color 0C

echo ========================================
echo   DETENER SISTEMA - RESTAURANTE DOÑA LOLA
echo ========================================
echo.

echo Buscando procesos del sistema...
echo.

REM Detener Backend (puerto 8080)
echo [1/2] Deteniendo Backend...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080') do (
    echo     Deteniendo proceso %%a
    taskkill /PID %%a /F >nul 2>&1
)
echo     ✓ Backend detenido

REM Detener Frontend (puerto 3000)
echo [2/2] Deteniendo Frontend...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :3000') do (
    echo     Deteniendo proceso %%a
    taskkill /PID %%a /F >nul 2>&1
)
echo     ✓ Frontend detenido

echo.
echo ========================================
echo   SISTEMA DETENIDO
echo ========================================
echo.
pause
