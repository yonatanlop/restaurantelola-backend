# 🎯 Instrucciones para Probar el Módulo de Auditoría

## ✅ Pasos para Activar el Módulo

### 1. Ejecutar Script SQL (Si no lo has hecho)

```bash
psql -U postgres -d restaurante_lola -f database/scripts/07_auditoria_tables.sql
```

Esto creará la tabla `auditoria_logs` en la base de datos.

### 2. Instalar Dependencias del Frontend

```bash
cd tialola-frontend
npm install
```

Esto instalará `date-fns` que se agregó para el formato de fechas.

### 3. Iniciar el Sistema

Usa cualquiera de las opciones:

**Opción A: Script BAT**
```bash
# Doble clic en:
iniciar-restaurante-lola.bat
```

**Opción B: Manual**
```bash
# Terminal 1 - Backend
mvn spring-boot:run

# Terminal 2 - Frontend
cd tialola-frontend
npm run dev
```

### 4. Acceder al Sistema

1. Abrir navegador: `http://localhost:3000`
2. Login como **DUENO**:
   - Usuario: `dueno`
   - Contraseña: `dueno123`

### 5. Ir al Módulo de Auditoría

1. Clic en **"Menú Avanzado"** (botón en la parte superior)
2. Clic en **"Auditoría"** en el menú lateral

URL directa: `http://localhost:3000/dueno/avanzado/auditoria`

---

## 🧪 Cómo Probar el Módulo

### Paso 1: Generar Datos de Auditoría

Para que aparezcan logs, necesitas realizar operaciones en el sistema:

#### A. Crear una Venta
1. Ir a **POS / Ventas**
2. Agregar productos
3. Completar venta
4. ✅ Se registrará: `CREATE - VENTA`

#### B. Actualizar un Plato
1. Ir a **Menú Avanzado → Menú**
2. Editar un plato
3. Guardar cambios
4. ✅ Se registrará: `UPDATE - PLATO`

#### C. Ajustar Inventario
1. Ir a **Menú Avanzado → Inventario**
2. Hacer un ajuste de inventario
3. Guardar
4. ✅ Se registrará: `AJUSTE - INVENTARIO`

#### D. Registrar Nómina
1. Ir a **Menú Avanzado → Nómina**
2. Registrar nómina
3. ✅ Se registrará: `CREATE - NOMINA`

### Paso 2: Ver los Logs en Auditoría

1. Ir a **Auditoría**
2. Deberías ver los logs de las operaciones que realizaste
3. Por defecto muestra las **últimas 24 horas**

---

## 🔍 Funcionalidades a Probar

### 1. Visualización de Logs

✅ **Verificar:**
- Tabla muestra todos los logs
- Fechas en formato español
- Badges de colores por tipo de acción
- Usuario que realizó la acción
- Descripción de la operación

### 2. Filtros de Búsqueda

#### A. Filtrar por Fecha
1. Clic en **"Fecha Inicio"** y **"Fecha Fin"**
2. Seleccionar rango
3. Clic en **"🔍 Buscar"**
4. ✅ Debe mostrar solo logs en ese rango

#### B. Filtrar por Acción
1. Seleccionar acción en dropdown (ej: CREATE)
2. Clic en **"🔍 Buscar"**
3. ✅ Debe mostrar solo logs de esa acción

#### C. Filtrar por Entidad
1. Seleccionar entidad (ej: VENTA)
2. Clic en **"🔍 Buscar"**
3. ✅ Debe mostrar solo logs de esa entidad

#### D. Filtrar por ID de Entidad
1. Ingresar ID específico (ej: 1)
2. Seleccionar entidad
3. Clic en **"🔍 Buscar"**
4. ✅ Debe mostrar logs de ese registro específico

#### E. Limpiar Filtros
1. Clic en **"🗑️ Limpiar"**
2. ✅ Debe volver a mostrar últimas 24 horas

### 3. Ver Detalles de un Log

1. Clic en **"👁️ Ver"** en cualquier fila
2. ✅ Debe abrir modal con:
   - Información general
   - Descripción
   - Datos anteriores (JSON)
   - Datos nuevos (JSON)
   - Información técnica
   - Mensaje de error (si aplica)

### 4. Exportar a CSV

1. Clic en **"📥 Exportar CSV"**
2. ✅ Debe descargar archivo CSV con los logs visibles
3. Abrir el archivo en Excel
4. ✅ Verificar que contenga todos los datos

### 5. Actualizar Logs

1. Realizar una nueva operación en el sistema
2. Volver a Auditoría
3. Clic en **"🔄 Actualizar"**
4. ✅ Debe aparecer el nuevo log

### 6. Mostrar/Ocultar Filtros

1. Clic en **"🔼 Ocultar Filtros"**
2. ✅ Los filtros deben ocultarse
3. Clic en **"🔽 Mostrar Filtros"**
4. ✅ Los filtros deben aparecer

---

## 📊 Casos de Prueba Específicos

### Caso 1: Auditar Creación de Venta

**Pasos:**
1. Crear una venta en el POS
2. Ir a Auditoría
3. Buscar por acción "CREATE" y entidad "VENTA"

**Resultado Esperado:**
- Aparece log con acción CREATE
- Entidad: VENTA
- ID de la venta creada
- Usuario que la creó
- Datos nuevos con información de la venta

### Caso 2: Auditar Actualización de Plato

**Pasos:**
1. Editar un plato (cambiar precio)
2. Ir a Auditoría
3. Buscar por acción "UPDATE" y entidad "PLATO"

**Resultado Esperado:**
- Aparece log con acción UPDATE
- Datos anteriores: precio antiguo
- Datos nuevos: precio nuevo
- Descripción del cambio

### Caso 3: Auditar Ajuste de Inventario

**Pasos:**
1. Hacer ajuste de inventario
2. Ir a Auditoría
3. Buscar por acción "AJUSTE"

**Resultado Esperado:**
- Aparece log con acción AJUSTE
- Entidad: INVENTARIO
- ID del insumo ajustado
- Descripción del ajuste

### Caso 4: Ver Historial de un Registro

**Pasos:**
1. Buscar por entidad "PLATO" e ID específico (ej: 1)
2. Ver todos los logs

**Resultado Esperado:**
- Muestra todos los cambios de ese plato
- Ordenados por fecha (más reciente primero)
- Se puede ver evolución del registro

---

## 🎨 Elementos Visuales a Verificar

### Badges de Acción

| Acción | Color | Icono |
|--------|-------|-------|
| CREATE | Verde | ➕ |
| UPDATE | Azul | ✏️ |
| DELETE | Rojo | 🗑️ |
| AJUSTE | Amarillo | ⚙️ |
| LOGIN | Azul | 🔓 |
| LOGOUT | Gris | 🔒 |
| ERROR | Rojo | ❌ |

### Badges de Resultado

| Resultado | Color | Icono |
|-----------|-------|-------|
| EXITOSO | Verde | ✓ |
| FALLIDO | Rojo | ✗ |

### Estados de la Tabla

- **Hover:** Fila se resalta al pasar el mouse
- **Error:** Filas con resultado FALLIDO tienen fondo rojo claro
- **Vacía:** Mensaje "📋 No se encontraron registros"

---

## 🐛 Troubleshooting

### Problema: No aparecen logs

**Solución:**
1. Verificar que el script SQL se ejecutó:
   ```sql
   SELECT * FROM auditoria_logs;
   ```
2. Realizar operaciones en el sistema
3. Verificar que el backend esté corriendo
4. Ver consola del navegador (F12)

### Problema: Error al cargar

**Solución:**
1. Verificar que estás logueado como DUENO
2. Verificar URL del backend en `auditoria.service.ts`
3. Ver consola del navegador para errores
4. Verificar que el endpoint responda:
   ```bash
   curl http://localhost:8080/api/auditoria/rango?inicio=2024-01-01T00:00:00&fin=2024-12-31T23:59:59
   ```

### Problema: Fechas incorrectas

**Solución:**
1. Verificar zona horaria del servidor
2. Verificar que `date-fns` esté instalado:
   ```bash
   npm list date-fns
   ```

### Problema: No se exporta CSV

**Solución:**
1. Verificar que haya logs en la tabla
2. Ver consola del navegador
3. Verificar permisos de descarga del navegador

---

## 📝 Checklist de Pruebas

### Funcionalidades Básicas
- [ ] La página carga correctamente
- [ ] Muestra logs de las últimas 24 horas
- [ ] Tabla muestra todos los campos
- [ ] Fechas en formato español
- [ ] Badges con colores correctos

### Filtros
- [ ] Filtro por fecha funciona
- [ ] Filtro por acción funciona
- [ ] Filtro por entidad funciona
- [ ] Filtro por ID funciona
- [ ] Filtro por usuario funciona
- [ ] Limpiar filtros funciona
- [ ] Combinación de filtros funciona

### Detalles
- [ ] Modal de detalles abre
- [ ] Muestra información completa
- [ ] JSON formateado correctamente
- [ ] Botón cerrar funciona
- [ ] Click fuera del modal cierra

### Exportación
- [ ] Exportar CSV funciona
- [ ] Archivo contiene datos correctos
- [ ] Formato CSV es válido

### Responsive
- [ ] Funciona en pantalla grande
- [ ] Funciona en tablet
- [ ] Funciona en móvil
- [ ] Tabla es scrolleable

### Performance
- [ ] Carga rápida con pocos logs
- [ ] Carga aceptable con muchos logs
- [ ] Filtros responden rápido
- [ ] No hay memory leaks

---

## 🎯 Resultado Esperado Final

Al completar todas las pruebas, deberías tener:

✅ **Módulo de Auditoría Funcional:**
- Visualización completa de logs
- Filtros funcionando
- Detalles completos
- Exportación a CSV
- Interfaz responsive
- Sin errores en consola

✅ **Datos de Auditoría:**
- Todas las operaciones registradas
- Información completa por log
- Trazabilidad total
- Historial de cambios

✅ **Experiencia de Usuario:**
- Interfaz intuitiva
- Navegación fluida
- Información clara
- Acciones rápidas

---

## 📞 Soporte

Si encuentras problemas:

1. **Revisar logs del backend:**
   ```bash
   # Ver logs en la consola donde corre el backend
   ```

2. **Revisar consola del navegador:**
   ```
   F12 → Console
   ```

3. **Verificar base de datos:**
   ```sql
   SELECT COUNT(*) FROM auditoria_logs;
   SELECT * FROM auditoria_logs ORDER BY fecha DESC LIMIT 10;
   ```

4. **Consultar documentación:**
   - `tialola-frontend/src/modules/auditoria/README.md`
   - `src/main/java/com/tialola/auditoria/README_HU012.md`

---

## 🎉 ¡Listo!

Ahora tienes un módulo de auditoría completamente funcional que te permite:
- Ver todas las operaciones del sistema
- Filtrar y buscar logs específicos
- Ver detalles completos de cada operación
- Exportar datos para análisis
- Mantener trazabilidad completa

**¡Disfruta del módulo de auditoría!** 📋✨
