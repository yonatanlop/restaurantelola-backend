-- Tabla de clientes
CREATE TABLE IF NOT EXISTS clientes (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    direccion VARCHAR(200),
    notas TEXT,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de créditos/fiados
CREATE TABLE IF NOT EXISTS creditos (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL REFERENCES clientes(id),
    venta_id BIGINT,
    fecha_pedido TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valor_pedido DECIMAL(10,2) NOT NULL,
    pagado BOOLEAN DEFAULT FALSE,
    fecha_pago TIMESTAMP,
    usuario_registro_id BIGINT,
    usuario_pago_id BIGINT,
    descripcion TEXT,
    notas TEXT
);

-- Índices
CREATE INDEX idx_clientes_nombre ON clientes(nombre);
CREATE INDEX idx_clientes_activo ON clientes(activo);
CREATE INDEX idx_creditos_cliente ON creditos(cliente_id);
CREATE INDEX idx_creditos_pagado ON creditos(pagado);
CREATE INDEX idx_creditos_fecha ON creditos(fecha_pedido DESC);
CREATE INDEX idx_creditos_cliente_pagado ON creditos(cliente_id, pagado);

-- Insertar clientes de ejemplo
INSERT INTO clientes (nombre, telefono, direccion, notas) VALUES
('Juan Pérez', '555-0101', 'Calle Principal #123', 'Cliente frecuente'),
('María García', '555-0102', 'Av. Central #456', 'Prefiere pagar semanal'),
('Carlos López', '555-0103', 'Jr. Comercio #789', NULL),
('Ana Martínez', '555-0104', 'Calle Lima #321', 'Cliente VIP'),
('Pedro Rodríguez', '555-0105', 'Av. Arequipa #654', NULL);

-- Insertar algunos créditos de ejemplo
INSERT INTO creditos (cliente_id, fecha_pedido, valor_pedido, pagado, descripcion) VALUES
(1, NOW() - INTERVAL '3 days', 150.00, FALSE, 'Almuerzo familiar'),
(1, NOW() - INTERVAL '1 day', 85.50, FALSE, 'Cena'),
(2, NOW() - INTERVAL '5 days', 200.00, FALSE, 'Evento corporativo'),
(3, NOW() - INTERVAL '2 days', 120.00, TRUE, 'Almuerzo de negocios'),
(4, NOW(), 95.00, FALSE, 'Cena romántica');

COMMENT ON TABLE clientes IS 'Clientes con crédito del restaurante';
COMMENT ON TABLE creditos IS 'Registro de ventas a crédito/fiado';
COMMENT ON COLUMN creditos.pagado IS 'Indica si el crédito ya fue pagado';
COMMENT ON COLUMN creditos.valor_pedido IS 'Monto total del pedido a crédito';
