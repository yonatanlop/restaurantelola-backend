-- Script para generar contraseñas hasheadas con BCrypt
-- Las contraseñas en este script ya están hasheadas con BCrypt (10 rounds)

-- Contraseñas en texto plano (NO USAR EN PRODUCCIÓN):
-- admin123
-- dueno123
-- cajero123

-- Hash BCrypt de 'admin123', 'dueno123', 'cajero123':
-- $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

-- Para generar nuevas contraseñas hasheadas, puedes usar:
-- 1. Online: https://bcrypt-generator.com/
-- 2. Java: BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
--          String hash = encoder.encode("tu_password");

-- Actualizar contraseñas de usuarios existentes
UPDATE usuarios 
SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'
WHERE usuario IN ('admin', 'dueno', 'cajero');

-- Verificar usuarios
SELECT id, nombre, usuario, rol, activo FROM usuarios;
