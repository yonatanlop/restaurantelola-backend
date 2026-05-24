# Fix: Error en Cierre de Caja - cerrado_por

## Problema
Al intentar iniciar un cierre de caja, aparece el siguiente error:
```
ERROR: null value in column "cerrado_por" of relation "cierres_caja" violates not-null constraint
```

## Causa
La tabla `cierres_caja` tiene una columna `cerrado_por` definida como NOT NULL en el script original, pero:
- La entidad Java `CierreCaja` no tiene este campo
- El sistema ahora usa `usuario_id` en lugar de `cerrado_por`
- Al insertar un nuevo cierre, no se proporciona valor para `cerrado_por`

## Solución

### Opción 1: Ejecutar Script SQL (RECOMENDADO)
Ejecuta el script de migración que hace que la columna sea nullable:

```bash
psql -U postgres -d restaurante_lola -f database/scripts/10_fix_cerrado_por.sql
```

O desde pgAdmin/DBeaver, ejecuta:
```sql
-- Hacer que cerrado_por sea nullable
ALTER TABLE cierres_caja 
ALTER COLUMN cerrado_por DROP NOT NULL;

-- Actualizar registros existentes
UPDATE cierres_caja 
SET cerrado_por = usuario_id 
WHERE cerrado_por IS NULL AND usuario_id IS NOT NULL;
```

### Opción 2: Eliminar la Columna (Alternativa)
Si estás seguro de que no necesitas la columna `cerrado_por`:

```sql
ALTER TABLE cierres_caja DROP COLUMN IF EXISTS cerrado_por;
```

## Verificación
Después de aplicar el fix, verifica que funcione:

1. Inicia sesión como Dueño
2. Ve a Contabilidad > Cierre de Caja
3. Haz clic en "Iniciar Cierre"
4. El cierre debe crearse sin errores

## Archivos Relacionados
- `database/scripts/10_fix_cerrado_por.sql` - Script de migración
- `database/scripts/01_create_tables.sql` - Script original con el problema
- `src/main/java/com/tialola/caja/model/CierreCaja.java` - Entidad sin campo cerrado_por
- `src/main/java/com/tialola/caja/service/CierreCajaService.java` - Servicio que usa usuario_id

## Notas
- La columna `cerrado_por` era parte del diseño original
- Fue reemplazada por `usuario_id` en actualizaciones posteriores
- El sistema funciona correctamente con `usuario_id`
- Este fix es retrocompatible con datos existentes

## Fecha
30 de noviembre de 2025
