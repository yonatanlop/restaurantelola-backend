# HU-001: Registro Táctil de Ventas (POS)

## Descripción
Sistema de punto de venta táctil optimizado para registrar ventas de desayunos y almuerzos con interfaz intuitiva y botones grandes.

## Funcionalidades Implementadas

### Frontend

#### Pantalla POS Táctil
- **Grid de productos** con botones grandes y táctiles
- **Filtros por categoría**: Todos, Desayunos, Almuerzos, Bebidas, Postres
- **Carrito de compra** en tiempo real
- **Gestión de cantidades**: Incrementar, decrementar, eliminar
- **Cálculo automático** de subtotales y total

#### Flujo de Venta
1. Seleccionar productos desde el grid
2. Ajustar cantidades en el carrito
3. Procesar venta
4. Seleccionar método de pago (Efectivo, Tarjeta, Transferencia)
5. Confirmar venta
6. Generar ticket

#### Componentes
- **VentaTactilPage**: Página principal del POS
- **BotonProducto**: Botón táctil de producto
- **CarritoVenta**: Carrito con resumen
- **ModalMetodoPago**: Selección de método de pago
- **ModalTicket**: Ticket de venta con opción de impresión

### Backend

#### Endpoints

**GET /api/platos/activos**
- Obtiene todos los platos activos
- Response: Lista de platos con id, nombre, precio, categoría

**POST /api/ventas**
- Crea una nueva venta
- Request:
```json
{
  "usuarioId": 1,
  "subtotal": 100.00,
  "impuestos": 0,
  "propina": 0,
  "total": 100.00,
  "metodoPago": "EFECTIVO",
  "detalles": [
    {
      "platoId": 1,
      "cantidad": 2,
      "precioUnitario": 50.00,
      "subtotal": 100.00
    }
  ]
}
```

**GET /api/ventas/dia**
- Obtiene ventas del día actual

#### Modelos
- **Venta**: Encabezado de venta
- **VentaDetalle**: Detalle de productos vendidos
- **Plato**: Productos del menú

## Características de UI/UX

### Diseño Táctil
- Botones mínimo 60px de altura
- Espaciado generoso entre elementos
- Iconos visuales claros
- Feedback visual en interacciones

### Colores
- Primario: Morado (#667eea)
- Éxito: Verde (#10b981)
- Categorías con iconos distintivos

### Responsive
- Grid adaptable de productos
- Carrito lateral en desktop
- Carrito inferior en mobile

## Ticket de Venta

### Información Incluida
- Logo y nombre del restaurante
- Número de ticket
- Fecha y hora
- Cajero
- Detalle de productos (cantidad, nombre, precio)
- Subtotal, impuestos, propina
- Total
- Método de pago
- Mensaje de agradecimiento

### Funcionalidades
- Vista previa del ticket
- Botón de impresión (window.print())
- Formato optimizado para impresoras térmicas

## Métodos de Pago

- **Efectivo** 💵
- **Tarjeta** 💳
- **Transferencia** 📱

## Validaciones

### Frontend
- No permitir venta con carrito vacío
- Cantidades mínimas de 1
- Validación de datos antes de enviar

### Backend
- Validación de usuario existente
- Validación de platos existentes
- Cálculo correcto de totales
- Registro de fecha automático

## Preparado para Futuras Funcionalidades

### HU-003: Descuento Automático de Inventario
- Estructura de detalles lista para consumo de insumos
- Relación platos-insumos preparada

### HU-010: Impresión de Tickets
- Formato de ticket listo
- Función de impresión implementada
- Estilos de impresión (@media print)

### HU-014: Propinas
- Campos de propina en modelo
- Cálculo preparado en totales

## Seguridad
- Solo usuarios autenticados pueden registrar ventas
- Registro de usuario que realizó la venta
- Auditoría automática de fecha/hora

## Próximas Mejoras
- Búsqueda de productos
- Descuentos por producto
- Notas en productos
- Historial de ventas del cajero
- Cancelación de ventas
- Reimpresión de tickets
