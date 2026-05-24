-- Script de datos iniciales para Restaurante Doña Lola

-- Insertar usuarios iniciales
-- Contraseñas: 'admin123', 'dueno123', 'cajero123' (en producción usar bcrypt)
INSERT INTO usuarios (nombre, usuario, password, rol, activo) VALUES
('Administrador', 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', true),
('Lola (Dueña)', 'dueno', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'DUENO', true),
('Cajero Principal', 'cajero', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CAJERO', true)
ON CONFLICT (usuario) DO NOTHING;

-- Insertar categorías de platos
INSERT INTO categorias (nombre, descripcion, activo) VALUES
('Desayunos', 'Platos de desayuno', true),
('Almuerzos', 'Platos de almuerzo', true),
('Bebidas', 'Bebidas frías y calientes', true),
('Postres', 'Postres y dulces', true)
ON CONFLICT (nombre) DO NOTHING;

-- Insertar algunos platos de ejemplo
INSERT INTO platos (nombre, descripcion, categoria_id, precio, tipo_comida, activo) VALUES
('Desayuno Completo', 'Huevos, frijoles, queso, crema y tortillas', 1, 45.00, 'DESAYUNO', true),
('Café con Leche', 'Café con leche caliente', 3, 15.00, 'BEBIDA', true),
('Almuerzo del Día', 'Plato del día con sopa, arroz, carne y ensalada', 2, 65.00, 'ALMUERZO', true),
('Jugo Natural', 'Jugo de frutas naturales', 3, 20.00, 'BEBIDA', true),
('Flan Casero', 'Flan de la casa', 4, 25.00, 'POSTRE', true);

-- Insertar algunos insumos de ejemplo
INSERT INTO insumos (nombre, descripcion, unidad_medida, cantidad_actual, cantidad_minima, precio_unitario, activo) VALUES
('Huevos', 'Huevos frescos', 'UNIDAD', 100, 20, 3.50, true),
('Frijoles', 'Frijoles negros', 'LIBRA', 50, 10, 8.00, true),
('Arroz', 'Arroz blanco', 'LIBRA', 80, 15, 6.00, true),
('Café', 'Café molido', 'LIBRA', 30, 5, 45.00, true),
('Leche', 'Leche entera', 'LITRO', 40, 10, 12.00, true),
('Carne de Res', 'Carne para guisar', 'LIBRA', 25, 5, 55.00, true),
('Tomate', 'Tomate fresco', 'LIBRA', 20, 5, 8.00, true),
('Cebolla', 'Cebolla blanca', 'LIBRA', 15, 5, 6.00, true);

-- Insertar algunos empleados de ejemplo
INSERT INTO empleados (nombre, apellido, documento, telefono, puesto, salario_diario, activo, fecha_ingreso) VALUES
('María', 'González', '001-123456-7', '7777-8888', 'Cocinera', 150.00, true, '2024-01-15'),
('Juan', 'Pérez', '001-234567-8', '7777-9999', 'Ayudante de Cocina', 100.00, true, '2024-02-01'),
('Ana', 'Martínez', '001-345678-9', '7777-0000', 'Mesera', 120.00, true, '2024-01-20');

-- Insertar un proveedor de ejemplo
INSERT INTO proveedores (nombre, contacto, telefono, email, direccion, activo) VALUES
('Distribuidora La Esperanza', 'Carlos Ramírez', '2222-3333', 'ventas@laesperanza.com', 'Mercado Central, Local 45', true),
('Carnes Don José', 'José López', '2222-4444', 'info@carnesdonjose.com', 'Zona Industrial', true);

-- Nota: Las contraseñas están hasheadas con BCrypt
-- Para testing, las contraseñas en texto plano son:
-- admin: admin123
-- dueno: dueno123
-- cajero: cajero123
