-- Tabla de auditoría
CREATE TABLE IF NOT EXISTS auditoria_logs (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT,
    usuario_nombre VARCHAR(100),
    accion VARCHAR(50) NOT NULL, -- CREATE, UPDATE, DELETE, LOGIN, LOGOUT, AJUSTE, ERROR
    entidad VARCHAR(50) NOT NULL, -- VENTA, PLATO, INSUMO, EMPLEADO, NOMINA, etc.
    entidad_id BIGINT,
    descripcion TEXT,
    datos_anteriores TEXT, -- JSON
    datos_nuevos TEXT, -- JSON
    ip_address VARCHAR(50),
    user_agent VARCHAR(255),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    resultado VARCHAR(20) DEFAULT 'EXITOSO', -- EXITOSO, FALLIDO
    mensaje_error TEXT
);

-- Índices para búsquedas rápidas
CREATE INDEX idx_auditoria_usuario ON auditoria_logs(usuario_id);
CREATE INDEX idx_auditoria_entidad ON auditoria_logs(entidad, entidad_id);
CREATE INDEX idx_auditoria_accion ON auditoria_logs(accion);
CREATE INDEX idx_auditoria_fecha ON auditoria_logs(fecha DESC);
CREATE INDEX idx_auditoria_resultado ON auditoria_logs(resultado);
CREATE INDEX idx_auditoria_fecha_entidad ON auditoria_logs(fecha DESC, entidad);

COMMENT ON TABLE auditoria_logs IS 'HU-012: Registro de auditoría de operaciones críticas';
COMMENT ON COLUMN auditoria_logs.accion IS 'Tipo de acción realizada';
COMMENT ON COLUMN auditoria_logs.entidad IS 'Entidad afectada por la acción';
COMMENT ON COLUMN auditoria_logs.datos_anteriores IS 'Estado anterior en formato JSON';
COMMENT ON COLUMN auditoria_logs.datos_nuevos IS 'Estado nuevo en formato JSON';
