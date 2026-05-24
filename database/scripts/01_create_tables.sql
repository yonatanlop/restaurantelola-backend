-- Script de creación de base de datos y tablas para Restaurante Doña Lola
-- Base de datos: contabilidadRestaurante

-- Crear la base de datos (ejecutar como superusuario)
-- DROP DATABASE IF EXISTS "contabilidadRestaurante";
-- CREATE DATABASE "contabilidadRestaurante"
--     WITH 
--     OWNER = postgres
--     ENCODING = 'UTF8'
--     LC_COLLATE = 'Spanish_Honduras.1252'
--     LC_CTYPE = 'Spanish_Honduras.1252'
--     TABLESPACE = pg_default
--     CONNECTION LIMIT = -1;

-- Conectarse a la base de datos antes de ejecutar el resto del script
-- \c contabilidadRestaurante

-- Tabla de usuarios (dueño y cajeros)
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    usuario VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('DUENO', 'CAJERO', 'ADMIN')),
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de sesiones (para auditoría de login)
CREATE TABLE IF NOT EXISTS sesiones (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    fecha_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_logout TIMESTAMP,
    ip_address VARCHAR(50),
    CONSTRAINT fk_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Tabla de categorías de platos
CREATE TABLE IF NOT EXISTS categorias (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion TEXT,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de platos/productos
CREATE TABLE IF NOT EXISTS platos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    categoria_id BIGINT REFERENCES categorias(id),
    precio DECIMAL(10, 2) NOT NULL,
    tipo_comida VARCHAR(20) CHECK (tipo_comida IN ('DESAYUNO', 'ALMUERZO', 'BEBIDA', 'POSTRE')),
    activo BOOLEAN DEFAULT TRUE,
    imagen_url VARCHAR(255),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de insumos
CREATE TABLE IF NOT EXISTS insumos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    unidad_medida VARCHAR(20) NOT NULL,
    cantidad_actual DECIMAL(10, 3) DEFAULT 0,
    cantidad_minima DECIMAL(10, 3) DEFAULT 0,
    precio_unitario DECIMAL(10, 2) DEFAULT 0,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de recetas (relación platos-insumos)
CREATE TABLE IF NOT EXISTS recetas (
    id BIGSERIAL PRIMARY KEY,
    plato_id BIGINT NOT NULL REFERENCES platos(id) ON DELETE CASCADE,
    insumo_id BIGINT NOT NULL REFERENCES insumos(id) ON DELETE CASCADE,
    cantidad_necesaria DECIMAL(10, 3) NOT NULL,
    UNIQUE(plato_id, insumo_id)
);

-- Tabla de ventas
CREATE TABLE IF NOT EXISTS ventas (
    id BIGSERIAL PRIMARY KEY,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    subtotal DECIMAL(10, 2) NOT NULL,
    impuestos DECIMAL(10, 2) DEFAULT 0,
    propina DECIMAL(10, 2) DEFAULT 0,
    total DECIMAL(10, 2) NOT NULL,
    metodo_pago VARCHAR(20) NOT NULL CHECK (metodo_pago IN ('EFECTIVO', 'TARJETA', 'TRANSFERENCIA')),
    estado VARCHAR(20) DEFAULT 'COMPLETADA' CHECK (estado IN ('COMPLETADA', 'CANCELADA')),
    notas TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de detalles de venta
CREATE TABLE IF NOT EXISTS venta_detalles (
    id BIGSERIAL PRIMARY KEY,
    venta_id BIGINT NOT NULL REFERENCES ventas(id) ON DELETE CASCADE,
    plato_id BIGINT NOT NULL REFERENCES platos(id),
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    notas TEXT
);

-- Tabla de empleados
CREATE TABLE IF NOT EXISTS empleados (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    documento VARCHAR(50) UNIQUE,
    telefono VARCHAR(20),
    direccion TEXT,
    puesto VARCHAR(50),
    salario_diario DECIMAL(10, 2) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_ingreso DATE NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de nómina diaria
CREATE TABLE IF NOT EXISTS nomina_diaria (
    id BIGSERIAL PRIMARY KEY,
    empleado_id BIGINT NOT NULL REFERENCES empleados(id),
    fecha DATE NOT NULL,
    monto DECIMAL(10, 2) NOT NULL,
    estado VARCHAR(20) DEFAULT 'PENDIENTE' CHECK (estado IN ('PENDIENTE', 'PAGADO')),
    fecha_pago TIMESTAMP,
    notas TEXT,
    registrado_por BIGINT REFERENCES usuarios(id),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(empleado_id, fecha)
);

-- Tabla de proveedores
CREATE TABLE IF NOT EXISTS proveedores (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    contacto VARCHAR(100),
    telefono VARCHAR(20),
    email VARCHAR(100),
    direccion TEXT,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de compras
CREATE TABLE IF NOT EXISTS compras (
    id BIGSERIAL PRIMARY KEY,
    proveedor_id BIGINT REFERENCES proveedores(id),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10, 2) NOT NULL,
    estado VARCHAR(20) DEFAULT 'COMPLETADA' CHECK (estado IN ('COMPLETADA', 'CANCELADA')),
    notas TEXT,
    registrado_por BIGINT REFERENCES usuarios(id),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de detalles de compra
CREATE TABLE IF NOT EXISTS compra_detalles (
    id BIGSERIAL PRIMARY KEY,
    compra_id BIGINT NOT NULL REFERENCES compras(id) ON DELETE CASCADE,
    insumo_id BIGINT NOT NULL REFERENCES insumos(id),
    cantidad DECIMAL(10, 3) NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL
);

-- Tabla de movimientos de inventario
CREATE TABLE IF NOT EXISTS movimientos_inventario (
    id BIGSERIAL PRIMARY KEY,
    insumo_id BIGINT NOT NULL REFERENCES insumos(id),
    tipo_movimiento VARCHAR(20) NOT NULL CHECK (tipo_movimiento IN ('ENTRADA', 'SALIDA', 'AJUSTE')),
    cantidad DECIMAL(10, 3) NOT NULL,
    cantidad_anterior DECIMAL(10, 3) NOT NULL,
    cantidad_nueva DECIMAL(10, 3) NOT NULL,
    motivo VARCHAR(50),
    referencia_id BIGINT,
    referencia_tipo VARCHAR(50),
    usuario_id BIGINT REFERENCES usuarios(id),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notas TEXT
);

-- Tabla de cierres de caja
CREATE TABLE IF NOT EXISTS cierres_caja (
    id BIGSERIAL PRIMARY KEY,
    fecha DATE NOT NULL UNIQUE,
    efectivo_esperado DECIMAL(10, 2) NOT NULL,
    efectivo_contado DECIMAL(10, 2) NOT NULL,
    diferencia DECIMAL(10, 2) NOT NULL,
    total_ventas DECIMAL(10, 2) NOT NULL,
    total_egresos DECIMAL(10, 2) NOT NULL,
    notas TEXT,
    cerrado_por BIGINT NOT NULL REFERENCES usuarios(id),
    fecha_cierre TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de movimientos de caja (contabilidad)
CREATE TABLE IF NOT EXISTS movimientos_caja (
    id BIGSERIAL PRIMARY KEY,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('INGRESO', 'EGRESO')),
    concepto VARCHAR(50) NOT NULL,
    descripcion TEXT,
    monto DECIMAL(10, 2) NOT NULL,
    referencia_id BIGINT,
    referencia_tipo VARCHAR(50),
    usuario_id BIGINT REFERENCES usuarios(id)
);

-- Tabla de auditoría
CREATE TABLE IF NOT EXISTS auditoria (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT REFERENCES usuarios(id),
    accion VARCHAR(50) NOT NULL,
    modulo VARCHAR(50) NOT NULL,
    descripcion TEXT,
    datos_anteriores JSONB,
    datos_nuevos JSONB,
    ip_address VARCHAR(50),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para mejorar rendimiento
CREATE INDEX idx_ventas_fecha ON ventas(fecha);
CREATE INDEX idx_ventas_usuario ON ventas(usuario_id);
CREATE INDEX idx_venta_detalles_venta ON venta_detalles(venta_id);
CREATE INDEX idx_nomina_fecha ON nomina_diaria(fecha);
CREATE INDEX idx_nomina_empleado ON nomina_diaria(empleado_id);
CREATE INDEX idx_movimientos_insumo ON movimientos_inventario(insumo_id);
CREATE INDEX idx_movimientos_fecha ON movimientos_inventario(fecha);
CREATE INDEX idx_auditoria_fecha ON auditoria(fecha);
CREATE INDEX idx_auditoria_usuario ON auditoria(usuario_id);
CREATE INDEX idx_sesiones_usuario ON sesiones(usuario_id);
CREATE INDEX idx_movimientos_caja_fecha ON movimientos_caja(fecha);
CREATE INDEX idx_movimientos_caja_tipo ON movimientos_caja(tipo);
CREATE INDEX idx_movimientos_caja_concepto ON movimientos_caja(concepto);
