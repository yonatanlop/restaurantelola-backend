-- Script para corregir la columna cerrado_por en cierres_caja
-- La columna cerrado_por ya no se usa, ahora se usa usuario_id
-- Este script hace que cerrado_por sea nullable para evitar errores

-- Hacer que cerrado_por sea nullable
ALTER TABLE cierres_caja 
ALTER COLUMN cerrado_por DROP NOT NULL;

-- Actualizar registros existentes que tengan cerrado_por NULL
-- usando el valor de usuario_id
UPDATE cierres_caja 
SET cerrado_por = usuario_id 
WHERE cerrado_por IS NULL AND usuario_id IS NOT NULL;

-- Comentario: En el futuro se puede eliminar la columna cerrado_por
-- si ya no se necesita, pero por ahora la dejamos nullable
-- ALTER TABLE cierres_caja DROP COLUMN IF EXISTS cerrado_por;
