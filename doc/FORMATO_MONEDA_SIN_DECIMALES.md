# Formato de Moneda Sin Decimales Innecesarios

## Cambio Implementado

Se ha actualizado el formato de visualización de montos en todo el sistema para que no muestre `.00` cuando el valor es un número entero.

## Ejemplos

**Antes:**
- $15500.00
- $7750.00
- $100.00

**Después:**
- $15500
- $7750.50 (mantiene decimales si son necesarios)
- $100

## Función Utilitaria Creada

**Archivo:** `tialola-frontend/src/shared/utils/formatters.ts`

```typescript
export const formatearMoneda = (valor: number): string => {
  // Si el valor tiene decimales significativos, mostrarlos
  if (valor % 1 !== 0) {
    return `$${valor.toFixed(2)}`;
  }
  // Si es un número entero, no mostrar decimales
  return `$${Math.round(valor)}`;
};
```

## Componentes Actualizados

### Dashboard
- ✅ `DashboardPage.tsx` - Todas las métricas de ventas
- ✅ `TarjetaMetrica.tsx` - Tarjetas de indicadores
- ✅ `EstadoCaja.tsx` - Ingresos, egresos y saldo
- ✅ `TopPlatos.tsx` - Total de ventas por plato

### Créditos
- ✅ `CreditosPage.tsx` - Deuda total general
- ✅ `ListaClientes.tsx` - Deuda por cliente
- ✅ `DetalleCreditos.tsx` - Valores de créditos individuales

## Ubicaciones Afectadas

1. **Dashboard de Indicadores**
   - Total de ventas (hoy, semana, mes)
   - Ticket promedio
   - Estado de caja (ingresos, egresos, saldo)
   - Top 5 platos más vendidos

2. **Sistema de Créditos**
   - Deuda total general
   - Deuda por cliente
   - Valor de cada crédito
   - Resumen de créditos pendientes

3. **Variaciones de Porcentaje**
   - Reducido de 2 decimales a 1 decimal
   - Ejemplo: +100.00% → +100.0%

## Beneficios

✅ **Interfaz más limpia** - Números más fáciles de leer
✅ **Menos ruido visual** - Elimina decimales innecesarios
✅ **Mantiene precisión** - Muestra decimales cuando son necesarios
✅ **Consistencia** - Mismo formato en todo el sistema

## Uso en Nuevos Componentes

Para usar el formateador en nuevos componentes:

```typescript
import { formatearMoneda } from '@/shared/utils/formatters';

// En el componente
<span>{formatearMoneda(valor)}</span>
```

## Funciones Disponibles

```typescript
// Formatear con símbolo de moneda
formatearMoneda(15500.00) // "$15500"
formatearMoneda(7750.50)  // "$7750.50"

// Formatear sin símbolo
formatearNumero(15500.00) // "15500"
formatearNumero(7750.50)  // "7750.50"

// Formatear porcentaje
formatearPorcentaje(100.5) // "+100.5%"
formatearPorcentaje(-25.3) // "-25.3%"
```

## Notas Técnicas

- La función usa el operador módulo (`%`) para detectar decimales
- Los valores se redondean con `Math.round()` para enteros
- Los decimales se mantienen con `.toFixed(2)` cuando son necesarios
- Compatible con todos los navegadores modernos

---

**Implementado:** 23 de noviembre de 2025
**Estado:** ✅ Completado y funcional
