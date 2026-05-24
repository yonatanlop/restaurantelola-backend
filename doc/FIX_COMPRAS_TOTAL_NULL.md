# Fix: Error al Registrar Compra - Total NULL

## Problema
Al intentar registrar una nueva compra, aparecía el siguiente error:
```
ERROR: null value in column "total" of relation "compras" violates not-null constraint
```

## Causa
El servicio de compras estaba estableciendo el total directamente desde el DTO:
```java
compra.setTotal(dto.getTotal());
```

Si el frontend no enviaba el total calculado (o lo enviaba como NULL), la base de datos rechazaba la inserción porque la columna `total` es NOT NULL.

## Solución Implementada

Se modificó el método `registrarCompra()` en `CompraService.java` para:

1. **Calcular automáticamente el total** sumando los subtotales de todos los detalles
2. **Calcular subtotales** si no vienen en el DTO (precio × cantidad)
3. **Usar el total del DTO** solo si existe, sino usar el calculado

### Código Corregido

```java
@Transactional
public CompraDTO registrarCompra(CompraDTO dto) {
    Compra compra = new Compra();
    compra.setProveedorId(dto.getProveedorId());
    compra.setNotas(dto.getNotas());
    compra.setRegistradoPor(dto.getRegistradoPor());
    
    // Agregar detalles y calcular total
    BigDecimal totalCalculado = BigDecimal.ZERO;
    if (dto.getDetalles() != null) {
        for (CompraDetalleDTO detalleDTO : dto.getDetalles()) {
            CompraDetalle detalle = new CompraDetalle();
            detalle.setInsumoId(detalleDTO.getInsumoId());
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setPrecioUnitario(detalleDTO.getPrecioUnitario());
            
            // Calcular subtotal si no viene en el DTO
            BigDecimal subtotal = detalleDTO.getSubtotal();
            if (subtotal == null && detalleDTO.getPrecioUnitario() != null 
                && detalleDTO.getCantidad() != null) {
                subtotal = detalleDTO.getPrecioUnitario()
                    .multiply(detalleDTO.getCantidad());
            }
            if (subtotal == null) {
                subtotal = BigDecimal.ZERO;
            }
            detalle.setSubtotal(subtotal);
            
            compra.addDetalle(detalle);
            totalCalculado = totalCalculado.add(subtotal);
        }
    }
    
    // Establecer el total calculado (o el del DTO si existe)
    compra.setTotal(dto.getTotal() != null ? dto.getTotal() : totalCalculado);
    
    Compra guardada = compraRepository.save(compra);
    // ... resto del código
}
```

## Beneficios

✅ **Robusto:** El total siempre se calcula, incluso si el frontend no lo envía
✅ **Flexible:** Acepta el total del frontend si existe, sino lo calcula
✅ **Seguro:** Nunca intenta insertar NULL en la columna total
✅ **Automático:** Calcula subtotales si no vienen en el DTO

## Archivos Modificados

- `src/main/java/com/tialola/compras/service/CompraService.java`
  - Método `registrarCompra()` actualizado
  - Cálculo automático de total y subtotales

## Prueba

Para probar que funciona:

1. Ve a **Compras > Registrar Compra**
2. Selecciona un proveedor
3. Agrega uno o más insumos con cantidad y precio
4. Haz clic en **Registrar Compra**
5. La compra debe registrarse exitosamente sin errores

## Fecha de Corrección
30 de noviembre de 2025
