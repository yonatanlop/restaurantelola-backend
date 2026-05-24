@echo off
title Compilar Launcher - Restaurante Doña Lola
color 0B

echo ========================================
echo   COMPILAR LAUNCHER JAVA
echo ========================================
echo.

REM Verificar Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java no está instalado
    pause
    exit /b 1
)

echo [1/3] Compilando RestauranteLolaLauncher.java...
javac RestauranteLolaLauncher.java

if %errorlevel% neq 0 (
    echo [ERROR] Error al compilar
    pause
    exit /b 1
)

echo [2/3] Creando archivo JAR...
echo Main-Class: RestauranteLolaLauncher > manifest.txt
jar cvfm RestauranteLola.jar manifest.txt RestauranteLolaLauncher*.class

if %errorlevel% neq 0 (
    echo [ERROR] Error al crear JAR
    pause
    exit /b 1
)

echo [3/3] Creando ejecutable con jpackage (Java 14+)...
where jpackage >nul 2>&1
if %errorlevel% equ 0 (
    jpackage --input . --name "RestauranteLola" --main-jar RestauranteLola.jar --main-class RestauranteLolaLauncher --type exe --win-console
    
    if exist "RestauranteLola-1.0.exe" (
        echo.
        echo ========================================
        echo   EJECUTABLE CREADO EXITOSAMENTE
        echo ========================================
        echo.
        echo Archivo: RestauranteLola-1.0.exe
        echo.
    )
) else (
    echo.
    echo [INFO] jpackage no disponible (requiere Java 14+)
    echo Se creó RestauranteLola.jar en su lugar
    echo.
    echo Para ejecutar: java -jar RestauranteLola.jar
    echo.
)

REM Limpiar archivos temporales
del manifest.txt >nul 2>&1
del RestauranteLolaLauncher*.class >nul 2>&1

echo.
pause
