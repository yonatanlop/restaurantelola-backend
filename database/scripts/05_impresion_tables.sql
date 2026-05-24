-- Tabla de configuración de impresoras
CREATE TABLE IF NOT EXISTS configuracion_impresora (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    tipo_impresora VARCHAR(20) NOT NULL, -- TERMICA, MATRICIAL, PDF
    nombre_impresora VARCHAR(200),
    ancho_papel INTEGER DEFAULT 80,
    caracteres_linea INTEGER DEFAULT 42,
    imprimir_logo BOOLEAN DEFAULT FALSE,
    ruta_logo VARCHAR(255),
    texto_encabezado TEXT,
    texto_pie TEXT,
    auto_cortar BOOLEAN DEFAULT TRUE,
    copias_comanda INTEGER DEFAULT 1,
    copias_ticket INTEGER DEFAULT 1,
    activo BOOLEAN DEFAULT TRUE,
    es_predeterminada BOOLEAN DEFAULT FALSE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de cola de impresión
CREATE TABLE IF NOT EXISTS cola_impresion (
    id BIGSERIAL PRIMARY KEY,
    tipo_documento VARCHAR(20) NOT NULL, -- VENTA, COMANDA
    referencia_id BIGINT NOT NULL,
    impresora_id BIGINT REFERENCES configuracion_impresora(id),
    estado VARCHAR(20) DEFAULT 'PENDIENTE', -- PENDIENTE, IMPRIMIENDO, COMPLETADO, ERROR
    intentos INTEGER DEFAULT 0,
    mensaje_error TEXT,
    contenido_ticket TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_impresion TIMESTAMP
);

-- Índices
CREATE INDEX idx_cola_estado ON cola_impresion(estado);
CREATE INDEX idx_cola_tipo_ref ON cola_impresion(tipo_documento, referencia_id);
CREATE INDEX idx_config_predeterminada ON configuracion_impresora(es_predeterminada, activo);

-- Insertar configuración predeterminada
INSERT INTO configuracion_impresora (
    nombre, tipo_impresora, ancho_papel, caracteres_linea,
    texto_encabezado, texto_pie, activo, es_predeterminada
) VALUES (
    'Impresora Predeterminada',
    'PDF',
    80,
    42,
    'RESTAURANTE LOLA\nCalle Principal #123\nTel: (555) 123-4567',
    'Gracias por su preferencia\nwww.restaurantelola.com',
    TRUE,
    TRUE
);

COMMENT ON TABLE configuracion_impresora IS 'HU-010: Configuración de impresoras térmicas';
COMMENT ON TABLE cola_impresion IS 'HU-010: Cola de trabajos de impresión';
