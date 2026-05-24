# 🎯 Instrucciones para Probar el Dashboard

## ✅ Pasos para Activar el Módulo

### 1. Instalar Dependencias

```bash
cd tialola-frontend
npm install
```

Esto instalará `recharts` que se agregó para los gráficos.

### 2. Reiniciar el Frontend

Si el frontend está corriendo, necesitas reiniciarlo:

**Opción A: Cerrar y volver a iniciar**
- Cerrar la ventana del frontend
- Ejecutar: `iniciar-restaurante-lola.bat`

**Opción B: Ctrl+C y npm run dev**
```bash
# En la terminal del frontend
Ctrl+C
npm run dev
```

### 3. Acceder al Dashboard

1. Abrir navegador: `http://localhost:3000`
2. Login como **DUENO**:
   - Usuario: `dueno`
   - Contraseña: `dueno123`
3. **¡El dashboard aparecerá automáticamente!**

---

## 🎨 Qué Verás en el Dashboard

### Sección 1: Ventas de Hoy
- 💰 **Total Ventas** - Monto total del día
- 🎫 **Ticket Promedio** - Promedio por orden
- 📋 **Órdenes** - Cantidad de órdenes

Cada tarjeta muestra:
- Valor principal
- Subtítulo con información adicional
- Variación porcentual vs día anterior (con color verde/rojo)

### Sección 2: Ventas de la Semana
- 💵 **Total Semana** - Últimos 7 días
- 📊 **Promedio Diario** - Promedio de la semana

### Sección 3: Ventas del Mes
- 💎 **Total Mes** - Mes actual
- 🎯 **Ticket Promedio** - Del mes

### Sección 4: Gráfico de Tendencias
- 📈 Gráfico de líneas con ventas de los últimos 7 días
- Interactivo: pasa el mouse sobre los puntos
- Muestra fecha y monto

### Sección 5: Top 5 Platos
- 🥇 Plato #1 con medalla de oro
- 🥈 Plato #2 con medalla de plata
- 🥉 Plato #3 con medalla de bronce
- 4️⃣ Plato #4
- 5️⃣ Plato #5

Cada plato muestra:
- Nombre y categoría
- Cantidad vendida
- Total de ventas

### Sección 6: Alertas de Stock
- ⚠️ Lista de insumos con stock bajo
- Colores por nivel:
  - 🔴 **CRITICO** - Menos del 25% del mínimo
  - 🟡 **BAJO** - Menos del 50% del mínimo
  - 🟠 **MEDIO** - Menos del 100% del mínimo
- Si todo está bien: ✅ "¡Todo el inventario está en niveles óptimos!"

### Sección 7: Estado de Caja
- 📈 **Ingresos** - Total de ingresos del día
- 📉 **Egresos** - Total de egresos del día
- 💵 **Saldo Actual** - Diferencia (ingresos - egresos)

---

## 🧪 Cómo Probar el Dashboard

### Paso 1: Generar Datos

Para que el dashboard muestre información, necesitas datos en el sistema:

#### A. Crear Ventas
1. Ir a **POS / Ventas** (o login como cajero)
2. Crear varias ventas con diferentes montos
3. Volver al dashboard como dueño
4. ✅ Verás las ventas reflejadas

#### B. Crear Ventas en Diferentes Días (Para el Gráfico)
Para ver el gráfico con datos reales, necesitas ventas de varios días.

**Opción 1: Esperar varios días** 😅

**Opción 2: Insertar datos manualmente en la BD**
```sql
-- Insertar ventas de prueba de los últimos 7 días
INSERT INTO ventas (fecha, usuario_id, subtotal, total, metodo_pago, estado)
VALUES 
  (NOW() - INTERVAL '6 days', 1, 500, 500, 'EFECTIVO', 'COMPLETADA'),
  (NOW() - INTERVAL '5 days', 1, 750, 750, 'EFECTIVO', 'COMPLETADA'),
  (NOW() - INTERVAL '4 days', 1, 600, 600, 'TARJETA', 'COMPLETADA'),
  (NOW() - INTERVAL '3 days', 1, 900, 900, 'EFECTIVO', 'COMPLETADA'),
  (NOW() - INTERVAL '2 days', 1, 800, 800, 'TARJETA', 'COMPLETADA'),
  (NOW() - INTERVAL '1 day', 1, 1000, 1000, 'EFECTIVO', 'COMPLETADA'),
  (NOW(), 1, 1200, 1200, 'EFECTIVO', 'COMPLETADA');
```

#### C. Ajustar Inventario (Para Alertas)
1. Ir a **Menú Avanzado → Inventario**
2. Ajustar algunos insumos para que estén bajo el mínimo
3. Volver al dashboard
4. ✅ Verás las alertas de stock

### Paso 2: Verificar Funcionalidades

#### A. Auto-Actualización
1. Dejar el dashboard abierto
2. Crear una nueva venta en otra pestaña
3. Esperar 5 minutos
4. ✅ El dashboard se actualizará automáticamente

#### B. Actualización Manual
1. Crear una nueva venta
2. Volver al dashboard
3. Clic en **"🔄 Actualizar"**
4. ✅ Los datos se actualizan inmediatamente

#### C. Interactividad del Gráfico
1. Pasar el mouse sobre el gráfico
2. ✅ Aparece tooltip con fecha y monto
3. Los puntos se agrandan al pasar el mouse

#### D. Responsive Design
1. Cambiar tamaño de la ventana
2. ✅ El layout se adapta
3. En móvil: todo en 1 columna
4. En tablet: 2 columnas
5. En desktop: 3 columnas

---

## 🎯 Casos de Prueba Específicos

### Caso 1: Dashboard con Datos Completos

**Objetivo:** Ver el dashboard con todas las secciones pobladas

**Pasos:**
1. Crear al menos 5 ventas hoy
2. Tener ventas de los últimos 7 días
3. Tener al menos 5 platos diferentes vendidos
4. Tener algunos insumos bajo stock
5. Ir al dashboard

**Resultado Esperado:**
- Todas las tarjetas muestran valores
- Gráfico muestra tendencia de 7 días
- Top 5 platos completo
- Alertas de stock visibles
- Estado de caja con valores

### Caso 2: Dashboard Sin Datos

**Objetivo:** Ver cómo se comporta sin datos

**Pasos:**
1. Base de datos limpia (sin ventas)
2. Ir al dashboard

**Resultado Esperado:**
- Tarjetas muestran $0.00 o 0
- Gráfico vacío o con línea en 0
- "No hay datos de platos vendidos"
- "¡Todo el inventario está en niveles óptimos!"
- Estado de caja en $0.00

### Caso 3: Variación Porcentual

**Objetivo:** Ver la variación vs período anterior

**Pasos:**
1. Crear ventas ayer: $500
2. Crear ventas hoy: $750
3. Ir al dashboard

**Resultado Esperado:**
- Variación: +50% (en verde)
- Badge verde con "↑ +50.00% vs período anterior"

### Caso 4: Alertas de Stock Crítico

**Objetivo:** Ver alertas en rojo

**Pasos:**
1. Ajustar un insumo a 10% del mínimo
2. Ir al dashboard

**Resultado Esperado:**
- Alerta en rojo con 🔴
- Nivel: "CRITICO"
- Fondo rojo claro

---

## 📊 Elementos Visuales a Verificar

### Tarjetas de Métricas

✅ **Verificar:**
- Icono grande y visible
- Título en mayúsculas
- Valor grande y destacado
- Subtítulo en gris
- Variación con color (verde/rojo)
- Hover: tarjeta se eleva
- Borde izquierdo de color

### Gráfico de Tendencias

✅ **Verificar:**
- Línea azul continua
- Puntos en cada día
- Ejes X (fechas) e Y (montos)
- Grid de fondo
- Tooltip al pasar mouse
- Formato de moneda en tooltip
- Responsive (se adapta al ancho)

### Top Platos

✅ **Verificar:**
- Medallas: 🥇 🥈 🥉
- Posición (#1, #2, etc.)
- Nombre del plato en negrita
- Categoría en gris
- Cantidad vendida
- Total de ventas
- Hover: se mueve a la derecha

### Alertas de Stock

✅ **Verificar:**
- Colores por nivel (rojo, amarillo, naranja)
- Icono de nivel (🔴 🟡 🟠)
- Nombre del insumo
- Cantidad actual vs mínima
- Unidad de medida
- Hover: se mueve a la derecha

### Estado de Caja

✅ **Verificar:**
- 3 tarjetas con gradientes
- Ingresos: fondo verde
- Egresos: fondo rojo
- Saldo: fondo azul
- Iconos grandes (📈 📉 💵)
- Valores en formato moneda
- Fecha del día

---

## 🐛 Troubleshooting

### Problema: Dashboard no carga

**Solución:**
1. Verificar que el backend esté corriendo
2. Verificar que estás logueado como DUENO
3. Ver consola del navegador (F12)
4. Verificar endpoint: `http://localhost:8080/api/dashboard`

### Problema: Gráfico no se muestra

**Solución:**
1. Verificar que `recharts` esté instalado:
   ```bash
   npm list recharts
   ```
2. Verificar que haya datos en `tendenciasSemanal`
3. Ver consola para errores de Recharts

### Problema: No aparecen datos

**Solución:**
1. Crear ventas en el sistema
2. Clic en "🔄 Actualizar"
3. Verificar que el backend responda:
   ```bash
   curl http://localhost:8080/api/dashboard
   ```

### Problema: Variación porcentual no aparece

**Solución:**
1. Necesitas ventas del día anterior
2. El backend calcula la variación automáticamente
3. Si no hay datos anteriores, no se muestra

### Problema: Alertas no aparecen

**Solución:**
1. Ajustar inventario para que esté bajo el mínimo
2. Verificar que los insumos tengan `cantidadMinima` configurada
3. Clic en "🔄 Actualizar"

---

## 📝 Checklist de Pruebas

### Funcionalidades Básicas
- [ ] Dashboard carga correctamente
- [ ] Todas las secciones visibles
- [ ] Datos se muestran correctamente
- [ ] Formato de moneda correcto
- [ ] Fechas en español

### Tarjetas de Métricas
- [ ] Ventas de hoy muestra datos
- [ ] Ventas de semana muestra datos
- [ ] Ventas de mes muestra datos
- [ ] Variación porcentual visible
- [ ] Colores correctos por tipo
- [ ] Hover effect funciona

### Gráfico
- [ ] Gráfico se renderiza
- [ ] Muestra 7 días
- [ ] Tooltip funciona
- [ ] Formato de moneda en tooltip
- [ ] Responsive

### Top Platos
- [ ] Muestra hasta 5 platos
- [ ] Medallas correctas
- [ ] Datos completos
- [ ] Hover effect funciona

### Alertas
- [ ] Muestra insumos bajo stock
- [ ] Colores por nivel
- [ ] Datos completos
- [ ] Mensaje cuando todo está bien

### Estado de Caja
- [ ] Muestra ingresos
- [ ] Muestra egresos
- [ ] Muestra saldo
- [ ] Gradientes visibles
- [ ] Fecha correcta

### Actualización
- [ ] Botón actualizar funciona
- [ ] Auto-actualización cada 5 min
- [ ] Última actualización visible
- [ ] Loading state funciona

### Responsive
- [ ] Funciona en desktop
- [ ] Funciona en tablet
- [ ] Funciona en móvil
- [ ] Grid se adapta

---

## 🎉 Resultado Esperado Final

Al completar todas las pruebas, deberías tener:

✅ **Dashboard Completo y Funcional:**
- Métricas de ventas actualizadas
- Gráfico de tendencias interactivo
- Top 5 platos más vendidos
- Alertas de stock en tiempo real
- Estado de caja del día
- Auto-actualización funcionando
- Interfaz responsive
- Sin errores en consola

✅ **Experiencia de Usuario:**
- Carga rápida (< 2 segundos)
- Interfaz intuitiva
- Información clara y visible
- Interacciones suaves
- Colores y diseño profesional

---

## 📞 Soporte

Si encuentras problemas:

1. **Revisar consola del navegador:** F12 → Console
2. **Verificar backend:** Ver logs del servidor
3. **Verificar datos:** Consultar base de datos
4. **Consultar documentación:**
   - `tialola-frontend/src/modules/dashboard/README.md`
   - `src/main/java/com/tialola/service/README.md` (si existe)

---

## 🎊 ¡Listo!

Ahora tienes un dashboard completamente funcional que muestra:
- 📊 Métricas clave de ventas
- 📈 Tendencias visuales
- 🍽️ Platos más populares
- ⚠️ Alertas de inventario
- 💰 Estado financiero

**¡Disfruta del dashboard!** 🎉
