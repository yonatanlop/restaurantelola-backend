# HU-003: Control de Inventario con Consumo Automático

## ✅ Implementación Completa

### Descripción
Sistema completo de gestión de inventario con descuento automático de insumos al registrar ventas, alertas de bajo stock y trazabilidad de movimientos.

## Funcionalidades Implementadas

### Backend

#### Modelos
- **Insumo**: Ingredientes/productos del inventario
  - Nombre, descripción, unidad de medida
  - Cantidad actual y mínima
  - Precio unitario
  - Estado activo/inactivo
  - Método `isBajoStock()` para alertas

- **Receta**: Relación platos-insumos
  - Asocia platos con insumos necesarios
  - Cantidad necesaria por plato
  - Permite múltiples insumos por plato

- **MovimientoInventario**: Historial de movimientos
  - Tipo: ENTRADA, SALIDA, AJUSTE
  - Cantidad anterior y nueva
  - Motivo y referencia (venta, compra, ajuste)
  - Usuario que realizó el movimiento
  - Fecha y notas

#### Servicios

**InventarioService:**
- `obtenerTodos()` - Lista todos los insumos
- `obtenerActivos()` - Solo insumos activos
- `obtenerBajoStock()` - Insumos con stock <= mínimo
- `crearInsumo(dto)` - Crear nuevo insumo
- `actualizarInsumo(id, dto)` - Actualizar insumo
- `ajustarCantidad(id, cantidad, motivo, usuarioId)` - Ajustar stock manualmente
- `descontarPorVenta(platoId, cantidad, ventaId)` - **Descuento automático**

**RecetaService:**
- `obtenerPorPlato(platoId)` - Receta de un plato
- `agregarInsumoAPlato(platoId, dto)` - Agregar insumo a receta
- `eliminarInsumoDePlato(platoId, insumoId)` - Quitar insumo

#### Endpoints

**Insumos:**
- `GET /api/insumos` - Todos los insumos
- `GET /api/insumos/activos` - Solo activos
- `GET /api/insumos/bajo-stock` - Con bajo stock
- `GET /api/insumos/{id}` - Un insumo
- `POST /api/insumos` - Crear insumo
- `PUT /api/insumos/{id}` - Actualizar insumo
- `PATCH /api/insumos/{id}/ajustar` - Ajustar cantidad

**Recetas:**
- `GET /api/recetas/plato/{platoId}` - Receta de un plato
- `POST /api/recetas/plato/{platoId}` - Agregar insumo a plato
- `DELETE /api/recetas/plato/{platoId}/insumo/{insumoId}` - Quitar insumo

### Integración con Ventas

**Descuento Automático:**
Cuando se registra una venta en `VentaService.crearVenta()`:

1. Se guarda la venta
2. Por cada detalle de venta:
   - Se buscan los insumos del plato (receta)
   - Se calcula cantidad a descontar (cantidad_necesaria × cantidad_vendida)
   - Se descuenta del stock actual
   - Se registra movimiento tipo "SALIDA" con referencia a la venta

**Ejemplo:**
```
Venta: 2x Desayuno Completo
Receta Desayuno:
  - 2 Huevos
  - 0.5 lb Frijoles
  - 0.2 lb Queso

Descuento automático:
  - Huevos: 4 unidades (2×2)
  - Frijoles: 1 lb (2×0.5)
  - Queso: 0.4 lb (2×0.2)
```

### Alertas de Bajo Stock

**Detección:**
- Método `isBajoStock()` en modelo Insumo
- Query `findInsumosBajoStock()` en repositorio
- Endpoint `/api/insumos/bajo-stock`

**Criterio:**
```java
cantidadActual <= cantidadMinima
```

### Trazabilidad

**Movimientos Registrados:**
- **ENTRADA**: Compras, inventario inicial, ajustes positivos
- **SALIDA**: Ventas (automático), mermas, ajustes negativos
- **AJUSTE**: Correcciones manuales

**Información Guardada:**
- Cantidad anterior y nueva
- Motivo del movimiento
- Referencia (ID de venta, compra, etc.)
- Usuario que realizó el cambio
- Fecha y hora exacta

## Frontend (Táctil)

### Páginas
- **GestionInventarioPage**: Lista de insumos con filtros
- **ModalInsumo**: Crear/editar insumo
- **ModalAjustarStock**: Ajustar cantidad con motivo
- **TarjetaInsumo**: Tarjeta visual de insumo

### Características UI
- Botones grandes táctiles (min 50px)
- Alertas visuales para bajo stock
- Filtros: Todos / Bajo Stock
- Teclado numérico para cantidades
- Indicadores de stock con colores

### Flujos de Uso

**Crear Insumo:**
1. Click "➕ Nuevo Insumo"
2. Ingresar nombre y descripción
3. Seleccionar unidad de medida
4. Definir cantidad inicial
5. Establecer stock mínimo
6. Ingresar precio unitario
7. Guardar

**Ajustar Stock:**
1. Click en insumo
2. Click "Ajustar Stock"
3. Ingresar cantidad (+/-)
4. Especificar motivo
5. Confirmar

**Asociar Insumos a Plato:**
1. Ir a gestión de menú
2. Editar plato
3. Agregar insumos necesarios
4. Definir cantidades
5. Guardar receta

## Datos de Ejemplo

Los siguientes insumos vienen precargados:
- Huevos (100 unidades, mín: 20)
- Frijoles (50 lb, mín: 10)
- Arroz (80 lb, mín: 15)
- Café (30 lb, mín: 5)
- Leche (40 L, mín: 10)
- Carne de Res (25 lb, mín: 5)
- Tomate (20 lb, mín: 5)
- Cebolla (15 lb, mín: 5)

## Unidades de Medida Soportadas

- UNIDAD (piezas, unidades)
- LIBRA (lb)
- KILOGRAMO (kg)
- LITRO (L)
- GALON (gal)
- ONZA (oz)
- GRAMO (g)

## Validaciones

### Backend
- Cantidad no puede ser negativa (con excepción controlada)
- Stock mínimo debe ser >= 0
- Precio unitario debe ser >= 0
- Nombre obligatorio
- Unidad de medida obligatoria

### Frontend
- Campos obligatorios validados
- Cantidades numéricas
- Feedback visual de errores

## Seguridad
- Solo usuarios DUENO/ADMIN pueden gestionar inventario
- Todos los movimientos registran usuario
- Auditoría completa de cambios

## Reportes y Consultas

**Disponibles:**
- Lista de insumos bajo stock
- Historial de movimientos por insumo
- Movimientos por rango de fechas
- Valor total del inventario

## Mejoras Futuras

- [ ] Alertas automáticas por email/SMS
- [ ] Predicción de consumo
- [ ] Sugerencias de compra
- [ ] Códigos de barras
- [ ] Inventario físico vs sistema
- [ ] Costos por plato
- [ ] Análisis de rentabilidad
- [ ] Proveedores preferidos por insumo
- [ ] Historial de precios

## Integración con Otras HUs

### HU-001: POS
✅ Descuento automático al registrar venta

### HU-002: Gestión de Menú
✅ Asociación de insumos a platos (recetas)

### HU-004: Compras (Preparado)
- Estructura lista para registrar compras
- Movimientos tipo ENTRADA preparados

### HU-007: Contabilidad (Preparado)
- Valor del inventario calculable
- Costo de ventas por insumos

## Testing

**Probar Descuento Automático:**
1. Crear insumo "Huevos" con 100 unidades
2. Crear plato "Desayuno" 
3. Asociar 2 huevos al desayuno (receta)
4. Registrar venta de 3 desayunos
5. Verificar que quedan 94 huevos (100 - 6)
6. Revisar movimientos de inventario

**Probar Alertas:**
1. Crear insumo con cantidad actual = 5, mínima = 10
2. Verificar que aparece en "Bajo Stock"
3. Ajustar cantidad a 15
4. Verificar que ya no aparece en alertas

## Notas Técnicas

- Transacciones para garantizar consistencia
- Permite stock negativo con alerta (no bloquea venta)
- Movimientos inmutables (no se pueden editar)
- Soft delete en insumos (activo/inactivo)
- Índices en queries frecuentes
