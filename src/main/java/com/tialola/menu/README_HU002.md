# HU-002: Gestión de Menú y Precios de Platos

## Descripción
Permite al dueño crear, editar, activar/desactivar platos del menú con una interfaz táctil intuitiva. Incluye gestión de precios y categorización por tipo de comida.

## Funcionalidades Implementadas

### Frontend

#### Página de Gestión de Menú
- **Grid de platos** con tarjetas visuales
- **Filtros táctiles** por categoría y estado
- **Botón grande** para crear nuevo plato
- **Acciones rápidas** en cada tarjeta (Editar, Activar/Desactivar)

#### Modal de Plato (Crear/Editar)
- **Formulario táctil** con campos grandes
- **Selector de categoría** con iconos visuales:
  - 🍳 Desayuno
  - 🍛 Almuerzo
  - 🥤 Bebida
  - 🍰 Postre
- **Teclado numérico** integrado para precio
- **Validaciones** en tiempo real

#### Componentes
- **GestionMenuPage**: Página principal
- **TarjetaPlato**: Tarjeta de plato con acciones
- **ModalPlato**: Modal para crear/editar
- **FiltrosMenu**: Filtros por categoría y estado
- **TecladoNumerico**: Teclado táctil para precios (reutilizado)

### Backend

#### Endpoints

**GET /api/platos**
- Obtiene todos los platos
- Response: Lista completa de platos

**GET /api/platos/activos**
- Obtiene solo platos activos
- Usado por el POS

**GET /api/platos/{id}**
- Obtiene un plato específico

**POST /api/platos**
- Crea un nuevo plato
- Request:
```json
{
  "nombre": "Desayuno Típico",
  "descripcion": "Huevos, frijoles, queso, crema",
  "precio": 45.00,
  "tipoComida": "DESAYUNO",
  "activo": true
}
```

**PUT /api/platos/{id}**
- Actualiza un plato existente

**PATCH /api/platos/{id}/estado**
- Cambia el estado activo/inactivo
- Request: `{ "activo": true }`

**DELETE /api/platos/{id}**
- Elimina un plato (soft delete recomendado)

#### Servicios
- **PlatoService**: Lógica de negocio
- **PlatoDTO**: DTO para transferencia de datos

## Características de UI/UX

### Diseño Táctil
- Botones mínimo 50px de altura
- Tarjetas grandes con información clara
- Iconos visuales para categorías
- Feedback visual en todas las interacciones

### Filtros
- **Por categoría**: Todos, Desayunos, Almuerzos, Bebidas, Postres
- **Por estado**: Todos, Activos, Inactivos
- Contador de resultados

### Tarjetas de Plato
- Badge de categoría con icono
- Badge de estado (Activo/Inactivo)
- Nombre y descripción
- Precio destacado
- Botones de acción grandes

### Modal de Edición
- Campos de entrada grandes
- Selector visual de categorías
- Teclado numérico para precio
- Validaciones claras

## Validaciones

### Frontend
- Nombre obligatorio
- Precio mayor a 0
- Categoría seleccionada
- Feedback visual de errores

### Backend
- Validación de datos requeridos
- Validación de tipos de datos
- Manejo de errores

## Flujo de Uso

### Crear Plato
1. Click en "➕ Nuevo Plato"
2. Ingresar nombre
3. Ingresar descripción (opcional)
4. Seleccionar categoría
5. Ingresar precio con teclado numérico
6. Guardar

### Editar Plato
1. Click en "✏️ Editar" en tarjeta
2. Modificar campos
3. Guardar cambios

### Activar/Desactivar
1. Click en botón de estado en tarjeta
2. Confirmación automática
3. Actualización visual inmediata

## Integración con Otras HUs

### HU-001: POS
- Los platos activos aparecen en el POS
- Cambios de precio se reflejan inmediatamente
- Platos inactivos no se muestran en ventas

### HU-003: Inventario (Preparado)
- Estructura lista para asociar insumos
- Campo categoriaId preparado
- Relación con recetas

### HU-014: Propinas (Preparado)
- Estructura de precios lista
- Campos adicionales preparados

## Datos de Ejemplo

Los siguientes platos vienen precargados:
- Desayuno Completo - $45.00
- Café con Leche - $15.00
- Almuerzo del Día - $65.00
- Jugo Natural - $20.00
- Flan Casero - $25.00

## Seguridad
- Solo usuarios con rol DUENO o ADMIN pueden gestionar menú
- Rutas protegidas con guards
- Validación de permisos en backend

## Mejoras Futuras
- Subir imágenes de platos
- Duplicar platos
- Ordenar platos
- Búsqueda de platos
- Historial de cambios de precios
- Precios por horario
- Combos y promociones
