# Denominaciones de Arqueo de Caja

## Cambio Implementado

Se han actualizado las denominaciones del arqueo de caja para reflejar los billetes y monedas de Honduras (Lempiras).

## Denominaciones Actuales

### Billetes
1. **$100,000** - Cien mil lempiras
2. **$50,000** - Cincuenta mil lempiras
3. **$20,000** - Veinte mil lempiras
4. **$10,000** - Diez mil lempiras
5. **$5,000** - Cinco mil lempiras
6. **$2,000** - Dos mil lempiras
7. **$1,000** - Mil lempiras
8. **$500** - Quinientos lempiras

### Monedas
9. **$200** - Doscientos lempiras
10. **$100** - Cien lempiras
11. **$50** - Cincuenta lempiras

## Formato de Visualización

- ✅ Todas las denominaciones se muestran con el símbolo **$**
- ✅ Los valores grandes incluyen **separadores de miles** (comas)
- ✅ Formato consistente con el resto del sistema

## Ejemplo de Uso

Al hacer el arqueo de caja, el usuario ingresa la cantidad de cada denominación:

| Denominación | Cantidad | Subtotal |
|--------------|----------|----------|
| $100,000 | 5 | $500,000 |
| $50,000 | 10 | $500,000 |
| $20,000 | 15 | $300,000 |
| $10,000 | 20 | $200,000 |
| $5,000 | 30 | $150,000 |
| $2,000 | 25 | $50,000 |
| $1,000 | 40 | $40,000 |
| $500 | 50 | $25,000 |
| $200 | 30 | $6,000 |
| $100 | 20 | $2,000 |
| $50 | 10 | $500 |
| **TOTAL** | | **$1,773,500** |

## Cambios Técnicos

### Antes
```typescript
const DENOMINACIONES = ['200', '100', '50', '20', '10', '5', '2', '1', '0.50', '0.25', '0.10', '0.05']
```

Mostraba: `L 200`, `L 100`, `L 50`, etc.

### Ahora
```typescript
const DENOMINACIONES = ['100000', '50000', '20000', '10000', '5000', '2000', '1000', '500', '200', '100', '50']
```

Muestra: `$100,000`, `$50,000`, `$20,000`, etc.

## Archivos Modificados

- `tialola-frontend/src/modules/contabilidad/pages/CierreCajaPage.tsx`
  - Constante `DENOMINACIONES` actualizada
  - Formato de visualización con `formatearMoneda()`
  - Eliminado prefijo "L" reemplazado por formato de moneda

## Beneficios

✅ **Realista:** Refleja las denominaciones reales de Honduras
✅ **Legible:** Formato con separadores de miles facilita la lectura
✅ **Consistente:** Usa el mismo formato $ que el resto del sistema
✅ **Práctico:** Incluye las denominaciones más comunes en uso

## Fecha de Implementación
30 de noviembre de 2025
