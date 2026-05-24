@echo off
title Instalador - Restaurante Doña Lola
color 0E

echo ========================================
echo   RESTAURANTE DOÑA LOLA - INSTALADOR
echo ========================================
echo.
echo Este script configurará todo automáticamente
echo.
pause

REM Verificar requisitos
echo.
echo [PASO 1/6] Verificando requisitos...
echo.

java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [X] Java NO encontrado
    echo     Descargue Java 17 desde: https://adoptium.net/
    pause
    exit /b 1
) else (
    echo [OK] Java encontrado
)

node -v >nul 2>&1
if %errorlevel% neq 0 (
    echo [X] Node.js NO encontrado
    echo     Descargue Node.js desde: https://nodejs.org/
    pause
    exit /b 1
) else (
    echo [OK] Node.js encontrado
)

mvn -v >nul 2>&1
if %errorlevel% neq 0 (
    echo [X] Maven NO encontrado
    echo     Descargue Maven desde: https://maven.apache.org/
    pause
    exit /b 1
) else (
    echo [OK] Maven encontrado
)

pg_isready -h localhost -p 5432 >nul 2>&1
if %errorlevel% neq 0 (
    echo [!] PostgreSQL no responde
    echo     Asegúrese de que PostgreSQL esté instalado e iniciado
    echo     Descargue desde: https://www.postgresql.org/
    set /p continuar="¿Desea continuar de todos modos? (S/N): "
    if /i not "%continuar%"=="S" exit /b 1
) else (
    echo [OK] PostgreSQL corriendo
)

REM Configurar Base de Datos
echo.
echo [PASO 2/6] Configurando Base de Datos...
echo.
set /p db_password="Ingrese la contraseña de PostgreSQL (usuario postgres): "

echo Creando base de datos...
set PGPASSWORD=%db_password%
psql -U postgres -c "CREATE DATABASE restaurante_lola;" 2>nul
if %errorlevel% equ 0 (
    echo [OK] Base de datos creada
) else (
    echo [!] Base de datos ya existe o error al crear
)

echo Ejecutando scripts SQL...
psql -U postgres -d restaurante_lola -f database/scripts/01_create_tables.sql >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Tablas creadas
) else (
    echo [!] Error al crear tablas
)

psql -U postgres -d restaurante_lola -f database/scripts/02_insert_data.sql >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Datos iniciales insertados
) else (
    echo [!] Error al insertar datos
)

REM Configurar application.properties
echo.
echo [PASO 3/6] Configurando Backend...
echo.

set props_file=src\main\resources\application.properties

echo Actualizando application.properties...
(
echo spring.application.name=Restaurante Doña Lola
echo.
echo # Database Configuration
echo spring.datasource.url=jdbc:postgresql://localhost:5432/restaurante_lola
echo spring.datasource.username=postgres
echo spring.datasource.password=%db_password%
echo spring.datasource.driver-class-name=org.postgresql.Driver
echo.
echo # JPA Configuration
echo spring.jpa.hibernate.ddl-auto=update
echo spring.jpa.show-sql=false
echo spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
echo spring.jpa.properties.hibernate.format_sql=true
echo.
echo # Server Configuration
echo server.port=8080
echo.
echo # JWT Configuration
echo jwt.secret=RestauranteLolaSecretKey2024SuperSecureKeyForJWTTokenGeneration
echo jwt.expiration=86400000
) > %props_file%

echo [OK] Backend configurado

REM Instalar dependencias del Frontend
echo.
echo [PASO 4/6] Instalando dependencias del Frontend...
echo.

if exist "tialola-frontend" (
    cd tialola-frontend
    echo Ejecutando npm install...
    call npm install
    if %errorlevel% equ 0 (
        echo [OK] Dependencias instaladas
    ) else (
        echo [!] Error al instalar dependencias
    )
    cd ..
) else (
    echo [!] Carpeta tialola-frontend no encontrada
)

REM Compilar Backend
echo.
echo [PASO 5/6] Compilando Backend...
echo.
echo Esto puede tomar varios minutos...
call mvn clean package -DskipTests
if %errorlevel% equ 0 (
    echo [OK] Backend compilado exitosamente
) else (
    echo [!] Error al compilar backend
)

REM Crear ejecutables
echo.
echo [PASO 6/6] Creando ejecutables...
echo.

echo Compilando Launcher Java...
javac RestauranteLolaLauncher.java 2>nul
if %errorlevel% equ 0 (
    echo Main-Class: RestauranteLolaLauncher > manifest.txt
    jar cvfm RestauranteLola.jar manifest.txt RestauranteLolaLauncher*.class >nul 2>&1
    del manifest.txt >nul 2>&1
    del RestauranteLolaLauncher*.class >nul 2>&1
    echo [OK] RestauranteLola.jar creado
) else (
    echo [!] Error al compilar Launcher
)

REM Crear acceso directo
echo.
echo ¿Desea crear un acceso directo en el escritorio?
set /p crear_acceso="(S/N): "
if /i "%crear_acceso%"=="S" (
    echo Set oWS = WScript.CreateObject("WScript.Shell") > CreateShortcut.vbs
    echo sLinkFile = "%USERPROFILE%\Desktop\Restaurante Doña Lola.lnk" >> CreateShortcut.vbs
    echo Set oLink = oWS.CreateShortcut(sLinkFile) >> CreateShortcut.vbs
    echo oLink.TargetPath = "%~dp0iniciar-restaurante-lola.bat" >> CreateShortcut.vbs
    echo oLink.WorkingDirectory = "%~dp0" >> CreateShortcut.vbs
    echo oLink.Description = "Restaurante Doña Lola - Sistema POS" >> CreateShortcut.vbs
    echo oLink.Save >> CreateShortcut.vbs
    cscript CreateShortcut.vbs >nul
    del CreateShortcut.vbs
    echo [OK] Acceso directo creado en el escritorio
)

REM Resumen final
echo.
echo ========================================
echo   INSTALACIÓN COMPLETADA
echo ========================================
echo.
echo El sistema está listo para usar.
echo.
echo OPCIONES PARA INICIAR:
echo   1. Doble clic en: iniciar-restaurante-lola.bat
echo   2. Doble clic en: RestauranteLola.jar
echo   3. Acceso directo en el escritorio (si lo creó)
echo.
echo USUARIOS POR DEFECTO:
echo   Usuario: dueno    / Contraseña: dueno123
echo   Usuario: cajero   / Contraseña: cajero123
echo.
echo URLs:
echo   Frontend: http://localhost:3000
echo   Backend:  http://localhost:8080
echo.
echo Para más información, consulte: INSTRUCCIONES_INICIO.md
echo.
pause

echo.
echo ¿Desea iniciar el sistema ahora?
set /p iniciar="(S/N): "
if /i "%iniciar%"=="S" (
    start "" "%~dp0iniciar-restaurante-lola.bat"
)

echo.
echo ¡Gracias por usar Restaurante Doña Lola!
echo.
pause
