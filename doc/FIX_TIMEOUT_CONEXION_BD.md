# Fix: Timeout de Conexión a Base de Datos

## Problema Identificado

**Error experimentado:**
```
WARN: SQL Error: 0, SQLState: 08001
ERROR: HikariPool-1 - Connection is not available, request timed out
ERROR: El intento de conexión falló
Root cause: SocketTimeoutException: Read timed out
```

## Causa Raíz

El problema ocurre por una combinación de factores en hardware limitado (disco mecánico):

1. **Pool de conexiones agotado**: HikariCP no tenía configuración específica y usaba valores por defecto inadecuados
2. **PostgreSQL lento**: El disco mecánico causa I/O lento, haciendo que las queries tarden más
3. **Conexiones colgadas**: Sin timeouts configurados, las conexiones quedaban esperando indefinidamente
4. **Sin recuperación automática**: El pool no podía detectar y cerrar conexiones problemáticas

## Solución Implementada

### 1. Configuración de HikariCP (Pool de Conexiones)

```properties
# Pool reducido para hardware limitado
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.maximum-pool-size=5

# Timeouts configurados
spring.datasource.hikari.connection-timeout=30000        # 30 seg para obtener conexión
spring.datasource.hikari.initialization-fail-timeout=90000  # 90 seg para startup lento
spring.datasource.hikari.max-lifetime=600000             # 10 min vida máxima
spring.datasource.hikari.idle-timeout=300000             # 5 min inactividad
spring.datasource.hikari.validation-timeout=5000         # 5 seg validación

# Detección de problemas
spring.datasource.hikari.connection-test-query=SELECT 1
spring.datasource.hikari.leak-detection-threshold=60000  # Detecta fugas
```

**IMPORTANTE - Diferencia entre Startup y Operación:**

- **initialization-fail-timeout=90000**: Aplica SOLO al inicio de la aplicación. Permite hasta 90 segundos para crear el pool inicial. Esto es crítico en hardware lento donde el startup puede tardar 72+ segundos.

- **connection-timeout=30000**: Aplica durante la operación normal (cuando la app ya está corriendo). Es el tiempo máximo para obtener una conexión del pool cuando se hace una petición.

Estos son dos momentos diferentes y no interfieren entre sí.

### 2. Configuración de PostgreSQL JDBC

```properties
# URL con parámetros de timeout
jdbc:postgresql://localhost:5432/contabilidadRestaurante?connectTimeout=10&socketTimeout=30&tcpKeepAlive=true
```

- **connectTimeout=10**: 10 segundos para establecer conexión inicial
- **socketTimeout=30**: 30 segundos para operaciones de lectura/escritura
- **tcpKeepAlive=true**: Mantiene la conexión activa y detecta desconexiones

### 3. Configuración de Transacciones

```properties
spring.transaction.default-timeout=60  # 60 segundos máximo por transacción
```

## Beneficios de la Configuración

✅ **Recuperación automática**: El pool detecta y cierra conexiones problemáticas
✅ **Prevención de bloqueos**: Los timeouts evitan esperas indefinidas
✅ **Detección de fugas**: Identifica conexiones que no se cierran correctamente
✅ **Optimizado para hardware limitado**: Pool pequeño reduce carga en el sistema
✅ **Validación de conexiones**: Verifica que las conexiones estén vivas antes de usarlas

## Recomendaciones Adicionales

### Para PostgreSQL (Configuración del Servidor)

Editar `postgresql.conf`:

```conf
# Timeouts del servidor
statement_timeout = 30000              # 30 segundos por query
idle_in_transaction_session_timeout = 60000  # 60 seg transacciones inactivas

# Optimización para disco mecánico
shared_buffers = 256MB                 # Caché en memoria
effective_cache_size = 1GB             # Memoria disponible para caché
random_page_cost = 4.0                 # Penalización para disco mecánico
checkpoint_completion_target = 0.9     # Suavizar checkpoints

# Conexiones
max_connections = 20                   # Limitar conexiones totales
```

### Monitoreo de Conexiones

Se ha creado un componente de monitoreo automático en:
`src/main/java/com/tialola/config/HikariMonitorConfig.java`

Este componente:
- ✅ Muestra el estado del pool cuando la app termina de iniciar
- ✅ Monitorea el pool cada 2 minutos durante operación
- ✅ Genera alertas si hay threads esperando conexiones
- ✅ Detecta cuando el pool está saturado

**Logs que verás:**

```
# Al iniciar la aplicación
========================================
Aplicación iniciada correctamente
========================================
Pool HikariCP - Estado: OK | Activas: 0 | Inactivas: 2 | Total: 2 | Esperando: 0

# Durante operación normal (cada 2 minutos)
Pool HikariCP - Estado: OK | Activas: 2 | Inactivas: 1 | Total: 3 | Esperando: 0

# Si hay problemas
⚠️ Pool HikariCP - ALERTA: 3 threads esperando conexión | Activas: 5 | Inactivas: 0 | Total: 5
⚠️ Pool HikariCP - Pool saturado | Activas: 5 | Inactivas: 0 | Total: 5
```

**Si los logs son muy frecuentes**, puedes:
1. Aumentar el intervalo: `@Scheduled(fixedRate = 300000)` (5 minutos)
2. Comentar el método `monitorearPoolPeriodico()` completo
3. Comentar `@EnableScheduling` en la clase

### Mantenimiento Preventivo

1. **Reiniciar PostgreSQL periódicamente** (semanal): Limpia conexiones zombies
2. **Monitorear logs**: Buscar patrones de "Connection timeout"
3. **VACUUM regular**: `VACUUM ANALYZE;` para optimizar tablas
4. **Revisar queries lentas**: Activar `log_min_duration_statement = 1000` en PostgreSQL

### Si el Problema Persiste

**Opción 1: Aumentar recursos de PostgreSQL**
```properties
spring.datasource.hikari.maximum-pool-size=3  # Reducir aún más
```

**Opción 2: Usar PgBouncer (Connection Pooler externo)**
- Instalar PgBouncer como intermediario
- Reduce carga en PostgreSQL
- Mejor manejo de conexiones

**Opción 3: Optimizar queries**
- Agregar índices a tablas frecuentes
- Revisar queries con EXPLAIN ANALYZE
- Evitar SELECT * en tablas grandes

## Cómo Prevenir Reinicios

Con esta configuración, el sistema debería recuperarse automáticamente:

1. **Conexiones timeout**: Se cierran automáticamente después de 30 segundos
2. **Pool se regenera**: Crea nuevas conexiones automáticamente
3. **Validación previa**: Verifica conexiones antes de usarlas
4. **Detección de fugas**: Alerta sobre conexiones no cerradas
5. **Startup tolerante**: Permite hasta 90 segundos para inicializar en hardware lento

## Aclaración: Startup Lento vs Timeouts de Operación

### ¿El startup de 72 segundos causará timeout?

**NO.** Los timeouts están separados:

| Momento | Timeout Aplicable | Valor | Propósito |
|---------|------------------|-------|-----------|
| **Inicio de aplicación** | `initialization-fail-timeout` | 90 seg | Crear pool inicial |
| **Operación normal** | `connection-timeout` | 30 seg | Obtener conexión del pool |
| **Conexión JDBC inicial** | `connectTimeout` (URL) | 10 seg | Establecer socket TCP |
| **Operaciones de lectura/escritura** | `socketTimeout` (URL) | 30 seg | Queries SQL |

**Durante el startup (72 segundos):**
- HikariCP tiene 90 segundos para crear el pool inicial
- Puede reintentar múltiples veces si falla
- Spring Boot espera pacientemente
- ✅ No habrá timeout

**Durante operación (crear platos, ventas, etc):**
- HikariCP tiene 30 segundos para dar una conexión del pool
- Si PostgreSQL está lento, el socket timeout es 30 segundos
- Si una transacción tarda mucho, timeout es 60 segundos
- ✅ Suficiente para operaciones normales, pero previene bloqueos indefinidos

## Verificación Post-Implementación

### 1. Verificar Startup en Producción

Usa el script `doc/verificar-startup.bat` o revisa los logs:

```bash
# Buscar en los logs el tiempo de inicio
grep "Started RestauranteLolaApplication" logs/spring.log
```

Deberías ver algo como:
```
Started RestauranteLolaApplication in 72.345 seconds (JVM running for 73.123)
```

**Si el startup tarda más de 90 segundos**, aumenta el timeout:
```properties
spring.datasource.hikari.initialization-fail-timeout=120000  # 120 segundos
```

### 2. Monitorear Pool Durante Operación

Después de reiniciar la aplicación:

1. ✅ Verificar que aparezca el log de inicio del pool
2. ✅ Realizar operaciones normales (crear platos, ventas)
3. ✅ Verificar que no aparezcan warnings de HikariCP
4. ✅ Probar operaciones intensivas (crear múltiples platos simultáneamente)
5. ✅ Revisar métricas del pool en logs cada 2 minutos

### 3. Prueba de Estrés (Opcional)

Para verificar que el pool maneja bien la carga:

```bash
# Hacer múltiples peticiones simultáneas
# (ajusta la URL según tu endpoint)
for i in {1..10}; do
  curl -X POST http://localhost:8080/api/menu/platos &
done
```

El pool debería manejar las peticiones sin timeouts.

## Logs a Monitorear

```
# Logs buenos (normales)
HikariPool-1 - Pool stats (total=5, active=2, idle=3, waiting=0)

# Logs de alerta (investigar)
HikariPool-1 - Connection is not available, request timed out
HikariPool-1 - Connection leak detection triggered

# Logs críticos (requieren acción)
SocketTimeoutException: Read timed out
PSQLException: Connection refused
```

## Fecha de Implementación

Implementado: [Fecha actual]
Responsable: Equipo de desarrollo
Estado: Pendiente de pruebas en producción
