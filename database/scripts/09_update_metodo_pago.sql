-- Script para actualizar el constraint de método de pago en la tabla ventas
-- Agregar 'CREDITO' como método de pago válido

-- Conectarse a la base de datos
-- \c contabilidadRestaurante

-- Eliminar el constraint existente
ALTER TABLE ventas DROP CONSTRAINT IF EXISTS ventas_metodo_pago_check;

-- Crear el nuevo constraint con CREDITO incluido
ALTER TABLE ventas ADD CONSTRAINT ventas_metodo_pago_check 
    CHECK (metodo_pago IN ('EFECTIVO', 'CREDITO', 'TRANSFERENCIA'));

-- Verificar el cambio
SELECT conname, pg_get_constraintdef(oid) 
FROM pg_constraint 
WHERE conrelid = 'ventas'::regclass 
AND conname = 'ventas_metodo_pago_check';

-- Mensaje de confirmación
DO $$
BEGIN
    RAISE NOTICE 'Constraint actualizado exitosamente. Ahora se permite CREDITO como método de pago.';
END $$;
