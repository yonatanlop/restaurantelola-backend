@echo off
REM Script para probar el nuevo sistema de login
REM Ejecutar después de desplegar los cambios

echo ========================================
echo Pruebas del Nuevo Sistema de Login
echo ========================================
echo.

REM Verificar que el backend está corriendo
echo [1/5] Verificando que el backend está activo...
curl -s http://localhost:8080/api/auth/login > nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ ERROR: El backend no está corriendo en el puerto 8080
    echo    Inicia el backend primero: java -jar target/restaurante-lola-*.jar
    pause
    exit /b 1
)
echo ✅ Backend activo
echo.

REM Probar login de cajero
echo [2/5] Probando login de cajero (sin contraseña)...
curl -s -X POST http://localhost:8080/api/auth/login/cajero -H "Content-Type: application/json" > temp_cajero.json
findstr /C:"token" temp_cajero.json > nul
if %errorlevel% equ 0 (
    echo ✅ Login de cajero exitoso
) else (
    echo ❌ ERROR: Login de cajero falló
    echo    Verifica que existe un usuario con rol CAJERO activo
    type temp_cajero.json
)
del temp_cajero.json > nul 2>&1
echo.

REM Probar login de dueño con contraseña correcta
echo [3/5] Probando login de dueño con contraseña correcta...
curl -s -X POST http://localhost:8080/api/auth/login/dueno -H "Content-Type: application/json" -d "{\"password\":\"admin123\"}" > temp_dueno.json
findstr /C:"token" temp_dueno.json > nul
if %errorlevel% equ 0 (
    echo ✅ Login de dueño exitoso
) else (
    echo ❌ ERROR: Login de dueño falló
    echo    Verifica que existe un usuario con rol DUENO activo
    echo    y que la contraseña es "admin123"
    type temp_dueno.json
)
del temp_dueno.json > nul 2>&1
echo.

REM Probar login de dueño con contraseña incorrecta
echo [4/5] Probando login de dueño con contraseña incorrecta...
curl -s -X POST http://localhost:8080/api/auth/login/dueno -H "Content-Type: application/json" -d "{\"password\":\"wrongpassword\"}" > temp_dueno_wrong.json
findstr /C:"token" temp_dueno_wrong.json > nul
if %errorlevel% neq 0 (
    echo ✅ Contraseña incorrecta rechazada correctamente
) else (
    echo ⚠️ ADVERTENCIA: Login exitoso con contraseña incorrecta
    echo    Esto es un problema de seguridad
)
del temp_dueno_wrong.json > nul 2>&1
echo.

REM Verificar frontend
echo [5/5] Verificando que el frontend está disponible...
curl -s http://localhost:8080 > nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Frontend disponible
    echo.
    echo ========================================
    echo Pruebas completadas
    echo ========================================
    echo.
    echo Abre tu navegador en: http://localhost:8080
    echo.
    echo Deberías ver:
    echo - Botón "Cajero" (acceso directo)
    echo - Botón "Dueño" (solicita contraseña)
    echo.
    echo Contraseña del dueño: admin123
    echo.
) else (
    echo ❌ ERROR: Frontend no disponible
)

pause
