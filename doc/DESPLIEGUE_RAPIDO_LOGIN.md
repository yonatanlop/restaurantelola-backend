# Despliegue Rápido - Nuevo Sistema de Login

## ⚡ Guía Rápida (5 minutos)

### Paso 1: Base de Datos (1 min)
```bash
psql -U engouser -d contabilidadRestaurante -f database/scripts/11_setup_usuarios_login.sql
```

### Paso 2: Compilar (2 min)
```bash
mvn clean package -DskipTests
cd tialola-frontend && npm run build && cd ..
```

### Paso 3: Reiniciar (1 min)
```bash
taskkill /F /IM java.exe
java -jar target/restaurante-lola-*.jar
```

### Paso 4: Probar (1 min)
```bash
doc\test-nuevo-login.bat
```

O abrir: http://localhost:8080

---

## 🎯 Resultado Esperado

### Pantalla de Login
- ✅ Botón "Cajero" (💰)
- ✅ Botón "Dueño" (👔)

### Funcionalidad
- ✅ Cajero: Clic → Entra directo
- ✅ Dueño: Clic → Pide contraseña → Entra

### Credenciales
- **Cajero**: Sin contraseña
- **Dueño**: Contraseña = `admin123`

---

## ⚠️ Si Algo Falla

### Error: "No se encontró usuario cajero"
```sql
-- Ejecutar en PostgreSQL
INSERT INTO usuarios (nombre, usuario, password, rol, activo, fecha_creacion, fecha_modificacion)
VALUES ('Cajero', 'cajero', '$2a$10$dummy', 'CAJERO', true, NOW(), NOW());
```

### Error: "No se encontró usuario dueño"
```sql
-- Ejecutar en PostgreSQL
INSERT INTO usuarios (nombre, usuario, password, rol, activo, fecha_creacion, fecha_modificacion)
VALUES ('Dueño', 'dueno', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'DUENO', true, NOW(), NOW());
```

### Error: Botones no aparecen
```bash
# Limpiar caché del navegador
Ctrl + Shift + R

# O reconstruir frontend
cd tialola-frontend
npm run build
cd ..
```

---

## 📋 Checklist Mínimo

- [ ] Script SQL ejecutado
- [ ] Backend compilado
- [ ] Frontend compilado
- [ ] Aplicación reiniciada
- [ ] Botones aparecen en login
- [ ] Cajero entra sin contraseña
- [ ] Dueño entra con contraseña

---

## 🆘 Ayuda Rápida

**Documentación completa**: `doc/NUEVO_SISTEMA_LOGIN.md`
**Resumen de cambios**: `doc/RESUMEN_CAMBIOS_LOGIN.md`
**Script de pruebas**: `doc/test-nuevo-login.bat`

---

¡Listo en 5 minutos! 🚀
