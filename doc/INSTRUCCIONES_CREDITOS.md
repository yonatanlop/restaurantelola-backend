# Sistema de Créditos - Restaurante Doña Lola

## Descripción General
Sistema completo para gestionar ventas a crédito (fiado) con clientes del restaurante.

## Características Implementadas

### Backend (Java/Spring Boot)

#### Modelos
- **Cliente**: Gestión de clientes con crédito
  - Nombre, teléfono, dirección, notas
  - Cálculo automático de deuda total
  - Contador de créditos pendientes

- **Credito**: Registro de ventas a crédito
  - Fecha de pedido y valor
  - Estado de pago (pagado/pendiente)
  - Relación con cliente y venta
  - Usuarios de registro y pago

#### Endpoints REST (`/api/creditos`)

**Clientes:**
- `GET /clientes` - Listar todos los clientes
- `GET /clientes/{id}` - Obtener cliente por ID
- `POST /clientes` - Crear nuevo cliente
- `PUT /clientes/{id}` - Actualizar cliente

**Créditos:**
- `GET /cliente/{clienteId}` - Créditos de un cliente
- `GET /pendientes` - Todos los créditos pendientes
- `POST /registrar` - Registrar nuevo crédito
- `PUT /{id}/pagar` - Marcar crédito como pagado
- `GET /cliente/{clienteId}/deuda` - Deuda total del cliente

### Frontend (React/TypeScript)

#### Páginas
- **CreditosPage**: Vista principal con resumen y lista de clientes

#### Componentes
- **ListaClientes**: Grid de tarjetas con clientes y sus deudas
- **DetalleCreditos**: Vista detallada de créditos de un cliente
- **ModalNuevoCliente**: Formulario para registrar clientes

#### Hooks
- **useCreditos**: Gestión de estado y carga de datos

#### Servicios
- **creditosApi**: Comunicación con el backend

### Base de Datos

#### Tablas
```sql
clientes
- id (PK)
- nombre
- telefono
- direccion
- notas

creditos
- id (PK)
- cliente_id (FK)
- venta_id (FK, nullable)
- fecha_pedido
- valor_pedido
- pagado (boolean)
- fecha_pago
- usuario_registro_id
- usuario_pago_id
- descripcion
- notas
```

## Flujo de Uso

### 1. Registrar Cliente
1. Ir a "Créditos" en el menú avanzado
2. Clic en "+ Nuevo Cliente"
3. Llenar nombre (obligatorio) y datos opcionales
4. Guardar

### 2. Registrar Venta a Crédito
**Desde el módulo de ventas (POS):**
1. Agregar productos al carrito
2. Clic en "Procesar Venta"
3. Seleccionar método de pago "Crédito" 💳
4. Aparece lista de clientes con crédito
5. Buscar cliente por nombre o teléfono
6. Seleccionar el cliente (o crear uno nuevo con "+ Nuevo")
7. Confirmar la venta
8. El sistema registra automáticamente:
   - La venta en el sistema
   - El crédito asociado al cliente
   - Actualiza la deuda total del cliente

### 3. Ver Créditos de un Cliente
1. En la página de créditos, clic en tarjeta del cliente
2. Ver lista de créditos pendientes y pagados
3. Ver deuda total actualizada

### 4. Marcar Crédito como Pagado
1. En detalle del cliente
2. Clic en "Marcar como Pagado" en el crédito
3. Confirmar acción
4. El crédito pasa a historial pagado

## Integración con Ventas

El método de pago cambió de "TARJETA" a "CREDITO":
- **EFECTIVO**: Pago en efectivo con cálculo de cambio
- **CREDITO**: Venta a crédito (fiado) con selector de cliente
- **TRANSFERENCIA**: Pago por transferencia bancaria

### Flujo Completo de Venta a Crédito:

1. **Cajero selecciona "CREDITO"**:
   - Se muestra lista de clientes registrados
   - Búsqueda en tiempo real por nombre o teléfono
   - Opción de crear nuevo cliente rápidamente

2. **Información mostrada por cliente**:
   - Nombre y teléfono
   - Deuda actual
   - Número de créditos pendientes
   - Nueva deuda después de esta venta

3. **Al confirmar la venta**:
   - Se registra la venta normalmente
   - Se crea automáticamente el registro de crédito
   - Se vincula con el cliente seleccionado
   - Se actualiza la deuda total del cliente
   - Se genera el ticket de venta

4. **Datos del crédito registrado**:
   - Cliente asociado
   - Venta asociada (ID)
   - Valor del pedido
   - Descripción: "Venta #X - Y producto(s)"
   - Notas: Detalle de productos
   - Usuario que registró
   - Fecha de registro

## Acceso
- **Ruta**: `/dueno/avanzado/creditos`
- **Rol requerido**: DUENO o ADMIN
- **Icono en menú**: 💳 Créditos

## Archivos Creados

### Backend
```
src/main/java/com/tialola/credito/
├── model/
│   ├── Cliente.java
│   └── Credito.java
├── repository/
│   ├── ClienteRepository.java
│   └── CreditoRepository.java
├── service/
│   └── CreditoService.java
└── controller/
    └── CreditoController.java
```

### Frontend
```
tialola-frontend/src/modules/creditos/
├── components/
│   ├── DetalleCreditos.tsx
│   ├── ListaClientes.tsx
│   └── ModalNuevoCliente.tsx
├── hooks/
│   └── useCreditos.ts
├── pages/
│   ├── CreditosPage.tsx
│   └── CreditosPage.css
├── services/
│   └── creditosApi.ts
├── types/
│   └── creditos.types.ts
└── index.ts

tialola-frontend/src/modules/ventas/components/
├── ModalMetodoPago.tsx (modificado - integración con créditos)
└── ModalNuevoClienteRapido.tsx (nuevo)

tialola-frontend/src/styles/
└── pos.css (estilos para selector de clientes)
```

### Base de Datos
```
database/scripts/08_creditos_tables.sql
```

## Características Adicionales Implementadas

### Selector de Clientes en POS
- Lista completa de clientes con crédito
- Búsqueda en tiempo real
- Información de deuda actual visible
- Creación rápida de nuevos clientes
- Validación antes de confirmar venta

### Registro Automático
- El crédito se registra automáticamente al confirmar venta
- Se vincula con la venta para trazabilidad
- Se guarda descripción y detalle de productos
- Se registra el usuario que realizó la operación

## Próximos Pasos Sugeridos

1. **Reportes**:
   - Reporte de clientes con mayor deuda
   - Historial de pagos por período
   - Análisis de créditos vencidos

3. **Notificaciones**:
   - Alertas de créditos antiguos sin pagar
   - Recordatorios de cobro

4. **Mejoras**:
   - Límite de crédito por cliente
   - Fechas de vencimiento
   - Intereses por mora
   - Abonos parciales

## Notas Técnicas

- El backend usa JPA con relaciones bidireccionales
- El frontend usa React Hooks para gestión de estado
- Los estilos siguen el patrón del resto de la aplicación
- Las consultas incluyen cálculos agregados (deuda total, créditos pendientes)
