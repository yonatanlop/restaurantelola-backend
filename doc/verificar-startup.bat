@echo off
REM Script para verificar el tiempo de startup de la aplicación
REM Útil para monitorear el rendimiento en hardware lento

echo ========================================
echo Verificador de Startup - Restaurante Lola
echo ========================================
echo.

REM Obtener tiempo de inicio
set START_TIME=%TIME%
echo [%START_TIME%] Iniciando aplicacion...
echo.

REM Iniciar la aplicación (ajusta la ruta si es necesario)
echo Ejecutando: mvn spring-boot:run
echo.
echo NOTA: Presiona Ctrl+C para detener cuando veas "Started RestauranteLolaApplication"
echo.

mvn spring-boot:run

REM Si llegas aquí, la app se detuvo
set END_TIME=%TIME%
echo.
echo ========================================
echo [%END_TIME%] Aplicacion detenida
echo Tiempo inicio: %START_TIME%
echo Tiempo fin:    %END_TIME%
echo ========================================
echo.
echo Para calcular el tiempo exacto, revisa los logs de Spring Boot
echo Busca la línea: "Started RestauranteLolaApplication in X.XXX seconds"
echo.
pause
