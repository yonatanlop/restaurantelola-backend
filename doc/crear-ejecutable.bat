@echo off
title Crear Ejecutable - Restaurante Doña Lola
color 0B

echo ========================================
echo   CREAR EJECUTABLE .EXE
echo ========================================
echo.

REM Verificar si existe IExpress (viene con Windows)
where iexpress >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] IExpress no está disponible
    pause
    exit /b 1
)

echo Creando archivo de configuración...

REM Crear archivo SED para IExpress
(
echo [Version]
echo Class=IEXPRESS
echo SEDVersion=3
echo [Options]
echo PackagePurpose=InstallApp
echo ShowInstallProgramWindow=0
echo HideExtractAnimation=1
echo UseLongFileName=1
echo InsideCompressed=0
echo CAB_FixedSize=0
echo CAB_ResvCodeSigning=0
echo RebootMode=N
echo InstallPrompt=%%InstallPrompt%%
echo DisplayLicense=%%DisplayLicense%%
echo FinishMessage=%%FinishMessage%%
echo TargetName=%%TargetName%%
echo FriendlyName=%%FriendlyName%%
echo AppLaunched=%%AppLaunched%%
echo PostInstallCmd=%%PostInstallCmd%%
echo AdminQuietInstCmd=%%AdminQuietInstCmd%%
echo UserQuietInstCmd=%%UserQuietInstCmd%%
echo SourceFiles=SourceFiles
echo [Strings]
echo InstallPrompt=
echo DisplayLicense=
echo FinishMessage=
echo TargetName=%~dp0RestauranteLola.exe
echo FriendlyName=Restaurante Doña Lola - Sistema POS
echo AppLaunched=cmd /c iniciar-restaurante-lola.bat
echo PostInstallCmd=^<None^>
echo AdminQuietInstCmd=
echo UserQuietInstCmd=
echo FILE0="iniciar-restaurante-lola.bat"
echo [SourceFiles]
echo SourceFiles0=%~dp0
echo [SourceFiles0]
echo %%FILE0%%=
) > "%TEMP%\restaurante-lola-setup.sed"

echo Generando ejecutable...
iexpress /N /Q "%TEMP%\restaurante-lola-setup.sed"

if exist "%~dp0RestauranteLola.exe" (
    echo.
    echo ========================================
    echo   EJECUTABLE CREADO EXITOSAMENTE
    echo ========================================
    echo.
    echo Archivo: RestauranteLola.exe
    echo Ubicación: %~dp0
    echo.
    echo Ahora puede hacer doble clic en RestauranteLola.exe
    echo para iniciar el sistema.
    echo.
) else (
    echo.
    echo [ERROR] No se pudo crear el ejecutable
    echo.
)

del "%TEMP%\restaurante-lola-setup.sed" >nul 2>&1

pause
