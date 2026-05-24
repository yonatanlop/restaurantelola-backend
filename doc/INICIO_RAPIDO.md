# 🚀 Inicio Rápido - Restaurante Doña Lola

## ⚡ Instalación Automática (Recomendado)

### 1️⃣ Primera Vez - Instalación Completa
```
Hacer doble clic en: INSTALAR_Y_CONFIGURAR.bat
```

Este script hará TODO automáticamente:
- ✅ Verifica requisitos (Java, Node.js, PostgreSQL, Maven)
- ✅ Crea la base de datos
- ✅ Ejecuta los scripts SQL
- ✅ Configura el backend
- ✅ Instala dependencias del frontend
- ✅ Compila el proyecto
- ✅ Crea ejecutables
- ✅ Crea acceso directo en el escritorio

**Tiempo estimado:** 5-10 minutos

---

## 🎯 Iniciar el Sistema

Después de la instalación, tienes **4 formas** de iniciar:

### Opción A: Script BAT (Más Rápido) ⚡
```
Doble clic en: iniciar-restaurante-lola.bat
```

### Opción B: PowerShell (Más Robusto) 💪
```
Clic derecho en: iniciar-restaurante-lola.ps1
→ Ejecutar con PowerShell
```

### Opción C: Launcher Java (Interfaz Gráfica) 🎨
```
Doble clic en: RestauranteLola.jar
```

### Opción D: Acceso Directo del Escritorio 🖥️
```
Doble clic en el icono del escritorio
```

---

## 🌐 Acceder al Sistema

Una vez iniciado:

1. **Espera 30-60 segundos** a que todo inicie
2. El navegador se abrirá automáticamente en: **http://localhost:3000**
3. Si no se abre, abre manualmente: http://localhost:3000

### Usuarios por Defecto:
| Usuario | Contraseña | Rol |
|---------|-----------|-----|
| `dueno` | `dueno123` | Administrador |
| `cajero` | `cajero123` | Cajero |

---

## 🛑 Detener el Sistema

### Opción A: Script Automático (Más Fácil)
```
Doble clic en: detener-sistema.bat
```

### Opción B: Manual
- **Cerrar** las ventanas de consola que se abrieron
- O presionar **Ctrl+C** en cada ventana

### Opción C: Launcher GUI
- Clic en **"Detener Sistema"**

---

## 📋 Requisitos Previos

Antes de instalar, necesitas:

1. **Java 17+** → https://adoptium.net/
2. **Node.js 16+** → https://nodejs.org/
3. **PostgreSQL** → https://www.postgresql.org/
4. **Maven** → https://maven.apache.org/

---

## 🆘 Problemas Comunes

### "Puerto 8080 ya está en uso"
```bash
netstat -ano | findstr :8080
taskkill /PID <número> /F
```

### "PostgreSQL no está corriendo"
```bash
net start postgresql-x64-14
```

### "Java no encontrado"
Agregar Java al PATH de Windows

---

## 📚 Documentación Completa

Para más detalles, ver: **INSTRUCCIONES_INICIO.md**

---

## ✅ Resumen de Archivos

| Archivo | Descripción |
|---------|-------------|
| `INSTALAR_Y_CONFIGURAR.bat` | 🔧 Instalador automático |
| `iniciar-restaurante-lola.bat` | ⚡ Iniciar sistema (BAT) |
| `iniciar-restaurante-lola.ps1` | 💪 Iniciar sistema (PowerShell) |
| `RestauranteLola.jar` | 🎨 Launcher con GUI |
| `INSTRUCCIONES_INICIO.md` | 📖 Documentación completa |
| `INICIO_RAPIDO.md` | ⚡ Esta guía rápida |

---

## 🎉 ¡Eso es Todo!

1. Ejecuta `INSTALAR_Y_CONFIGURAR.bat`
2. Espera a que termine
3. Haz doble clic en `iniciar-restaurante-lola.bat`
4. ¡Disfruta del sistema! 🍽️

**¿Necesitas ayuda?** Consulta `INSTRUCCIONES_INICIO.md`
