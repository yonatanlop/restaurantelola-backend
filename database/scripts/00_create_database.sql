-- Script para crear la base de datos contabilidadRestaurante
-- Ejecutar este script como superusuario de PostgreSQL (postgres)

-- Eliminar la base de datos si existe (CUIDADO: esto borra todos los datos)
-- DROP DATABASE IF EXISTS "contabilidadRestaurante";

-- Crear la base de datos
CREATE DATABASE "contabilidadRestaurante"
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'Spanish_Honduras.1252'
    LC_CTYPE = 'Spanish_Honduras.1252'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

-- Comentario de la base de datos
COMMENT ON DATABASE "contabilidadRestaurante" 
    IS 'Base de datos para el sistema de contabilidad del Restaurante Doña Lola';

-- Conectarse a la base de datos
\c "contabilidadRestaurante"

-- Mensaje de confirmación
SELECT 'Base de datos contabilidadRestaurante creada exitosamente' AS mensaje;
