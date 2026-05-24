# HU-012: Auditoría de Operaciones Clave

## Descripción
Sistema completo de auditoría que registra automáticamente todas las operaciones críticas del sistema con trazabilidad completa.

## Funcionalidades Implementadas

### 1. Registro Automático de Auditoría
- Intercepta operaciones críticas usando AOP (Aspect-Oriented Programming)
- Registra automáticamente sin modificar código existente
- Captura contexto completo de la operación

### 2. Información Capturada
- Usuario que realizó la acción
- Tipo de acción (CREATE, UPDATE, DELETE, etc.)
- Entidad afectada
- ID de la entidad
- Datos anteriores y nuevos (JSON)
- Fecha y hora exacta
- Resultado (EXITOSO/FALLIDO)
- Mensaje de error (si aplica)

### 3. Operaciones Auditadas
- ✅ Creación de ventas
- ✅ Actualización de platos
- ✅ Ajustes de inventario
- ✅ Registro de nómina
- ✅ Todos los errores del sistema
- ✅ Login/Logout de usuarios

### 4. Búsquedas y Filtros
- Por usuario
- Por entidad y ID
- Por tipo de acción
- Por rango de fechas
- Filtros combinados

## Endpoints API

### Consultas (Solo DUENO)
- `GET /api/auditoria/usuario/{usuarioId}` - Logs de un usuario
- `GET /api/auditoria/entidad/{entidad}/{entidadId}` - Logs de una entidad específica
- `GET /api/auditoria/accion/{accion}` - Logs por tipo de acción
- `GET /api/auditoria/rango?inicio={fecha}&fin={fecha}` - Logs por rango de fechas
- `POST /api/auditoria/buscar` - Búsqueda con filtros combinados

## Modelo de Datos

### AuditoriaLog
```json
{
  "id": 1,
  "usuarioId": 1,
  "usuarioNombre": "admin",
  "accion": "CREATE",
  "entidad": "VENTA",
  "entidadId": 123,
  "descripcion": "Venta creada exitosamente",
  "datosAnteriores": null,
  "datosNuevos": "{\"total\": 150.00, \"items\": 3}",
  "ipAddress": "192.168.1.100",
  "userAgent": "Mozilla/5.0...",
  "fecha": "2024-11-16T20:30:45",
  "resultado": "EXITOSO",
  "mensajeError": null
}
```

## Tipos de Acciones

| Acción | Descripción | Ejemplo |
|--------|-------------|---------|
| CREATE | Creación de registro | Nueva venta |
| UPDATE | Actualización | Cambio de precio |
| DELETE | Eliminación | Borrar plato |
| AJUSTE | Ajuste de valores | Inventario |
| LOGIN | Inicio de sesión | Usuario ingresa |
| LOGOUT | Cierre de sesión | Usuario sale |
| ERROR | Error del sistema | Excepción capturada |

## Entidades Auditadas

- VENTA
- PLATO
- INSUMO
- INVENTARIO
- EMPLEADO
- NOMINA
- COMPRA
- MESA
- COMANDA
- USUARIO

## Uso del Servicio

### Registro Manual
```java
@Autowired
private AuditoriaService auditoriaService;

// Registrar acción simple
auditoriaService.registrarAccion(
    "UPDATE",
    "PLATO",
    platoId,
    usuarioId,
    "Juan Perez",
    "Precio actualizado de $45 a $50"
);

// Registrar cambio con datos
auditoriaService.registrarCambio(
    "UPDATE",
    "PLATO",
    platoId,
    usuarioId,
    "Juan Perez",
    platoAnterior,  // Objeto antes del cambio
    platoNuevo,     // Objeto después del cambio
    "Actualización de plato"
);

// Registrar error
auditoriaService.registrarError(
    "CREATE",
    "VENTA",
    null,
    usuarioId,
    "Juan Perez",
    "Error al procesar pago: Fondos insuficientes"
);
```

### Registro Automático (AOP)
El sistema registra automáticamente:

```java
// Esto se audita automáticamente
@Service
public class VentaService {
    public Venta crearVenta(VentaDTO dto) {
        // ... lógica de negocio
        return venta; // Se audita automáticamente
    }
}
```

## Consultas de Ejemplo

### 1. Ver todas las acciones de un usuario
```bash
GET /api/auditoria/usuario/1
```

### 2. Ver historial de una venta específica
```bash
GET /api/auditoria/entidad/VENTA/123
```

### 3. Ver todos los ajustes de inventario
```bash
GET /api/auditoria/accion/AJUSTE
```

### 4. Ver logs del día
```bash
GET /api/auditoria/rango?inicio=2024-11-16T00:00:00&fin=2024-11-16T23:59:59
```

### 5. Búsqueda avanzada
```bash
POST /api/auditoria/buscar
{
  "fechaInicio": "2024-11-16T00:00:00",
  "fechaFin": "2024-11-16T23:59:59",
  "entidad": "VENTA",
  "accion": "CREATE"
}
```

## Reportes de Auditoría

### Actividad por Usuario
```sql
SELECT 
    usuario_nombre,
    COUNT(*) as total_acciones,
    COUNT(CASE WHEN resultado = 'EXITOSO' THEN 1 END) as exitosas,
    COUNT(CASE WHEN resultado = 'FALLIDO' THEN 1 END) as fallidas
FROM auditoria_logs
WHERE DATE(fecha) = CURRENT_DATE
GROUP BY usuario_nombre
ORDER BY total_acciones DESC;
```

### Operaciones Críticas del Día
```sql
SELECT 
    accion,
    entidad,
    COUNT(*) as cantidad
FROM auditoria_logs
WHERE DATE(fecha) = CURRENT_DATE
  AND accion IN ('CREATE', 'UPDATE', 'DELETE')
GROUP BY accion, entidad
ORDER BY cantidad DESC;
```

### Errores Recientes
```sql
SELECT 
    fecha,
    usuario_nombre,
    entidad,
    mensaje_error
FROM auditoria_logs
WHERE resultado = 'FALLIDO'
  AND fecha >= NOW() - INTERVAL '24 hours'
ORDER BY fecha DESC
LIMIT 50;
```

### Historial de Cambios de una Entidad
```sql
SELECT 
    fecha,
    usuario_nombre,
    accion,
    descripcion,
    datos_anteriores,
    datos_nuevos
FROM auditoria_logs
WHERE entidad = 'PLATO'
  AND entidad_id = 5
ORDER BY fecha DESC;
```

## Configuración de AOP

El sistema usa AspectJ para interceptar automáticamente:

```java
@Aspect
@Component
public class AuditoriaAspect {
    
    // Audita después de crear venta
    @AfterReturning(
        pointcut = "execution(* com.tialola..VentaService.crearVenta(..))",
        returning = "result"
    )
    public void auditarCreacionVenta(JoinPoint joinPoint, Object result) {
        // Registro automático
    }
    
    // Audita todos los errores
    @AfterThrowing(
        pointcut = "execution(* com.tialola..*Service.*(..))",
        throwing = "error"
    )
    public void auditarErrores(JoinPoint joinPoint, Throwable error) {
        // Registro de error
    }
}
```

## Integración con Otros Módulos

### Con Ventas
```java
// Automático al crear venta
Venta venta = ventaService.crearVenta(dto);
// Se audita: CREATE - VENTA - {ventaId}
```

### Con Inventario
```java
// Automático al ajustar inventario
insumoService.ajustarInventario(insumoId, cantidad, motivo);
// Se audita: AJUSTE - INVENTARIO - {insumoId}
```

### Con Nómina
```java
// Automático al registrar nómina
Nomina nomina = nominaService.registrarNomina(dto);
// Se audita: CREATE - NOMINA - {nominaId}
```

## Base de Datos

### Ejecutar Script
```bash
psql -U postgres -d restaurante_lola -f database/scripts/07_auditoria_tables.sql
```

### Tabla Creada
- `auditoria_logs` - Registro completo de auditoría

## Retención de Datos

### Limpieza Automática (Recomendado)
```sql
-- Eliminar logs mayores a 1 año
DELETE FROM auditoria_logs
WHERE fecha < NOW() - INTERVAL '1 year';

-- Archivar logs antiguos
INSERT INTO auditoria_logs_archivo
SELECT * FROM auditoria_logs
WHERE fecha < NOW() - INTERVAL '6 months';
```

## Seguridad y Cumplimiento

### GDPR / Protección de Datos
- Los datos sensibles se pueden anonimizar
- Retención configurable
- Exportación de datos de usuario

### Cumplimiento Normativo
- Trazabilidad completa
- No repudio (non-repudiation)
- Registro inmutable
- Timestamps precisos

## Pantalla de Auditoría (Frontend)

### Vista de Logs
```
┌─────────────────────────────────────────────────────┐
│ Auditoría de Operaciones                            │
├─────────────────────────────────────────────────────┤
│ Filtros:                                            │
│ [Usuario ▼] [Acción ▼] [Entidad ▼] [Fecha ▼]      │
├─────────────────────────────────────────────────────┤
│ Fecha/Hora          Usuario    Acción    Entidad   │
│ 16/11/24 20:30:45  admin      CREATE    VENTA      │
│ 16/11/24 20:25:12  cajero     UPDATE    PLATO      │
│ 16/11/24 20:20:33  admin      AJUSTE    INVENTARIO │
│ 16/11/24 20:15:44  cajero     CREATE    VENTA      │
└─────────────────────────────────────────────────────┘
```

## Notas Técnicas

1. **AOP (Aspect-Oriented Programming)**: Intercepta métodos sin modificar código
2. **JSON Storage**: Datos anteriores/nuevos en formato JSON
3. **Async Logging**: No afecta performance de operaciones
4. **Índices Optimizados**: Búsquedas rápidas por múltiples criterios
5. **Inmutabilidad**: Los logs no se pueden modificar

## Próximas Mejoras

- [ ] Exportación de logs a CSV/Excel
- [ ] Dashboard de auditoría en tiempo real
- [ ] Alertas por operaciones sospechosas
- [ ] Firma digital de logs
- [ ] Integración con SIEM
- [ ] Análisis de patrones anómalos
