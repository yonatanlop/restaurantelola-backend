-- HU-013: Cierre de caja diario
ALTER TABLE cierres_caja
    ADD COLUMN IF NOT EXISTS usuario_id BIGINT REFERENCES usuarios(id),
    ADD COLUMN IF NOT EXISTS usuario_nombre VARCHAR(100),
    ADD COLUMN IF NOT EXISTS saldo_inicial DECIMAL(10,2) DEFAULT 0,
    ADD COLUMN IF NOT EXISTS total_ingresos DECIMAL(10,2) DEFAULT 0,
    ADD COLUMN IF NOT EXISTS total_egresos DECIMAL(10,2) DEFAULT 0,
    ADD COLUMN IF NOT EXISTS saldo_esperado DECIMAL(10,2) DEFAULT 0,
    ADD COLUMN IF NOT EXISTS tarjetas DECIMAL(10,2) DEFAULT 0,
    ADD COLUMN IF NOT EXISTS transferencias DECIMAL(10,2) DEFAULT 0,
    ADD COLUMN IF NOT EXISTS otros_medios DECIMAL(10,2) DEFAULT 0,
    ADD COLUMN IF NOT EXISTS total_contado DECIMAL(10,2) DEFAULT 0,
    ADD COLUMN IF NOT EXISTS observaciones TEXT,
    ADD COLUMN IF NOT EXISTS estado VARCHAR(20) DEFAULT 'ABIERTO',
    ADD COLUMN IF NOT EXISTS fecha_apertura TIMESTAMP,
    ADD COLUMN IF NOT EXISTS fecha_cierre TIMESTAMP;

CREATE TABLE IF NOT EXISTS detalle_arqueo (
    id BIGSERIAL PRIMARY KEY,
    cierre_id BIGINT NOT NULL REFERENCES cierres_caja(id) ON DELETE CASCADE,
    denominacion VARCHAR(50) NOT NULL,
    cantidad INT NOT NULL DEFAULT 0,
    subtotal DECIMAL(10,2) NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_detalle_arqueo_cierre ON detalle_arqueo(cierre_id);

-- HU-014: Estructura inicial para propinas por venta
CREATE TABLE IF NOT EXISTS propinas_venta (
    id BIGSERIAL PRIMARY KEY,
    venta_id BIGINT NOT NULL UNIQUE REFERENCES ventas(id) ON DELETE CASCADE,
    monto_sugerido DECIMAL(10,2) DEFAULT 0,
    monto_registrado DECIMAL(10,2) DEFAULT 0,
    porcentaje_sugerido DECIMAL(5,2) DEFAULT 0,
    estado VARCHAR(20) DEFAULT 'DESHABILITADA',
    metodo_registro VARCHAR(30),
    notas TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_propinas_venta_estado ON propinas_venta(estado);

