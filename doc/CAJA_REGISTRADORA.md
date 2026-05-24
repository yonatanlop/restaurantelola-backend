# Integración con Caja Registradora

## Descripción

Se ha implementado la funcionalidad para abrir automáticamente el cajón de la caja registradora al confirmar una venta, con un botón manual de respaldo.

## Características

### 1. Apertura Automática
- ✅ El cajón se abre automáticamente al confirmar una venta
- ✅ Funciona con cualquier método de pago (Efectivo, Crédito, Transferencia)
- ✅ No interrumpe el flujo de venta si falla

### 2. Botón Manual
- ✅ Botón "🔓 Abrir Caja" disponible en la página de ventas
- ✅ Permite abrir el cajón manualmente en cualquier momento
- ✅ Útil si la apertura automática falla

### 3. Compatibilidad
- ✅ Windows (COM1, COM2, COM3, etc.)
- ✅ Linux (/dev/ttyS0, /dev/ttyUSB0, etc.)
- ✅ Protocolo ESC/POS estándar

## Configuración

### Archivo: `application.properties`

```properties
# Puerto serial donde está conectada la caja registradora
caja.registradora.puerto=COM1

# Habilitar/deshabilitar la apertura automática del cajón
caja.registradora.habilitada=true
```

### Configuración del Puerto

**Windows:**
- `COM1` - Puerto serial 1
- `COM2` - Puerto serial 2
- `COM3` - Puerto serial 3 (USB-Serial)

**Linux:**
- `/dev/ttyS0` - Puerto serial 1
- `/dev/ttyS1` - Puerto serial 2
- `/dev/ttyUSB0` - Adaptador USB-Serial

### Deshabilitar la Función

Si no tienes caja registradora o quieres deshabilitarla:

```properties
caja.registradora.habilitada=false
```

## Conexión Física

### Requisitos de Hardware

1. **Caja Registradora** con puerto RJ11/RJ12 o RS232
2. **Cable Serial** (RJ11 a DB9 o USB-Serial)
3. **Puerto disponible** en el PC

### Tipos de Conexión

#### Opción 1: Puerto Serial Nativo (DB9)
```
Caja Registradora (RJ11) → Cable RJ11-DB9 → PC (Puerto Serial)
```

#### Opción 2: Adaptador USB-Serial
```
Caja Registradora (RJ11) → Cable RJ11-DB9 → Adaptador USB-Serial → PC (USB)
```

### Configuración en Windows

1. Conectar la caja registradora al puerto
2. Abrir **Administrador de Dispositivos**
3. Buscar en **Puertos (COM y LPT)**
4. Identificar el puerto asignado (ej: COM3)
5. Actualizar `application.properties` con el puerto correcto

### Configuración en Linux

1. Conectar la caja registradora
2. Ejecutar: `dmesg | grep tty` para ver el puerto asignado
3. Dar permisos: `sudo chmod 666 /dev/ttyUSB0`
4. Actualizar `application.properties` con el puerto correcto

## Comando ESC/POS

El sistema envía el siguiente comando estándar:

```
ESC p m t1 t2
```

Donde:
- `ESC` = 0x1B (27)
- `p` = 0x70 (112)
- `m` = 0x00 (pin del cajón)
- `t1` = 0x19 (25) - Tiempo ON: 100ms
- `t2` = 0x19 (25) - Tiempo OFF: 100ms

Bytes: `[0x1B, 0x70, 0x00, 0x19, 0x19]`

## Uso

### Apertura Automática

1. Agregar productos al carrito
2. Hacer clic en **"Pagar"**
3. Seleccionar método de pago
4. Hacer clic en **"Confirmar Venta"**
5. ✅ El cajón se abre automáticamente

### Apertura Manual

1. En la página de ventas (POS)
2. Hacer clic en el botón **"🔓 Abrir Caja"** (esquina superior derecha)
3. ✅ El cajón se abre inmediatamente

## Solución de Problemas

### El cajón no se abre

**1. Verificar conexión física**
- Cable conectado correctamente
- Caja registradora encendida
- Puerto serial funcionando

**2. Verificar configuración**
```properties
# Verificar que esté habilitada
caja.registradora.habilitada=true

# Verificar puerto correcto
caja.registradora.puerto=COM1  # o el puerto correcto
```

**3. Verificar puerto en Windows**
- Administrador de Dispositivos → Puertos (COM y LPT)
- Verificar que el puerto existe y está activo

**4. Verificar permisos en Linux**
```bash
# Dar permisos al puerto
sudo chmod 666 /dev/ttyUSB0

# Agregar usuario al grupo dialout
sudo usermod -a -G dialout $USER
```

**5. Probar manualmente**
- Usar el botón "🔓 Abrir Caja" para probar
- Revisar logs del backend para ver errores

### Logs del Sistema

El sistema registra información en los logs:

```
INFO: Intentando abrir cajón en puerto: COM1
INFO: Comando enviado exitosamente al puerto COM1
```

O en caso de error:

```
ERROR: Error al abrir cajón de caja registradora: ...
WARN: Puerto COM1 no encontrado
```

## Archivos Implementados

### Backend
- `src/main/java/com/tialola/caja/service/CajaRegistradoraService.java`
  - Servicio principal para controlar la caja
  - Envía comandos ESC/POS al puerto serial
  - Soporta Windows y Linux

- `src/main/java/com/tialola/caja/controller/CajaRegistradoraController.java`
  - API REST para abrir cajón
  - Endpoint: `POST /api/caja-registradora/abrir-cajon`
  - Endpoint: `GET /api/caja-registradora/estado`

- `src/main/java/com/tialola/contabilidad/ventas/service/VentaService.java`
  - Integración con ventas
  - Abre cajón automáticamente al crear venta

### Frontend
- `tialola-frontend/src/shared/services/cajaRegistradoraApi.ts`
  - Cliente API para comunicarse con el backend

- `tialola-frontend/src/shared/components/BotonAbrirCaja.tsx`
  - Componente de botón reutilizable
  - Maneja estados de carga y errores

- `tialola-frontend/src/modules/ventas/pages/VentaTactilPage.tsx`
  - Integración del botón en la página de ventas

### Configuración
- `src/main/resources/application.properties`
  - Configuración del puerto y habilitación

## API REST

### Abrir Cajón
```http
POST /api/caja-registradora/abrir-cajon
```

**Respuesta exitosa:**
```json
{
  "exito": true,
  "mensaje": "Cajón abierto exitosamente",
  "puerto": "COM1"
}
```

**Respuesta con error:**
```json
{
  "exito": false,
  "mensaje": "No se pudo abrir el cajón. Verifica la configuración.",
  "puerto": "COM1"
}
```

### Obtener Estado
```http
GET /api/caja-registradora/estado
```

**Respuesta:**
```json
{
  "habilitada": true,
  "puerto": "COM1"
}
```

## Compatibilidad con Cajas Registradoras

Este sistema es compatible con la mayoría de cajas registradoras que soporten:

- ✅ Protocolo ESC/POS
- ✅ Conexión serial (RS232/RJ11)
- ✅ Comando estándar de apertura de cajón

**Marcas compatibles:**
- Epson
- Star Micronics
- Bixolon
- Citizen
- Y la mayoría de cajas genéricas con ESC/POS

## Fecha de Implementación
30 de noviembre de 2025
