-- Tabla de mesas
CREATE TABLE IF NOT EXISTS mesas (
    id BIGSERIAL PRIMARY KEY,
    numero VARCHAR(20) UNIQUE NOT NULL,
    capacidad INTEGER NOT NULL,
    ubicacion VARCHAR(50), -- SALON, TERRAZA, VIP
    estado VARCHAR(20) DEFAULT 'LIBRE', -- LIBRE, OCUPADA, RESERVADA, LIMPIEZA
    venta_actual_id BIGINT,
    hora_ocupacion TIMESTAMP,
    activa BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de comandas
CREATE TABLE IF NOT EXISTS comandas (
    id BIGSERIAL PRIMARY KEY,
    mesa_id BIGINT NOT NULL REFERENCES mesas(id),
    venta_id BIGINT,
    usuario_id BIGINT NOT NULL,
    estado VARCHAR(20) DEFAULT 'PENDIENTE', -- PENDIENTE, EN_PREPARACION, LISTA, SERVIDA, CANCELADA
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_preparacion TIMESTAMP,
    fecha_lista TIMESTAMP,
    fecha_servida TIMESTAMP,
    notas TEXT
);

-- Tabla de detalles de comanda
CREATE TABLE IF NOT EXISTS comanda_detalles (
    id BIGSERIAL PRIMARY KEY,
    comanda_id BIGINT NOT NULL REFERENCES comandas(id) ON DELETE CASCADE,
    plato_id BIGINT NOT NULL,
    cantidad INTEGER NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    estado VARCHAR(20) DEFAULT 'PENDIENTE', -- PENDIENTE, EN_PREPARACION, LISTO, SERVIDO, CANCELADO
    notas TEXT
);

-- Tabla de transferencias de mesa
CREATE TABLE IF NOT EXISTS transferencias_mesa (
    id BIGSERIAL PRIMARY KEY,
    mesa_origen_id BIGINT NOT NULL REFERENCES mesas(id),
    mesa_destino_id BIGINT NOT NULL REFERENCES mesas(id),
    venta_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    motivo TEXT
);

-- Índices
CREATE INDEX idx_mesas_estado ON mesas(estado, activa);
CREATE INDEX idx_mesas_ubicacion ON mesas(ubicacion, activa);
CREATE INDEX idx_comandas_mesa ON comandas(mesa_id);
CREATE INDEX idx_comandas_estado ON comandas(estado);
CREATE INDEX idx_comandas_fecha ON comandas(fecha_creacion);
CREATE INDEX idx_comanda_detalles_comanda ON comanda_detalles(comanda_id);
CREATE INDEX idx_transferencias_fecha ON transferencias_mesa(fecha);

-- Insertar mesas de ejemplo
INSERT INTO mesas (numero, capacidad, ubicacion, estado) VALUES
('M01', 4, 'SALON', 'LIBRE'),
('M02', 4, 'SALON', 'LIBRE'),
('M03', 2, 'SALON', 'LIBRE'),
('M04', 2, 'SALON', 'LIBRE'),
('M05', 6, 'SALON', 'LIBRE'),
('M06', 6, 'SALON', 'LIBRE'),
('M07', 8, 'SALON', 'LIBRE'),
('T01', 4, 'TERRAZA', 'LIBRE'),
('T02', 4, 'TERRAZA', 'LIBRE'),
('T03', 2, 'TERRAZA', 'LIBRE'),
('V01', 6, 'VIP', 'LIBRE'),
('V02', 8, 'VIP', 'LIBRE');

COMMENT ON TABLE mesas IS 'HU-011: Gestión de mesas del restaurante';
COMMENT ON TABLE comandas IS 'HU-011: Comandas de pedidos por mesa';
COMMENT ON TABLE comanda_detalles IS 'HU-011: Detalles de items en comandas';
COMMENT ON TABLE transferencias_mesa IS 'HU-011: Historial de transferencias entre mesas';
