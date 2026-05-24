# Checklist para Despliegue en Producción

## ✅ Antes de Desplegar

### 1. Verificar Configuración
- [ ] `application.properties` tiene la configuración de HikariCP
- [ ] `initialization-fail-timeout=90000` está configurado (para startup de 72s)
- [ ] URL de PostgreSQL incluye `connectTimeout=10&socketTimeout=30&tcpKeepAlive=true`

### 2. Compilar Aplicación
```bash
mvn clean package -DskipTests
```

### 3. Backup de Base de Datos
```bash
# Hacer backup antes de actualizar
pg_dump -U engouser -d contabilidadRestaurante > backup_antes_fix_timeout.sql
```

---

## 🚀 Durante el Despliegue

### 1. Detener Aplicación Actual
```bash
# Encontrar el proceso
tasklist | findstr java

# Detener (reemplaza PID con el número real)
taskkill /PID <numero> /F
```

### 2. Copiar Nuevo JAR
```bash
# Copiar el nuevo JAR compilado
copy target\restaurante-lola-*.jar C:\ruta\produccion\
```

### 3. Iniciar Aplicación
```bash
# Iniciar y capturar logs
java -jar restaurante-lola-*.jar > logs\startup.log 2>&1
```

### 4. Monitorear Startup
```bash
# En otra terminal, ver los logs en tiempo real
tail -f logs\startup.log

# O en Windows
powershell Get-Content logs\startup.log -Wait
```

**Buscar estas líneas:**
```
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
Started RestauranteLolaApplication in XX.XXX seconds
Pool HikariCP - Estado: OK | Activas: 0 | Inactivas: 2 | Total: 2 | Esperando: 0
```

---

## 🔍 Verificación Post-Despliegue

### 1. Verificar que la App Inició Correctamente

**Tiempo esperado:** 60-80 segundos

```bash
# Verificar que el proceso está corriendo
tasklist | findstr java

# Verificar que responde
curl http://localhost:8080/api/dashboard
```

### 2. Revisar Logs de Inicio

```bash
# Buscar el tiempo de inicio
findstr "Started RestauranteLolaApplication" logs\startup.log
```

Deberías ver:
```
Started RestauranteLolaApplication in 72.345 seconds (JVM running for 73.123)
```

✅ **Si es < 90 segundos**: Todo bien
❌ **Si es > 90 segundos**: Aumentar `initialization-fail-timeout` a 120000

### 3. Verificar Estado del Pool

Buscar en logs:
```
Pool HikariCP - Estado: OK | Activas: X | Inactivas: Y | Total: Z | Esperando: 0
```

✅ **Esperando: 0** = Todo bien
⚠️ **Esperando: > 0** = Hay problemas, revisar

### 4. Probar Operaciones Críticas

- [ ] Login de usuario
- [ ] Crear un plato nuevo
- [ ] Registrar una venta
- [ ] Abrir caja registradora
- [ ] Cerrar caja

**Cada operación debería completarse en < 5 segundos**

---

## 📊 Monitoreo Continuo (Primeras 24 Horas)

### Cada 2 Horas, Revisar:

```bash
# Ver últimas 50 líneas de logs
tail -n 50 logs\application.log

# Buscar errores
findstr "ERROR" logs\application.log
findstr "timeout" logs\application.log
findstr "Connection" logs\application.log
```

### Logs Buenos (Normales):
```
Pool HikariCP - Estado: OK | Activas: 2 | Inactivas: 1 | Total: 3 | Esperando: 0
```

### Logs de Alerta (Investigar):
```
⚠️ Pool HikariCP - ALERTA: 3 threads esperando conexión
⚠️ Pool HikariCP - Pool saturado | Activas: 5
```

### Logs Críticos (Acción Inmediata):
```
ERROR: HikariPool-1 - Connection is not available, request timed out
ERROR: SocketTimeoutException: Read timed out
```

**Si ves logs críticos:**
1. Revisar estado de PostgreSQL: `pg_ctl status`
2. Revisar conexiones activas: `SELECT * FROM pg_stat_activity;`
3. Si persiste, reiniciar PostgreSQL (no la app)

---

## 🛠️ Troubleshooting

### Problema 1: App No Inicia (Timeout al Startup)

**Síntoma:**
```
ERROR: HikariPool-1 - Exception during pool initialization
Caused by: SocketTimeoutException: connect timed out
```

**Solución:**
```properties
# Aumentar timeout de inicialización
spring.datasource.hikari.initialization-fail-timeout=120000
```

### Problema 2: Operaciones Lentas Durante el Día

**Síntoma:**
```
⚠️ Pool HikariCP - Pool saturado | Activas: 5 | Inactivas: 0 | Total: 5
```

**Solución Temporal:**
```properties
# Aumentar tamaño del pool (con precaución)
spring.datasource.hikari.maximum-pool-size=7
```

**Solución Permanente:**
- Optimizar queries lentas
- Agregar índices a tablas
- Ejecutar VACUUM en PostgreSQL

### Problema 3: Timeouts Aleatorios

**Síntoma:**
```
ERROR: SocketTimeoutException: Read timed out
```

**Causas posibles:**
1. Disco mecánico muy lento → Considerar SSD
2. PostgreSQL sin mantenimiento → Ejecutar VACUUM
3. Queries sin índices → Revisar con EXPLAIN ANALYZE

**Solución Inmediata:**
```sql
-- Ejecutar en PostgreSQL
VACUUM ANALYZE;
REINDEX DATABASE contabilidadRestaurante;
```

---

## 📈 Métricas de Éxito

### Después de 1 Semana:

- [ ] No ha sido necesario reiniciar la aplicación
- [ ] No hay errores de timeout en logs
- [ ] Pool HikariCP muestra "Esperando: 0" consistentemente
- [ ] Operaciones completan en < 5 segundos
- [ ] Usuarios no reportan lentitud

### Si Todas las Métricas son ✅:
**¡El fix fue exitoso!** 🎉

### Si Alguna Métrica es ❌:
Revisar sección de Troubleshooting o contactar soporte.

---

## 🔄 Mantenimiento Preventivo

### Semanal:
```sql
-- Ejecutar en PostgreSQL
VACUUM ANALYZE;
```

### Mensual:
```sql
-- Reindexar base de datos
REINDEX DATABASE contabilidadRestaurante;
```

### Trimestral:
```bash
# Limpiar logs antiguos
del logs\*.log.old
```

---

## 📞 Contacto de Emergencia

Si el problema persiste después de aplicar todas las soluciones:

1. Capturar logs completos: `logs\application.log`
2. Capturar estado de PostgreSQL: `SELECT * FROM pg_stat_activity;`
3. Capturar métricas del sistema: `tasklist`, `netstat -an`
4. Contactar al equipo de desarrollo con esta información

---

## ✅ Checklist Final

Antes de dar por terminado el despliegue:

- [ ] Aplicación inició en < 90 segundos
- [ ] Pool HikariCP muestra estado OK
- [ ] Todas las operaciones críticas funcionan
- [ ] No hay errores en logs
- [ ] Usuarios pueden trabajar normalmente
- [ ] Monitoreo configurado para próximas 24 horas
- [ ] Backup de base de datos guardado
- [ ] Documentación actualizada

**Fecha de despliegue:** _______________
**Responsable:** _______________
**Tiempo de startup observado:** _______________
**Estado final:** _______________
