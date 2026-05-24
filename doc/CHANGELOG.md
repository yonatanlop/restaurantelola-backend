# Changelog - Restaurante Doña Lola

## [1.1.1] - 2026-01-26: Optimización Layout Cajero y Fix Doble Clic

### ✨ Optimizado
- **Layout del cajero reorganizado en 3 columnas**:
  - Columna izquierda: Productos en grid de 3 columnas
  - Columna central: Carrito de compras (300px)
  - Columna derecha: Panel de pago (400px)
- **Panel de pago siempre visible**: Convertido de modal a panel fijo
- **Calculadora optimizada**: Botones reducidos a 45px para mejor ajuste
- **Todo visible sin scroll**: Optimizado para pantallas 1080p

### 🎨 Mejorado
- Experiencia de usuario más fluida sin necesidad de scroll
- Todo el proceso de venta visible en una sola pantalla
- Botones de método de pago más compactos (padding 0.75rem)
- Input de monto optimizado (font-size 1.3rem)
- Lista de clientes para crédito más compacta (200px)
- CSS limpio sin duplicados (reducido de 86.70 kB a 78.12 kB)

### 🐛 Corregido
- **FIX CRÍTICO: Doble clic en pantallas táctiles**:
  - Productos ya no se agregan duplicados al carrito
  - Transferencias ya no se registran 2 veces
  - Implementada protección de múltiples capas con debouncing
  - Timeouts apropiados: 300ms (productos), 500ms (procesar), 1000ms (confirmar)
  - Feedback visual mejorado con estados de "Procesando..."
- Eliminado contenido duplicado en pos.css
- Corregidos errores de TypeScript (imports no usados)
- Agregado vite-env.d.ts para tipos de import.meta.env
- Parámetros no usados con prefijo _ en MesasPage

## [1.1.0] - 2026-01-19: Nuevo Sistema de Login Simplificado

### ✨ Nuevo
- **Sistema de login con dos perfiles diferenciados**:
  - **Cajero**: Acceso directo sin contraseña (un solo clic)
  - **Dueño**: Acceso con validación de contraseña
- **Interfaz rediseñada** con botones grandes e iconos intuitivos
- **Modal elegante** para ingreso de contraseña del dueño
- **Diseño responsive** optimizado para tablets y móviles

### 🔐 Backend
- Nuevo endpoint `POST /api/auth/login/cajero` - Login sin contraseña
- Nuevo endpoint `POST /api/auth/login/dueno` - Login con contraseña
- Método `loginCajeroSinPassword()` en AuthService
- Método `loginDueno(LoginRequest)` en AuthService
- Búsqueda de usuarios por rol en UsuarioRepository

### 🎨 Frontend
- LoginPage completamente rediseñada
- Botón "Cajero" (💰) con acceso directo
- Botón "Dueño" (👔) con modal de contraseña
- Estilos CSS modernos y responsive
- Animaciones suaves y feedback visual

### 📝 Documentación
- `NUEVO_SISTEMA_LOGIN.md` - Documentación completa
- `RESUMEN_CAMBIOS_LOGIN.md` - Resumen ejecutivo
- `DESPLIEGUE_RAPIDO_LOGIN.md` - Guía rápida
- Script SQL `11_setup_usuarios_login.sql`
- Script de pruebas `test-nuevo-login.bat`

### 🔄 Cambios
- Versión actualizada a 1.1.0
- Copyright actualizado a 2026
- Textos simplificados en interfaz de login

### 🛡️ Seguridad
- Cajero mantiene acceso limitado solo a punto de venta
- Dueño mantiene validación de contraseña con BCrypt
- Separación estricta de roles y permisos
- Tokens JWT con roles correspondientes

### ⚙️ Compatibilidad
- Mantiene endpoint anterior `/api/auth/login` para compatibilidad
- Sin breaking changes
- Migración transparente

---

## [0.12.0] - HU-014: Estructura para Propinas por Venta

### ✅ Agregado
- Entidad `PropinaVenta` y tabla `propinas_venta`
- Servicio `PropinaVentaService` con flag `propinas.enabled`
- Endpoint `GET /api/propinas/estado` para monitorear el feature
- Registro automático de propinas al crear ventas (sin afectar tickets/reportes)
- Script SQL incremental `08_cierre_propinas.sql`

### 🔒 Feature Flag
- Configuración `propinas.enabled=false` por defecto
- Permite habilitar el flujo sin cambios de código
- Mantiene reportes y tickets sin propinas hasta activación oficial

---

## [0.11.0] - HU-013: Cierre de Caja Diario

### ✅ Agregado
- Servicio completo de cierre con estados ABIERTO/CERRADO
- Arqueo por denominaciones y cálculo automático de diferencias
- Alertas por días sin cierre y ventas posteriores
- Reporte JSON listo para exportar a PDF
- Nueva API REST `/api/cierre-caja/*`
- Página React para apertura/cierre y visualización de alertas

### 🔐 Seguridad y Validaciones
- Solo el Dueño/Admin puede iniciar/cerrar (validado vía `UsuarioRepository`)
- Un cierre abierto por día
- Historial y filtros por rango de fechas

---

## [0.10.0] - HU-008: Reportes Descargables de Ventas

### ✅ Agregado
- **Sistema de reportes descargables** en Excel y PDF
- **Filtros avanzados** por fecha, método de pago y tipo
- **Generación de Excel** con Apache POI
- **Generación de PDF** con formato profesional
- **Totales automáticos** en reportes
- **Formato profesional** con estilos y colores
- **Servicio**: ReporteVentasService
- **Endpoints REST** para descarga

### 📊 Funcionalidades
- Descargar reporte de ventas en Excel
- Descargar reporte de ventas en PDF
- Filtrar por rango de fechas
- Filtrar por método de pago (opcional)
- Filtrar por tipo de comida (opcional)
- Totales automáticos calculados
- Formato de moneda (Lempiras)
- Nombre de archivo con timestamp

### 🔧 Backend
- Endpoint: GET /api/reportes/ventas/excel?inicio=&fin=
- Endpoint: GET /api/reportes/ventas/pdf?inicio=&fin=
- Parámetros opcionales: metodoPago, tipoComida
- Apache POI para Excel (XLSX)
- iText preparado para PDF avanzado

### 📈 Formato Excel Incluye
- Encabezados con estilo
- ID de venta
- Fecha y hora
- Cajero
- Subtotal, impuestos, propina
- Total
- Método de pago
- Estado
- Fila de totales al final
- Columnas auto-ajustadas

### 🔗 Integración
- HU-001: Reportes de ventas del POS
- HU-007: Compatible con contabilidad
- Preparado para más tipos de reportes

### 📦 Dependencias Agregadas
- Apache POI 5.2.5 (Excel)
- iText7 7.2.5 (PDF)

---

## [0.9.0] - HU-007: Contabilidad Básica de Caja

### ✅ Agregado
- **Sistema de contabilidad básica** con movimientos de caja
- **Registro automático** de ingresos y egresos
- **Resumen de caja** por periodo
- **Cálculo de saldos** automático
- **Desglose por concepto** (ventas, compras, nómina)
- **Modelo**: MovimientoCaja con referencias
- **Servicio**: ContabilidadService con cálculos
- **Endpoints REST** completos

### 📊 Funcionalidades
- Registro automático de ventas (INGRESO)
- Registro automático de compras (EGRESO)
- Registro automático de nómina (EGRESO)
- Consultar movimientos por periodo
- Filtrar por tipo (INGRESO/EGRESO)
- Filtrar por concepto (VENTA/COMPRA/NOMINA)
- Resumen con totales y saldo
- Desglose detallado de ingresos y egresos

### 🔧 Backend
- Endpoint: GET /api/contabilidad/movimientos?inicio=&fin=
- Endpoint: GET /api/contabilidad/movimientos/tipo/{tipo}?inicio=&fin=
- Endpoint: GET /api/contabilidad/movimientos/concepto/{concepto}?inicio=&fin=
- Endpoint: GET /api/contabilidad/resumen?inicio=&fin=
- Endpoint: POST /api/contabilidad/movimiento

### 💰 Cálculos Automáticos
- Total ingresos = suma de INGRESO
- Total egresos = suma de EGRESO
- Saldo = ingresos - egresos
- Desglose por concepto automático

### 🔗 Integración
- HU-001: Registra ventas como INGRESO
- HU-004: Registra compras como EGRESO
- HU-006: Registra nómina como EGRESO
- HU-013: Base para cierre de caja

### 📈 Resumen Incluye
- Total de ingresos
- Total de egresos
- Saldo del periodo
- Cantidad de movimientos
- Desglose de ventas
- Desglose de compras
- Desglose de nómina
- Otros egresos

---

## [0.8.0] - HU-006: Cálculo y Registro de Nómina Diaria

### ✅ Agregado
- **Sistema completo de nómina diaria**
- **Registro de asistencia** por fecha
- **Cálculo automático** de pagos según salario
- **Marcar pagado/pendiente** individual o múltiple
- **Reportes de nómina** por día, periodo y empleado
- **Modelo**: NominaDiaria con estados
- **Servicio**: NominaDiariaService con cálculos
- **Endpoints REST** completos

### 📊 Funcionalidades
- Registrar quién trabajó cada día
- Cálculo automático según salario diario
- Marcar como pagado/pendiente
- Consultar nómina por fecha
- Consultar nómina por periodo
- Consultar nómina por empleado
- Calcular totales de día y periodo
- Listar pagos pendientes
- Eliminar registros erróneos

### 🔧 Backend
- Endpoint: GET /api/nomina/fecha/{fecha}
- Endpoint: GET /api/nomina/periodo?inicio=&fin=
- Endpoint: GET /api/nomina/empleado/{id}?inicio=&fin=
- Endpoint: GET /api/nomina/pendientes
- Endpoint: GET /api/nomina/total/dia/{fecha}
- Endpoint: GET /api/nomina/total/periodo?inicio=&fin=
- Endpoint: POST /api/nomina/registrar
- Endpoint: PATCH /api/nomina/{id}/pagar
- Endpoint: PATCH /api/nomina/pagar-multiples
- Endpoint: DELETE /api/nomina/{id}

### 💰 Cálculos Automáticos
- Monto = salario_diario del empleado
- Total día = suma de todos los registros
- Total periodo = suma entre fechas
- Previene duplicados (empleado + fecha únicos)

### 🔗 Integración
- HU-005: Usa empleados y salarios configurados
- HU-007: Preparado para contabilidad (egresos)
- Reportes descargables preparados

---

## [0.7.0] - HU-005: Gestión de Empleados

### ✅ Agregado
- **Sistema completo de gestión de empleados**
- **CRUD completo** con interfaz táctil
- **Validación de documentos** únicos
- **Gestión de puestos** y salarios diarios
- **Activar/desactivar** empleados (no elimina)
- **Modelo**: Empleado con datos completos
- **Servicio**: EmpleadoService con validaciones
- **Endpoints REST** completos

### 📊 Funcionalidades
- Crear empleados con datos básicos
- Editar información de empleados
- Definir salario diario
- Asignar puesto de trabajo
- Registrar fecha de ingreso
- Activar/desactivar empleados
- Validación de documento único
- Consultar por documento

### 🔧 Backend
- Endpoint: GET /api/empleados (todos)
- Endpoint: GET /api/empleados/activos
- Endpoint: GET /api/empleados/{id}
- Endpoint: GET /api/empleados/documento/{doc}
- Endpoint: POST /api/empleados (crear)
- Endpoint: PUT /api/empleados/{id} (actualizar)
- Endpoint: PATCH /api/empleados/{id}/estado
- Endpoint: DELETE /api/empleados/{id}

### 📦 Datos de Ejemplo
- 3 empleados precargados
- Puestos: Cocinera, Ayudante, Mesera
- Salarios diarios configurados

### 🔗 Preparado para
- HU-006: Nómina diaria (base de empleados lista)
- Reportes de nómina
- Control de asistencia

---

## [0.6.0] - HU-004: Registro de Compras y Ajustes de Inventario

### ✅ Agregado
- **Sistema completo de compras** a proveedores
- **Gestión de proveedores** (CRUD completo)
- **Actualización automática de inventario** al registrar compra
- **Registro de movimientos** tipo ENTRADA
- **Modelos**: Compra, CompraDetalle, Proveedor
- **Servicios**: CompraService, ProveedorService
- **Endpoints REST** completos

### 🔄 Integración
- CompraService actualiza inventario automáticamente
- Movimientos registrados con referencia a compra
- Ajustes manuales de inventario disponibles
- Trazabilidad completa de entradas

### 📊 Funcionalidades
- Registrar compras con múltiples insumos
- Gestionar proveedores (crear, editar, activar/desactivar)
- Actualización automática de stock
- Consultar compras por fecha
- Historial de compras por proveedor
- Ajustes manuales con motivo

### 🔧 Backend
- Endpoint: GET /api/proveedores
- Endpoint: POST /api/proveedores (crear)
- Endpoint: PUT /api/proveedores/{id} (actualizar)
- Endpoint: PATCH /api/proveedores/{id}/estado
- Endpoint: GET /api/compras
- Endpoint: POST /api/compras (registrar)
- Endpoint: GET /api/compras/fecha (por rango)

### 📦 Datos de Ejemplo
- 2 proveedores precargados
- Estructura lista para registrar compras

### 🔗 Integración
- HU-003: Actualiza inventario automáticamente
- HU-007: Preparado para contabilidad (egresos)

---

## [0.5.0] - HU-003: Control de Inventario con Consumo Automático

### ✅ Agregado
- **Sistema completo de inventario** con gestión de insumos
- **Descuento automático** de inventario al registrar ventas
- **Recetas** (asociación platos-insumos)
- **Alertas de bajo stock** automáticas
- **Trazabilidad completa** con movimientos de inventario
- **Modelos**: Insumo, Receta, MovimientoInventario
- **Servicios**: InventarioService, RecetaService
- **Endpoints REST** completos para gestión

### 🔄 Integración
- VentaService actualizado con descuento automático
- Al registrar venta se descuentan insumos según receta
- Movimientos registrados con referencia a venta
- Permite stock negativo con alerta (no bloquea venta)

### 📊 Funcionalidades
- Crear/editar insumos
- Definir stock mínimo y alertas
- Ajustar cantidades manualmente
- Asociar insumos a platos (recetas)
- Consultar insumos bajo stock
- Historial de movimientos
- Múltiples unidades de medida

### 🔧 Backend
- Endpoint: GET /api/insumos (todos)
- Endpoint: GET /api/insumos/activos
- Endpoint: GET /api/insumos/bajo-stock
- Endpoint: POST /api/insumos (crear)
- Endpoint: PUT /api/insumos/{id} (actualizar)
- Endpoint: PATCH /api/insumos/{id}/ajustar (ajustar stock)
- Endpoint: GET /api/recetas/plato/{id}
- Endpoint: POST /api/recetas/plato/{id}
- Endpoint: DELETE /api/recetas/plato/{id}/insumo/{id}

### 📦 Datos de Ejemplo
- 8 insumos precargados (huevos, frijoles, arroz, etc.)
- Unidades de medida: UNIDAD, LIBRA, LITRO, etc.

### 🔜 Preparado para
- HU-004: Registro de compras (movimientos ENTRADA)
- HU-007: Contabilidad (valor de inventario)
- Reportes de consumo y costos

---

## [0.4.0] - HU-002: Gestión de Menú y Precios de Platos

### ✅ Agregado
- **Gestión completa de menú** con interfaz táctil
- **CRUD de platos**: Crear, editar, activar/desactivar
- **Filtros táctiles** por categoría y estado
- **Modal de plato** con teclado numérico integrado
- **Selector visual de categorías** con iconos
- **Tarjetas de plato** con información completa
- **Backend completo** para gestión de platos
- Componentes: GestionMenuPage, TarjetaPlato, ModalPlato, FiltrosMenu
- Estilos táctiles optimizados (menu.css)

### 🎨 UI/UX
- Tarjetas grandes con información clara
- Botones de acción táctiles (min 50px)
- Selector de categorías con iconos visuales
- Teclado numérico para ingreso de precios
- Filtros intuitivos con feedback visual
- Estados visuales (activo/inactivo)

### 🔧 Backend
- Endpoint: GET /api/platos (todos los platos)
- Endpoint: POST /api/platos (crear plato)
- Endpoint: PUT /api/platos/{id} (actualizar plato)
- Endpoint: PATCH /api/platos/{id}/estado (cambiar estado)
- Endpoint: DELETE /api/platos/{id} (eliminar plato)
- Servicio: PlatoService con lógica de negocio
- DTO: PlatoDTO para transferencia de datos

### 📱 Funcionalidades
- Crear platos con nombre, descripción, precio y categoría
- Editar platos existentes
- Activar/desactivar platos (no se eliminan)
- Filtrar por categoría (Desayuno, Almuerzo, Bebida, Postre)
- Filtrar por estado (Activos, Inactivos, Todos)
- Contador de resultados
- Validaciones de campos obligatorios

### 🔗 Integración
- Platos activos aparecen automáticamente en el POS
- Cambios de precio se reflejan inmediatamente
- Platos inactivos no se muestran en ventas

### 🔜 Preparado para
- HU-003: Asociación de insumos a platos (recetas)
- Imágenes de platos
- Historial de cambios de precios

---

## [0.3.0] - HU-001: Registro Táctil de Ventas (POS)

### ✅ Agregado
- **Sistema POS completo** con interfaz táctil
- **Grid de productos** con filtros por categoría
- **Carrito de compra** con gestión de cantidades
- **Selección de método de pago** (Efectivo, Tarjeta, Transferencia)
- **Generación de tickets** con vista previa e impresión
- **Backend de ventas** con endpoints completos
- **Modelo de datos** para ventas y detalles
- Componentes: CarritoVenta, ModalMetodoPago, ModalTicket
- Estilos táctiles optimizados (pos.css)

### 🎨 UI/UX
- Botones grandes táctiles (min 60px)
- Categorías con iconos visuales
- Feedback visual en todas las interacciones
- Modal de confirmación de pago
- Ticket formateado para impresión térmica
- Diseño responsive (desktop y mobile)

### 🔧 Backend
- Endpoint: GET /api/platos/activos
- Endpoint: POST /api/ventas (con detalles)
- Endpoint: GET /api/ventas/dia
- Modelos: Venta, VentaDetalle, Plato
- Mappers y servicios completos
- Validaciones de negocio

### 📱 Funcionalidades
- Agregar productos al carrito
- Ajustar cantidades (incrementar/decrementar)
- Eliminar productos del carrito
- Cálculo automático de totales
- Registro de cajero automático
- Generación de ticket con todos los detalles
- Opción de impresión

### 🔜 Preparado para
- HU-003: Descuento automático de inventario
- HU-010: Integración con impresora térmica
- HU-014: Manejo de propinas

---

## [0.2.0] - HU-016: Control de Permisos y Menús por Rol

### ✅ Agregado
- **AuthProvider**: Context global de autenticación
- **ProtectedRoute**: Guard para proteger rutas según roles
- **DashboardDuenoPage**: Dashboard con indicadores y accesos rápidos
- **UnauthorizedPage**: Página de acceso no autorizado
- Layouts mejorados con información del usuario
- Menú diferenciado por rol (Cajero vs Dueño)
- Menú Rápido para el Dueño con indicadores básicos
- Menú Avanzado para el Dueño con todas las funcionalidades
- Estilos táctiles mejorados (dashboard.css)
- Botones de cerrar sesión en todos los layouts

### 🔒 Seguridad
- Rutas protegidas con validación de roles
- Redirección automática según permisos
- Validación de autenticación en cada ruta

### 🎨 UI/UX
- Dashboard con tarjetas de indicadores visuales
- Accesos rápidos con iconos grandes
- Alertas y notificaciones
- Navegación intuitiva entre menú rápido y avanzado
- Colores diferenciados por rol (verde para cajero, morado para dueño)

### 📱 Responsive
- Grid adaptable para indicadores
- Menús táctiles optimizados
- Diseño mobile-friendly

---

## [0.1.0] - HU-015: Login de Dueño y Cajero

### ✅ Agregado
- Sistema de autenticación con JWT
- Pantalla de login táctil e intuitiva
- Backend con Spring Security
- Endpoints: `/api/auth/login`, `/api/auth/logout`, `/api/auth/me`
- Contraseñas hasheadas con BCrypt
- Registro de sesiones en base de datos
- Scripts SQL completos para base de datos
- Usuarios iniciales: admin, dueno, cajero

### 🗄️ Base de Datos
- Script `00_create_database.sql`: Crea BD contabilidadRestaurante
- Script `01_create_tables.sql`: Todas las tablas del sistema
- Script `02_insert_initial_data.sql`: Datos iniciales
- Script `03_password_generator.sql`: Info de contraseñas

### 📚 Documentación
- README.md principal
- INSTRUCCIONES_INICIO.md
- database/README.md
- Documentación de módulo de autenticación

---

## Próximas Funcionalidades

### En Desarrollo
- HU-001: Registro táctil de ventas (POS)
- HU-002: Gestión de menú y precios

### Planificadas
- HU-003: Control de inventario con consumo automático
- HU-005: Gestión de empleados
- HU-006: Nómina diaria
- HU-007: Contabilidad básica de caja
- HU-013: Cierre de caja diario

---

## Notas de Versión

### Versión 0.2.0
Sistema de permisos completamente funcional. El Dueño tiene acceso a un dashboard con indicadores y puede navegar entre menú rápido y avanzado. El Cajero solo tiene acceso a la pantalla de ventas.

### Versión 0.1.0
Sistema de autenticación base implementado. Login funcional con redirección según rol.
