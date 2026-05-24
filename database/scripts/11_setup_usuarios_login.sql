-- Script para configurar usuarios para el nuevo sistema de login
-- Ejecutar este script después de actualizar la aplicación

-- ============================================
-- 1. Verificar usuarios existentes
-- ============================================

SELECT 
    id, 
    nombre, 
    usuario, 
    rol, 
    activo,
    fecha_creacion
FROM usuarios
ORDER BY rol, id;

-- ============================================
-- 2. Crear usuario CAJERO si no existe
-- ============================================

-- Verificar si existe un cajero activo
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM usuarios WHERE rol = 'CAJERO' AND activo = true) THEN
        -- Crear usuario cajero
        -- Nota: La contraseña no se usa para el login del cajero, pero se requiere en la BD
        INSERT INTO usuarios (nombre, usuario, password, rol, activo, fecha_creacion, fecha_modificacion)
        VALUES (
            'Cajero Principal', 
            'cajero', 
            '$2a$10$dummyHashNotUsedForCajero123456789012345678901234567890', 
            'CAJERO', 
            true, 
            NOW(), 
            NOW()
        );
        
        RAISE NOTICE 'Usuario CAJERO creado exitosamente';
    ELSE
        RAISE NOTICE 'Ya existe un usuario CAJERO activo';
    END IF;
END $$;

-- ============================================
-- 3. Crear usuario DUENO si no existe
-- ============================================

-- Verificar si existe un dueño activo
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM usuarios WHERE rol = 'DUENO' AND activo = true) THEN
        -- Crear usuario dueño
        -- Contraseña por defecto: "admin123"
        -- Hash BCrypt: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
        INSERT INTO usuarios (nombre, usuario, password, rol, activo, fecha_creacion, fecha_modificacion)
        VALUES (
            'Dueño', 
            'dueno', 
            '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
            'DUENO', 
            true, 
            NOW(), 
            NOW()
        );
        
        RAISE NOTICE 'Usuario DUENO creado exitosamente con contraseña: admin123';
        RAISE NOTICE 'IMPORTANTE: Cambia esta contraseña después del primer login';
    ELSE
        RAISE NOTICE 'Ya existe un usuario DUENO activo';
    END IF;
END $$;

-- ============================================
-- 4. Verificar resultado
-- ============================================

SELECT 
    id, 
    nombre, 
    usuario, 
    rol, 
    activo,
    CASE 
        WHEN rol = 'CAJERO' THEN 'Login sin contraseña'
        WHEN rol = 'DUENO' THEN 'Login con contraseña'
        ELSE 'Login estándar'
    END as tipo_acceso
FROM usuarios
WHERE activo = true
ORDER BY rol, id;

-- ============================================
-- 5. Información adicional
-- ============================================

-- Mostrar resumen
SELECT 
    rol,
    COUNT(*) as cantidad,
    STRING_AGG(nombre, ', ') as usuarios
FROM usuarios
WHERE activo = true
GROUP BY rol
ORDER BY rol;

-- ============================================
-- NOTAS IMPORTANTES:
-- ============================================
-- 
-- 1. CAJERO:
--    - Login sin contraseña (acceso directo)
--    - Solo puede acceder a /cajero/venta
--    - No necesita recordar contraseña
--
-- 2. DUENO:
--    - Login con contraseña (seguro)
--    - Acceso completo al sistema
--    - Contraseña por defecto: "admin123"
--    - CAMBIAR CONTRASEÑA después del primer login
--
-- 3. Para cambiar la contraseña del dueño:
--    - Usar la interfaz de configuración del sistema
--    - O ejecutar: UPDATE usuarios SET password = '$2a$10$NUEVO_HASH' WHERE rol = 'DUENO';
--
-- 4. Para generar un nuevo hash BCrypt:
--    - Usar: https://bcrypt-generator.com/
--    - O en Java: new BCryptPasswordEncoder().encode("nuevaContraseña")
--
-- ============================================

-- Fin del script
