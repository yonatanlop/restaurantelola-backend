# 🚀 Guía de Inicio - Restaurante Doña Lola

## 📋 Opciones para Iniciar el Sistema

Tienes **4 opciones** para iniciar el sistema. Elige la que mejor se adapte a tus necesidades:

---

## ✅ OPCIÓN 1: Script BAT (Más Simple y Rápido)

### Pasos:
1. **Hacer doble clic** en: `iniciar-restaurante-lola.bat`
2. El script automáticamente:
   - ✓ Verifica Java, Node.js y PostgreSQL
   - ✓ Inicia el Backend (Spring Boot)
   - ✓ Inicia el Frontend (React)
   - ✓ Abre el navegador en http://localhost:3000

### Ventajas:
- ✅ No requiere instalación adicional
- ✅ Funciona en cualquier Windows
- ✅ Muy rápido

### Desventajas:
- ⚠️ Abre ventanas de consola visibles
- ⚠️ No es un .exe

---

## ✅ OPCIÓN 2: Script PowerShell (Más Robusto)

### Pasos:
1. **Clic derecho** en: `iniciar-restaurante-lola.ps1`
2. Seleccionar: **"Ejecutar con PowerShell"**
3. Si aparece error de permisos, ejecutar primero:
   ```powershell
   Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
   ```

### Ventajas:
- ✅ Mejor manejo de errores
- ✅ Interfaz más bonita con colores
- ✅ Detecta y libera puertos ocupados
- ✅ Muestra progreso detallado

### Desventajas:
- ⚠️ Puede requerir cambiar política de ejecución
- ⚠️ No es un .exe

---

## ✅ OPCIÓN 3: Crear EXE con IExpress (Windows Nativo)

### Pasos:
1. **Hacer doble clic** en: `crear-ejecutable.bat`
2. Esperar a que se genere: `RestauranteLola.exe`
3. **Hacer doble clic** en: `RestauranteLola.exe`

### Ventajas:
- ✅ Genera un archivo .exe real
- ✅ No requiere software adicional (usa IExpress de Windows)
- ✅ Fácil de distribuir

### Desventajas:
- ⚠️ El .exe es solo un wrapper del script BAT
- ⚠️ Interfaz básica

---

## ✅ OPCIÓN 4: Launcher Java con GUI (Más Profesional)

### Pasos:

#### A. Compilar el Launcher (solo una vez):
1. **Hacer doble clic** en: `compilar-launcher.bat`
2. Se generará: `RestauranteLola.jar`

#### B. Ejecutar el Launcher:
1. **Hacer doble clic** en: `RestauranteLola.jar`
   - O ejecutar: `java -jar RestauranteLola.jar`

#### C. Crear EXE (Opcional - requiere Java 14+):
El script `compilar-launcher.bat` intentará crear un .exe automáticamente si tienes Java 14+

### Ventajas:
- ✅ Interfaz gráfica profesional
- ✅ Muestra logs en tiempo real
- ✅ Botones para iniciar/detener
- ✅ Barra de progreso
- ✅ Multiplataforma (Windows, Mac, Linux)

### Desventajas:
- ⚠️ Requiere compilar primero
- ⚠️ Necesita Java instalado para ejecutar

---

## 🎯 Recomendación por Caso de Uso

| Caso de Uso | Opción Recomendada |
|-------------|-------------------|
| **Desarrollo diario** | Opción 1 (BAT) |
| **Presentación/Demo** | Opción 4 (Launcher GUI) |
| **Distribución a usuarios** | Opción 3 (EXE con IExpress) |
| **Máxima confiabilidad** | Opción 2 (PowerShell) |

---

## 📦 Requisitos Previos

Antes de usar cualquier opción, asegúrate de tener instalado:

### 1. Java 17 o superior
```bash
java -version
```
**Descargar:** https://adoptium.net/

### 2. Node.js (versión 16+)
```bash
node -v
npm -v
```
**Descargar:** https://nodejs.org/

### 3. PostgreSQL
```bash
pg_isready -h localhost -p 5432
```
**Descargar:** https://www.postgresql.org/download/

### 4. Maven
```bash
mvn -version
```
**Descargar:** https://maven.apache.org/download.cgi

---

## 🔧 Configuración Inicial (Solo Primera Vez)

### 1. Configurar Base de Datos
```bash
# Crear base de datos
psql -U postgres
CREATE DATABASE restaurante_lola;
\q

# Ejecutar scripts
psql -U postgres -d restaurante_lola -f database/scripts/01_create_tables.sql
psql -U postgres -d restaurante_lola -f database/scripts/02_insert_data.sql
```

### 2. Configurar Backend
Editar `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/restaurante_lola
spring.datasource.username=postgres
spring.datasource.password=TU_PASSWORD
```

### 3. Instalar Dependencias del Frontend
```bash
cd tialola-frontend
npm install
```

---

## 🌐 URLs del Sistema

Una vez iniciado el sistema:

| Servicio | URL | Descripción |
|----------|-----|-------------|
| **Frontend** | http://localhost:3000 | Interfaz de usuario |
| **Backend API** | http://localhost:8080 | API REST |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | Documentación API |
| **Base de Datos** | localhost:5432 | PostgreSQL |

---

## 🔐 Usuarios por Defecto

| Usuario | Contraseña | Rol |
|---------|-----------|-----|
| `dueno` | `dueno123` | DUENO (Administrador) |
| `cajero` | `cajero123` | CAJERO |

---

## 🛑 Detener el Sistema

### Opción 1, 2 y 3:
- Cerrar las ventanas de consola que se abrieron
- O presionar `Ctrl+C` en cada ventana

### Opción 4 (Launcher GUI):
- Hacer clic en el botón **"Detener Sistema"**

---

## 🐛 Solución de Problemas

### Error: "Puerto 8080 ya está en uso"
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# PowerShell
Get-NetTCPConnection -LocalPort 8080 | Select-Object -ExpandProperty OwningProcess
Stop-Process -Id <PID> -Force
```

### Error: "Puerto 3000 ya está en uso"
```bash
# Windows
netstat -ano | findstr :3000
taskkill /PID <PID> /F
```

### Error: "PostgreSQL no está corriendo"
```bash
# Iniciar servicio
net start postgresql-x64-14

# O desde Servicios de Windows
services.msc
```

### Error: "Java no encontrado"
```bash
# Agregar Java al PATH
setx JAVA_HOME "C:\Program Files\Java\jdk-17"
setx PATH "%PATH%;%JAVA_HOME%\bin"
```

### Error: "Maven no encontrado"
```bash
# Agregar Maven al PATH
setx MAVEN_HOME "C:\Program Files\Apache\maven"
setx PATH "%PATH%;%MAVEN_HOME%\bin"
```

---

## 📝 Crear Acceso Directo en el Escritorio

### Para el Script BAT:
1. Clic derecho en `iniciar-restaurante-lola.bat`
2. Seleccionar **"Crear acceso directo"**
3. Mover el acceso directo al escritorio
4. (Opcional) Cambiar el icono:
   - Clic derecho → Propiedades → Cambiar icono

### Para el Launcher JAR:
1. Clic derecho en el escritorio → Nuevo → Acceso directo
2. Ubicación: `javaw -jar "RUTA_COMPLETA\RestauranteLola.jar"`
3. Nombre: "Restaurante Doña Lola"
4. Finalizar

---

## 🎨 Personalizar el Launcher

### Cambiar el título:
Editar `RestauranteLolaLauncher.java` línea 15:
```java
setTitle("TU TÍTULO AQUÍ");
```

### Cambiar colores:
Editar líneas 24-26:
```java
headerPanel.setBackground(new Color(41, 128, 185)); // Azul
```

### Recompilar:
```bash
compilar-launcher.bat
```

---

## 📚 Documentación Adicional

- **Backend:** Ver `README.md` en la raíz del proyecto
- **Frontend:** Ver `tialola-frontend/README.md`
- **API:** http://localhost:8080/swagger-ui.html (cuando esté corriendo)
- **Base de Datos:** Ver scripts en `database/scripts/`

---

## 💡 Tips y Trucos

### Inicio Automático con Windows
1. Presionar `Win + R`
2. Escribir: `shell:startup`
3. Copiar el acceso directo del launcher aquí

### Crear Tarea Programada
1. Abrir **Programador de tareas**
2. Crear tarea básica
3. Desencadenador: Al iniciar sesión
4. Acción: Iniciar programa
5. Programa: Ruta al script o .exe

### Modo Silencioso (Sin ventanas)
Editar el script BAT y cambiar:
```batch
start "Backend" cmd /k "mvn spring-boot:run"
```
Por:
```batch
start /B "" javaw -jar target/restaurante-lola.jar
```

---

## 🆘 Soporte

Si tienes problemas:
1. Revisar los logs en las ventanas de consola
2. Verificar que todos los requisitos estén instalados
3. Revisar la sección de "Solución de Problemas"
4. Contactar al equipo de desarrollo

---

## ✅ Checklist de Verificación

Antes de iniciar, verifica:

- [ ] Java 17+ instalado y en PATH
- [ ] Node.js instalado y en PATH
- [ ] PostgreSQL corriendo
- [ ] Base de datos creada
- [ ] Scripts SQL ejecutados
- [ ] Dependencias del frontend instaladas (`npm install`)
- [ ] Archivo `application.properties` configurado

---

## 🎉 ¡Listo!

Ahora puedes iniciar el sistema con cualquiera de las 4 opciones.

**Recomendación:** Empieza con la **Opción 1 (BAT)** para probar que todo funciona, luego usa la **Opción 4 (Launcher GUI)** para una experiencia más profesional.

¡Disfruta del Sistema POS de Restaurante Doña Lola! 🍽️
