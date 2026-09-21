@echo off
title Restaurante Doña Lola - Respaldo de Base de Datos
color 0A

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0respaldar-base-datos.ps1"
set EXITCODE=%errorlevel%

echo.
if %EXITCODE% neq 0 (
    echo El respaldo fallo. Revisa los mensajes de arriba.
)

pause
exit /b %EXITCODE%
