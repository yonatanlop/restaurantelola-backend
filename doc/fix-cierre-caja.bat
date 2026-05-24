@echo off
echo ========================================
echo Fix: Cierre de Caja - cerrado_por
echo ========================================
echo.
echo Este script corrige el error de la columna cerrado_por
echo en la tabla cierres_caja.
echo.
echo Presiona cualquier tecla para continuar o Ctrl+C para cancelar...
pause > nul

echo.
echo Ejecutando script de migracion...
psql -U postgres -d restaurante_lola -f database\scripts\10_fix_cerrado_por.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Fix aplicado exitosamente!
    echo ========================================
    echo.
    echo Ahora puedes usar el cierre de caja sin problemas.
) else (
    echo.
    echo ========================================
    echo Error al aplicar el fix
    echo ========================================
    echo.
    echo Verifica que:
    echo 1. PostgreSQL este instalado y en el PATH
    echo 2. La base de datos 'restaurante_lola' exista
    echo 3. El usuario 'postgres' tenga permisos
    echo.
    echo O ejecuta manualmente desde pgAdmin/DBeaver:
    echo ALTER TABLE cierres_caja ALTER COLUMN cerrado_por DROP NOT NULL;
)

echo.
pause
