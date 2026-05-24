# Scripts de Base de Datos - Restaurante Doña Lola

## Orden de Ejecución

Ejecutar los scripts en el siguiente orden:

1. **00_create_database.sql** - Crea la base de datos contabilidadRestaurante
2. **01_create_tables.sql** - Crea todas las tablas del sistema
3. **02_insert_initial_data.sql** - Inserta datos iniciales (usuarios, categorías, ejemplos)
4. **05_impresion_tables.sql** - Estructura adicional para el módulo de impresión (HU-010)
5. **06_mesas_tables.sql** - Estructura inicial para mesas y comandas (HU-011)
6. **07_auditoria_tables.sql** - Tablas dedicadas a auditoría avanzada (HU-012)
7. **08_cierre_propinas.sql** - Ajustes para cierres de caja (HU-013) y propinas (HU-014)

## Configuración de PostgreSQL

### Crear la base de datos

```sql
CREATE DATABASE "contabilidadRestaurante"
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'Spanish_Honduras.1252'
    LC_CTYPE = 'Spanish_Honduras.1252'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;
```

### Ejecutar scripts

```bash
# Paso 1: Crear la base de datos
psql -U postgres -f 00_create_database.sql

# Paso 2: Crear las tablas base
psql -U postgres -d contabilidadRestaurante -f 01_create_tables.sql

# Paso 3: Insertar datos iniciales
psql -U postgres -d contabilidadRestaurante -f 02_insert_initial_data.sql

# Paso 4: Ejecutar scripts incrementales según necesidad
psql -U postgres -d contabilidadRestaurante -f 05_impresion_tables.sql
psql -U postgres -d contabilidadRestaurante -f 06_mesas_tables.sql
psql -U postgres -d contabilidadRestaurante -f 07_auditoria_tables.sql
psql -U postgres -d contabilidadRestaurante -f 08_cierre_propinas.sql
```

## Usuarios Iniciales

| Usuario | Contraseña | Rol |
|---------|-----------|-----|
| admin   | admin123  | ADMIN |
| dueno   | dueno123  | DUENO |
| cajero  | cajero123 | CAJERO |

**IMPORTANTE:** Cambiar estas contraseñas en producción.

## Estructura de Tablas

### Módulo de Autenticación
- `usuarios` - Usuarios del sistema
- `sesiones` - Registro de sesiones

### Módulo de Ventas
- `ventas` - Encabezado de ventas
- `venta_detalles` - Detalle de productos vendidos

### Módulo de Menú/Productos
- `categorias` - Categorías de platos
- `platos` - Platos/productos del menú

### Módulo de Inventario
- `insumos` - Insumos/ingredientes
- `recetas` - Relación platos-insumos
- `movimientos_inventario` - Historial de movimientos

### Módulo de Compras
- `proveedores` - Proveedores
- `compras` - Encabezado de compras
- `compra_detalles` - Detalle de compras

### Módulo de Nómina
- `empleados` - Empleados del restaurante
- `nomina_diaria` - Registro de pagos diarios

### Módulo de Contabilidad
- `cierres_caja` - Cierres de caja diarios
- `detalle_arqueo` - Denominaciones contadas por cierre
- `propinas_venta` - Registro preparado para propinas por venta

### Módulo de Auditoría
- `auditoria` - Registro de operaciones importantes

## Notas

- Todas las tablas tienen timestamps de creación
- Las contraseñas están hasheadas con BCrypt
- Los índices están optimizados para consultas frecuentes
- Las relaciones tienen integridad referencial con CASCADE donde corresponde
