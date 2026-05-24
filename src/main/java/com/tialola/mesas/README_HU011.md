# HU-011: Gestión de Mesas y Comandas

## Descripción
Sistema completo de gestión de mesas con control de ocupación, comandas por mesa, transferencias entre mesas y seguimiento de estados.

## Funcionalidades Implementadas

### 1. Gestión de Mesas
- CRUD completo de mesas
- Estados: LIBRE, OCUPADA, RESERVADA, LIMPIEZA
- Ubicaciones: SALON, TERRAZA, VIP
- Control de capacidad
- Tiempo de ocupación en tiempo real
- Total de cuenta actual

### 2. Sistema de Comandas
- Crear comandas por mesa
- Estados: PENDIENTE, EN_PREPARACION, LISTA, SERVIDA, CANCELADA
- Seguimiento de tiempos (creación, preparación, lista, servida)
- Notas especiales por plato
- Cancelación con motivo

### 3. Transferencia de Mesas
- Mover cuenta entre mesas
- Historial de transferencias
- Validación de disponibilidad
- Registro de motivo

### 4. División de Cuentas
- Comandas independientes por mesa
- Múltiples comandas activas
- Control por item

## Endpoints API

### Mesas
- `GET /api/mesas` - Listar todas las mesas
- `GET /api/mesas/estado/{estado}` - Mesas por estado
- `GET /api/mesas/ubicacion/{ubicacion}` - Mesas por ubicación
- `POST /api/mesas` - Crear mesa (DUENO)
- `PUT /api/mesas/{id}` - Actualizar mesa (DUENO)
- `POST /api/mesas/{mesaId}/ocupar?ventaId={id}` - Ocupar mesa
- `POST /api/mesas/{mesaId}/liberar` - Liberar mesa
- `POST /api/mesas/transferir` - Transferir entre mesas
- `PATCH /api/mesas/{mesaId}/estado?estado={estado}` - Cambiar estado

### Comandas
- `POST /api/comandas` - Crear comanda
- `GET /api/comandas/mesa/{mesaId}` - Comandas de una mesa
- `GET /api/comandas/pendientes` - Comandas pendientes
- `GET /api/comandas/estado/{estado}` - Comandas por estado
- `PATCH /api/comandas/{id}/estado?estado={estado}` - Cambiar estado
- `PATCH /api/comandas/{id}/item/{detalleId}/estado?estado={estado}` - Estado de item
- `POST /api/comandas/{id}/cancelar?motivo={motivo}` - Cancelar comanda

## Modelos de Datos

### Mesa
```json
{
  "id": 1,
  "numero": "M01",
  "capacidad": 4,
  "ubicacion": "SALON",
  "estado": "OCUPADA",
  "ventaActualId": 123,
  "horaOcupacion": "2024-11-16T20:00:00",
  "totalCuenta": 150.00,
  "tiempoOcupacion": 45
}
```

### Comanda
```json
{
  "mesaId": 1,
  "numeroMesa": "M01",
  "usuarioId": 1,
  "notas": "Cliente prefiere sin sal",
  "items": [
    {
      "platoId": 5,
      "cantidad": 2,
      "precioUnitario": 45.00,
      "notas": "Sin cebolla"
    }
  ]
}
```

### Transferencia
```json
{
  "mesaOrigenId": 1,
  "mesaDestinoId": 5,
  "ventaId": 123,
  "usuarioId": 1,
  "motivo": "Cliente solicitó mesa más grande"
}
```

## Flujo de Trabajo

### 1. Ocupar Mesa
```bash
# Cliente llega y se asigna mesa
POST /api/mesas/1/ocupar?ventaId=123
```

### 2. Crear Comanda
```bash
POST /api/comandas
{
  "mesaId": 1,
  "usuarioId": 1,
  "items": [
    {"platoId": 5, "cantidad": 2, "precioUnitario": 45.00}
  ]
}
```

### 3. Cocina Prepara
```bash
# Cambiar estado a EN_PREPARACION
PATCH /api/comandas/1/estado?estado=EN_PREPARACION

# Cuando está lista
PATCH /api/comandas/1/estado?estado=LISTA
```

### 4. Servir
```bash
PATCH /api/comandas/1/estado?estado=SERVIDA
```

### 5. Transferir Mesa (si necesario)
```bash
POST /api/mesas/transferir
{
  "mesaOrigenId": 1,
  "mesaDestinoId": 5,
  "ventaId": 123,
  "usuarioId": 1,
  "motivo": "Mesa más grande"
}
```

### 6. Liberar Mesa
```bash
# Después de pagar
POST /api/mesas/1/liberar
```

## Integración con Otros Módulos

### Con Ventas (HU-001)
```java
// Al crear venta, ocupar mesa
ventaService.crearVenta(ventaDTO);
mesaService.ocuparMesa(mesaId, venta.getId());

// Al completar venta, liberar mesa
ventaService.completarVenta(ventaId);
mesaService.liberarMesa(mesaId);
```

### Con Impresión (HU-010)
```java
// Al crear comanda, imprimir en cocina
Comanda comanda = comandaService.crearComanda(dto);
impresionService.encolarComanda(comanda.getVentaId());
```

## Estados de Mesa

| Estado | Descripción | Puede Ocuparse |
|--------|-------------|----------------|
| LIBRE | Disponible | ✅ Sí |
| OCUPADA | Con clientes | ❌ No |
| RESERVADA | Reservación activa | ❌ No |
| LIMPIEZA | En proceso de limpieza | ❌ No |

## Estados de Comanda

| Estado | Descripción | Siguiente Estado |
|--------|-------------|------------------|
| PENDIENTE | Recién creada | EN_PREPARACION |
| EN_PREPARACION | Cocina trabajando | LISTA |
| LISTA | Preparada | SERVIDA |
| SERVIDA | Entregada al cliente | - |
| CANCELADA | Cancelada | - |

## Pantalla de Mesas (Frontend)

### Vista de Plano
```
┌─────────┐  ┌─────────┐  ┌─────────┐
│  M01    │  │  M02    │  │  M03    │
│  🟢     │  │  🔴     │  │  🟢     │
│  4 pers │  │  4 pers │  │  2 pers │
└─────────┘  └─────────┘  └─────────┘

🟢 LIBRE    🔴 OCUPADA    🟡 RESERVADA    ⚪ LIMPIEZA
```

### Información de Mesa Ocupada
```
Mesa: M02
Estado: OCUPADA 🔴
Tiempo: 45 min
Cuenta: $150.00
Comandas: 2 activas

[Ver Comandas] [Transferir] [Liberar]
```

## Base de Datos

### Ejecutar Script
```bash
psql -U postgres -d restaurante_lola -f database/scripts/06_mesas_tables.sql
```

### Tablas Creadas
- `mesas` - Información de mesas
- `comandas` - Comandas por mesa
- `comanda_detalles` - Items de cada comanda
- `transferencias_mesa` - Historial de transferencias

## Reportes Disponibles

### Ocupación de Mesas
```sql
SELECT 
    ubicacion,
    COUNT(*) as total_mesas,
    SUM(CASE WHEN estado = 'OCUPADA' THEN 1 ELSE 0 END) as ocupadas,
    ROUND(SUM(CASE WHEN estado = 'OCUPADA' THEN 1 ELSE 0 END)::numeric / COUNT(*) * 100, 2) as porcentaje_ocupacion
FROM mesas
WHERE activa = true
GROUP BY ubicacion;
```

### Tiempo Promedio de Ocupación
```sql
SELECT 
    AVG(EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - hora_ocupacion))/60) as minutos_promedio
FROM mesas
WHERE estado = 'OCUPADA';
```

### Comandas por Estado
```sql
SELECT 
    estado,
    COUNT(*) as cantidad,
    AVG(EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - fecha_creacion))/60) as tiempo_promedio_minutos
FROM comandas
WHERE DATE(fecha_creacion) = CURRENT_DATE
GROUP BY estado;
```

## Notas Técnicas

1. **Tiempo Real**: Los tiempos de ocupación se calculan dinámicamente
2. **Validaciones**: No se puede ocupar una mesa ya ocupada
3. **Transferencias**: Se registran para auditoría
4. **Comandas Múltiples**: Una mesa puede tener varias comandas activas
5. **Estados Independientes**: Mesa y comandas tienen estados independientes

## Próximas Mejoras

- [ ] Reservaciones con fecha/hora
- [ ] Notificaciones cuando comanda está lista
- [ ] Mapa visual interactivo de mesas
- [ ] División de cuenta entre comensales
- [ ] Historial de rotación de mesas
- [ ] Estadísticas de ocupación por horario
