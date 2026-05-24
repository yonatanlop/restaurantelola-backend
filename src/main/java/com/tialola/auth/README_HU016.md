# HU-016: Control de Permisos y Menús por Rol

## Descripción
Sistema de control de acceso basado en roles con menús diferenciados para Dueño y Cajero.

## Roles Implementados

### CAJERO
**Permisos:**
- Acceso a pantalla de ventas (POS)
- Solo puede registrar ventas
- No puede acceder a configuraciones ni reportes

**Menú:**
- Nueva Venta
- Cerrar Sesión

### DUENO / ADMIN
**Permisos:**
- Acceso total al sistema
- Menú Rápido con dashboard e indicadores
- Menú Avanzado con todas las funcionalidades

**Menú Rápido:**
- Dashboard con indicadores
- Ventas del Día
- Acceso a Menú Avanzado

**Menú Avanzado:**
- Inventario
- Compras
- Nómina
- Contabilidad
- Reportes
- Configuración
- Auditoría

## Componentes Frontend

### Guards
- **ProtectedRoute**: Protege rutas según autenticación y roles

### Providers
- **AuthProvider**: Context de autenticación global
  - Maneja usuario actual
  - Valida roles
  - Gestiona login/logout

### Layouts
- **CajeroLayout**: Layout simplificado para cajeros
- **DuenoLayout**: Layout con menú rápido
- **DuenoAvanzadoLayout**: Layout con menú avanzado completo

### Páginas
- **DashboardDuenoPage**: Dashboard con indicadores y accesos rápidos
- **UnauthorizedPage**: Página de acceso no autorizado

## Rutas Protegidas

### Rutas del Cajero
```
/cajero
  └─ /venta (POS táctil)
```

### Rutas del Dueño - Menú Rápido
```
/dueno
  ├─ /dashboard (indicadores)
  └─ /ventas-dia
```

### Rutas del Dueño - Menú Avanzado
```
/dueno/avanzado
  ├─ /inventario
  ├─ /compras
  ├─ /nomina
  ├─ /contabilidad
  ├─ /reportes
  ├─ /configuracion
  └─ /auditoria
```

## Flujo de Autenticación

1. Usuario ingresa credenciales en `/login`
2. Sistema valida y genera JWT
3. Token y datos de usuario se guardan en localStorage
4. AuthProvider carga datos al iniciar
5. ProtectedRoute valida acceso a cada ruta
6. Usuario es redirigido según su rol:
   - CAJERO → `/cajero/venta`
   - DUENO/ADMIN → `/dueno/dashboard`

## Validación de Permisos

```typescript
// Verificar si usuario tiene rol específico
const { hasRole } = useAuth()

if (hasRole(['DUENO', 'ADMIN'])) {
  // Mostrar funcionalidad
}
```

## Características de UI

### Dashboard del Dueño
- **Indicadores visuales**: Ventas, stock, empleados
- **Accesos rápidos**: Botones grandes táctiles
- **Alertas**: Notificaciones de bajo stock, etc.

### Menú Táctil
- Botones grandes (min 60px)
- Iconos visuales claros
- Colores diferenciados por rol
- Navegación intuitiva

## Seguridad

- Rutas protegidas con guards
- Validación de roles en frontend y backend
- Redirección automática si no autorizado
- Token JWT con expiración
- Logout limpia localStorage

## Próximas Mejoras

- Permisos granulares por funcionalidad
- Roles personalizables
- Auditoría de accesos
- Sesiones concurrentes
