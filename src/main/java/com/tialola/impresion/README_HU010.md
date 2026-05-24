# HU-010: Integración con Impresora de Tickets

## Descripción
Sistema de impresión de tickets y comandas para impresoras térmicas de 80mm con cola de impresión y gestión de errores.

## Funcionalidades Implementadas

### 1. Gestión de Configuración de Impresoras
- Soporte para múltiples impresoras
- Configuración de formato (ancho, caracteres por línea)
- Personalización de encabezado y pie de página
- Impresora predeterminada
- Tipos: TERMICA, MATRICIAL, PDF

### 2. Impresión de Tickets de Venta
- Formato profesional con información completa
- Detalles de productos con cantidades y precios
- Totales, impuestos y propinas
- Método de pago
- Notas adicionales

### 3. Impresión de Comandas
- Formato simplificado para cocina
- Lista de platos con cantidades
- Notas especiales resaltadas
- Número de orden y fecha/hora

### 4. Cola de Impresión
- Sistema de cola para gestionar trabajos
- Estados: PENDIENTE, IMPRIMIENDO, COMPLETADO, ERROR
- Reintentos automáticos en caso de error
- Historial de impresiones

### 5. Formateador de Tickets
- Alineación automática de texto
- Centrado y alineación a derecha
- Truncado inteligente de texto largo
- Líneas separadoras

## Estructura de Archivos

```
impresion/
├── controller/
│   └── ImpresionController.java
├── dto/
│   └── TicketDTO.java
├── model/
│   ├── ConfiguracionImpresora.java
│   └── ColaImpresion.java
├── repository/
│   ├── ConfiguracionImpresoraRepository.java
│   └── ColaImpresionRepository.java
└── service/
    ├── ImpresionService.java
    └── FormateadorTicketService.java
```

## Endpoints API

### Impresión
- `POST /api/impresion/ticket/{ventaId}` - Imprimir ticket de venta
- `POST /api/impresion/comanda/{ventaId}` - Imprimir comanda
- `POST /api/impresion/procesar-cola` - Procesar cola pendiente

### Cola de Impresión
- `GET /api/impresion/cola` - Obtener trabajos pendientes
- `POST /api/impresion/cola/{colaId}/reintentar` - Reintentar impresión fallida

### Configuración (Solo DUENO)
- `GET /api/impresion/configuracion` - Listar configuraciones
- `POST /api/impresion/configuracion` - Crear configuración
- `PUT /api/impresion/configuracion/{id}` - Actualizar configuración

## Modelos de Datos

### ConfiguracionImpresora
```java
{
  "id": 1,
  "nombre": "Impresora Principal",
  "tipoImpresora": "TERMICA",
  "nombreImpresora": "POS-80",
  "anchoPapel": 80,
  "caracteresLinea": 42,
  "imprimirLogo": false,
  "textoEncabezado": "RESTAURANTE DOÑA LOLA\nCalle Principal #123",
  "textoPie": "Gracias por su preferencia",
  "autoCortar": true,
  "copiasComanda": 2,
  "copiasTicket": 1,
  "activo": true,
  "esPredeterminada": true
}
```

### ColaImpresion
```java
{
  "id": 1,
  "tipoDocumento": "VENTA",
  "referenciaId": 123,
  "impresoraId": 1,
  "estado": "COMPLETADO",
  "intentos": 1,
  "mensajeError": null,
  "fechaCreacion": "2024-11-16T20:00:00",
  "fechaImpresion": "2024-11-16T20:00:05"
}
```

## Ejemplo de Uso

### 1. Imprimir Ticket después de una Venta
```bash
curl -X POST http://localhost:8080/api/impresion/ticket/123 \
  -H "Authorization: Bearer {token}"
```

### 2. Imprimir Comanda para Cocina
```bash
curl -X POST http://localhost:8080/api/impresion/comanda/123 \
  -H "Authorization: Bearer {token}"
```

### 3. Verificar Cola de Impresión
```bash
curl -X GET http://localhost:8080/api/impresion/cola \
  -H "Authorization: Bearer {token}"
```

### 4. Configurar Nueva Impresora
```bash
curl -X POST http://localhost:8080/api/impresion/configuracion \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Impresora Cocina",
    "tipoImpresora": "TERMICA",
    "nombreImpresora": "Kitchen-Printer",
    "anchoPapel": 80,
    "caracteresLinea": 42,
    "textoEncabezado": "COCINA - RESTAURANTE DOÑA LOLA",
    "copiasComanda": 2,
    "activo": true,
    "esPredeterminada": false
  }'
```

## Formato de Ticket de Venta

```
========================================
        RESTAURANTE DOÑA LOLA
        Calle Principal #123
        Tel: (555) 123-4567
========================================
          TICKET DE VENTA
========================================
Ticket #: 123
Fecha: 16/11/2024 20:30:45
Cajero: Juan Perez
========================================
PRODUCTO             Cant   P.Unit    Total
========================================
Ceviche de Pescado      2    45.00    90.00
Arroz con Mariscos      1    55.00    55.00
Chicha Morada           2    10.00    20.00
========================================
                      Subtotal: $165.00
                      Impuestos: $29.70
                        Propina: $15.00
========================================
                         TOTAL: $209.70
========================================
Metodo de pago: EFECTIVO

   Gracias por su preferencia
   www.restaurantelola.com

      ¡Gracias por su compra!
```

## Formato de Comanda

```
========================================
           *** COMANDA ***
========================================
Orden #: 123
Fecha: 16/11/2024 20:30:45
========================================

[ 2 ] Ceviche de Pescado
     ** Sin cebolla **

[ 1 ] Arroz con Mariscos

[ 2 ] Chicha Morada

========================================
Total items: 5
```

## Integración con Sistema de Ventas

El servicio se puede integrar automáticamente con el proceso de ventas:

```java
// En VentaService después de guardar la venta
@Autowired
private ImpresionService impresionService;

public Venta crearVenta(VentaDTO ventaDTO) {
    Venta venta = // ... guardar venta
    
    // Imprimir automáticamente
    impresionService.encolarTicketVenta(venta.getId());
    impresionService.encolarComanda(venta.getId());
    impresionService.procesarColaPendiente();
    
    return venta;
}
```

## Configuración de Impresoras del Sistema

### Windows
Las impresoras deben estar instaladas en el sistema operativo. El servicio buscará por nombre o usará la predeterminada.

### Linux
```bash
# Listar impresoras disponibles
lpstat -p -d

# Configurar impresora predeterminada
lpoptions -d nombre-impresora
```

## Manejo de Errores

El sistema maneja automáticamente:
- Impresora no disponible
- Errores de comunicación
- Papel agotado
- Reintentos automáticos

Los trabajos fallidos quedan en estado ERROR y pueden reintentarse manualmente.

## Base de Datos

### Ejecutar Script
```bash
psql -U postgres -d restaurante_lola -f database/scripts/05_impresion_tables.sql
```

### Tablas Creadas
- `configuracion_impresora` - Configuración de impresoras
- `cola_impresion` - Cola de trabajos de impresión

## Notas Técnicas

1. **Java Print Service**: Utiliza la API estándar de Java para impresión
2. **Formato Térmico**: Optimizado para impresoras de 80mm (42 caracteres)
3. **Codificación**: UTF-8 para soporte de caracteres especiales
4. **Cola Asíncrona**: Permite continuar operaciones sin esperar la impresión
5. **Modo PDF**: Para desarrollo/testing sin impresora física

## Próximas Mejoras

- [ ] Soporte para códigos de barras
- [ ] Impresión de logos/imágenes
- [ ] Comandos ESC/POS para control avanzado
- [ ] Impresión por red (IP)
- [ ] Plantillas personalizables
- [ ] Previsualización de tickets
