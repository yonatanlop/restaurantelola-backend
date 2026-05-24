# HU-005: Gestión de Empleados

## ✅ Implementación Completa

### Descripción
Sistema completo de gestión de empleados con datos básicos, salarios diarios y validaciones. Base fundamental para el módulo de nómina.

## Funcionalidades Implementadas

### Backend

#### Modelo Empleado
- **Datos personales**: Nombre, apellido, documento
- **Contacto**: Teléfono, dirección
- **Laborales**: Puesto, salario diario, fecha de ingreso
- **Estado**: Activo/inactivo (soft delete)
- **Método**: `getNombreCompleto()` para facilitar uso

#### Validaciones
- ✅ Documento único (no duplicados)
- ✅ Nombre y apellido obligatorios
- ✅ Salario diario obligatorio y > 0
- ✅ Fecha de ingreso obligatoria
- ✅ Validación al crear y actualizar

#### Endpoints

**GET /api/empleados**
- Obtiene todos los empleados
- Incluye activos e inactivos

**GET /api/empleados/activos**
- Solo empleados activos
- Para uso en nómina

**GET /api/empleados/{id}**
- Un empleado específico

**GET /api/empleados/documento/{documento}**
- Buscar por documento de identidad

**POST /api/empleados**
- Crear nuevo empleado
- Valida documento único
- Request:
```json
{
  "nombre": "María",
  "apellido": "González",
  "documento": "001-123456-7",
  "telefono": "7777-8888",
  "direccion": "Colonia Centro",
  "puesto": "Cocinera",
  "salarioDiario": 150.00,
  "fechaIngreso": "2024-01-15"
}
```

**PUT /api/empleados/{id}**
- Actualizar empleado existente
- Valida documento si cambió

**PATCH /api/empleados/{id}/estado**
- Activar/desactivar empleado
- Request: `{ "activo": false }`

**DELETE /api/empleados/{id}**
- Eliminar empleado (usar con precaución)
- Recomendado: usar desactivar en su lugar

### Datos de Ejemplo

3 empleados precargados:
```
1. María González
   - Documento: 001-123456-7
   - Puesto: Cocinera
   - Salario: L150.00/día
   - Ingreso: 2024-01-15

2. Juan Pérez
   - Documento: 001-234567-8
   - Puesto: Ayudante de Cocina
   - Salario: L100.00/día
   - Ingreso: 2024-02-01

3. Ana Martínez
   - Documento: 001-345678-9
   - Puesto: Mesera
   - Salario: L120.00/día
   - Ingreso: 2024-01-20
```

## Frontend (Táctil)

### Páginas
- **GestionEmpleadosPage**: Lista de empleados
- **ModalEmpleado**: Crear/editar empleado
- **TarjetaEmpleado**: Tarjeta visual de empleado

### Características UI
- Botones grandes táctiles (min 50px)
- Formulario con campos grandes
- Validación en tiempo real
- Feedback visual de errores
- Filtros: Todos / Activos / Inactivos

### Flujos de Uso

**Crear Empleado:**
1. Click "➕ Nuevo Empleado"
2. Ingresar nombre y apellido
3. Ingresar documento (opcional pero recomendado)
4. Agregar teléfono y dirección
5. Seleccionar puesto
6. Definir salario diario
7. Establecer fecha de ingreso
8. Guardar

**Editar Empleado:**
1. Click en empleado
2. Click "✏️ Editar"
3. Modificar campos necesarios
4. Guardar cambios

**Desactivar Empleado:**
1. Click en empleado
2. Click "🚫 Desactivar"
3. Confirmación
4. Empleado marcado como inactivo

## Puestos Comunes

- Cocinero/a
- Ayudante de Cocina
- Mesero/a
- Cajero/a
- Limpieza
- Gerente
- Supervisor

## Validaciones

### Backend
- Nombre obligatorio (max 100 caracteres)
- Apellido obligatorio (max 100 caracteres)
- Documento único si se proporciona
- Salario diario > 0
- Fecha de ingreso obligatoria
- Fecha de ingreso no puede ser futura

### Frontend
- Campos obligatorios marcados
- Validación de formato de documento
- Validación de teléfono
- Salario numérico positivo
- Fecha válida

## Seguridad
- Solo usuarios DUENO/ADMIN pueden gestionar empleados
- Rutas protegidas con guards
- Validación de permisos en backend
- Auditoría de cambios

## Integración con Otras HUs

### HU-006: Nómina Diaria (Preparado)
✅ Base de empleados lista
✅ Salarios diarios configurados
✅ Consulta de empleados activos
✅ Estructura preparada

### HU-007: Contabilidad (Preparado)
- Egresos por nómina
- Costo de personal

### HU-012: Auditoría (Preparado)
- Registro de cambios en empleados
- Historial de modificaciones

## Reportes Disponibles

**Consultas útiles:**
- Lista de empleados activos
- Empleados por puesto
- Total de salarios diarios
- Empleados por fecha de ingreso
- Costo mensual estimado de nómina

## Mejoras Futuras

- [ ] Foto del empleado
- [ ] Documentos adjuntos (contrato, DPI, etc.)
- [ ] Historial de puestos
- [ ] Historial de salarios
- [ ] Días de vacaciones
- [ ] Permisos y ausencias
- [ ] Evaluaciones de desempeño
- [ ] Bonificaciones
- [ ] Deducciones
- [ ] Contacto de emergencia
- [ ] Horarios de trabajo
- [ ] Control de asistencia

## Testing

**Probar Creación:**
```bash
curl -X POST http://localhost:8080/api/empleados \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Carlos",
    "apellido": "López",
    "documento": "001-456789-0",
    "telefono": "9999-0000",
    "puesto": "Mesero",
    "salarioDiario": 120.00,
    "fechaIngreso": "2024-11-01"
  }'
```

**Probar Validación de Documento Duplicado:**
```bash
# Intentar crear con documento existente
curl -X POST http://localhost:8080/api/empleados \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Test",
    "apellido": "Test",
    "documento": "001-123456-7",
    "puesto": "Test",
    "salarioDiario": 100.00,
    "fechaIngreso": "2024-11-01"
  }'
# Debe retornar error 400
```

**Consultar Empleados Activos:**
```bash
curl http://localhost:8080/api/empleados/activos
```

## Notas Técnicas

- Soft delete (activo/inactivo) en lugar de eliminar
- Documento único pero opcional
- Salario diario como base para nómina
- Fecha de ingreso para cálculos de antigüedad
- Método transient `getNombreCompleto()` para facilitar uso
- Índices en queries frecuentes
- Validaciones en servicio y controlador

## Cálculos Útiles

**Salario Mensual Estimado:**
```
salario_mensual = salario_diario × 26 días laborables
```

**Costo Total de Nómina Diaria:**
```sql
SELECT SUM(salario_diario) as costo_diario
FROM empleados
WHERE activo = true;
```

**Empleados por Puesto:**
```sql
SELECT puesto, COUNT(*) as cantidad, SUM(salario_diario) as costo_diario
FROM empleados
WHERE activo = true
GROUP BY puesto;
```
