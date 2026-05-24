# Nuevo Sistema de Login - Cajero y Dueño

## Cambios Implementados

Se ha modificado el sistema de autenticación para simplificar el acceso según el rol del usuario:

### 🎯 Objetivo

- **Cajero**: Acceso directo sin contraseña (solo un clic)
- **Dueño**: Acceso con validación de contraseña

### 📋 Cambios Realizados

#### Backend (Java/Spring Boot)

1. **AuthController.java** - Nuevos endpoints:
   - `POST /api/auth/login/cajero` - Login sin contraseña para cajero
   - `POST /api/auth/login/dueno` - Login con contraseña solo para dueño

2. **AuthService.java** - Nuevos métodos:
   - `loginCajeroSinPassword()` - Busca el primer usuario con rol CAJERO activo
   - `loginDueno(LoginRequest)` - Valida contraseña del usuario con rol DUENO

3. **UsuarioRepository.java** - Nuevo método:
   - `findByRolAndActivo(String rol, Boolean activo)` - Busca usuarios por rol

#### Frontend (React/TypeScript)

1. **authApi.ts** - Nuevas funciones:
   - `loginCajero()` - Llama al endpoint de cajero
   - `loginDueno(password)` - Llama al endpoint de dueño con contraseña

2. **LoginPage.tsx** - Nueva interfaz:
   - Dos botones grandes: "Cajero" y "Dueño"
   - Modal de contraseña que aparece solo para el dueño
   - Diseño más intuitivo y táctil

3. **globals.css** - Nuevos estilos:
   - Botones de rol con iconos y descripciones
   - Modal de contraseña elegante
   - Diseño responsive para móviles

### 🔐 Flujo de Autenticación

#### Flujo Cajero (Sin Contraseña)
```
1. Usuario hace clic en botón "Cajero"
2. Frontend llama a POST /api/auth/login/cajero
3. Backend busca el primer usuario con rol CAJERO activo
4. Backend genera token JWT
5. Frontend guarda token y redirige a /cajero/venta
```

#### Flujo Dueño (Con Contraseña)
```
1. Usuario hace clic en botón "Dueño"
2. Frontend muestra modal de contraseña
3. Usuario ingresa contraseña
4. Frontend llama a POST /api/auth/login/dueno con la contraseña
5. Backend busca el usuario con rol DUENO activo
6. Backend valida la contraseña
7. Backend genera token JWT
8. Frontend guarda token y redirige a /dueno/dashboard
```

### 🛡️ Seguridad

#### Cajero
- **Sin contraseña**: El acceso es directo pero limitado
- **Permisos restringidos**: Solo puede acceder a `/cajero/venta`
- **No ve información del dueño**: Las rutas del dueño están protegidas por rol

#### Dueño
- **Con contraseña**: Requiere validación de contraseña
- **Acceso completo**: Puede acceder a todas las funcionalidades
- **Contraseña encriptada**: Se valida con BCrypt en el backend

### 📱 Interfaz de Usuario

#### Pantalla de Login
```
┌─────────────────────────────────────┐
│         🍽️                          │
│   Restaurante Doña Lola             │
│   Sistema de Punto de Venta         │
│                                     │
│   Seleccione su perfil              │
│                                     │
│  ┌───────────────────────────────┐ │
│  │ 💰  Cajero                    │ │
│  │     Acceso al punto de venta  │ │
│  └───────────────────────────────┘ │
│                                     │
│  ┌───────────────────────────────┐ │
│  │ 👔  Dueño                     │ │
│  │     Acceso al sistema         │ │
│  └───────────────────────────────┘ │
│                                     │
│   Versión 1.1.0                     │
└─────────────────────────────────────┘
```

#### Modal de Contraseña (Solo Dueño)
```
┌─────────────────────────────────────┐
│  Acceso de Dueño              ✕     │
│  ─────────────────────────────────  │
│                                     │
│  Contraseña                         │
│  ┌───────────────────────────────┐ │
│  │ ••••••••••                    │ │
│  └───────────────────────────────┘ │
│                                     │
│  ┌──────────┐  ┌──────────────┐   │
│  │ Cancelar │  │   Ingresar   │   │
│  └──────────┘  └──────────────┘   │
└─────────────────────────────────────┘
```

### 🚀 Despliegue

#### Requisitos Previos

Asegúrate de que existan los usuarios en la base de datos:

```sql
-- Verificar usuarios existentes
SELECT id, nombre, usuario, rol, activo FROM usuarios;

-- Debe existir al menos:
-- 1 usuario con rol 'CAJERO' y activo = true
-- 1 usuario con rol 'DUENO' y activo = true
```

Si no existen, créalos:

```sql
-- Crear usuario cajero (sin contraseña necesaria para login)
INSERT INTO usuarios (nombre, usuario, password, rol, activo, fecha_creacion, fecha_modificacion)
VALUES ('Cajero Principal', 'cajero', '$2a$10$dummyHashNotUsed', 'CAJERO', true, NOW(), NOW());

-- Crear usuario dueño (con contraseña: "admin123")
-- Hash BCrypt de "admin123": $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
INSERT INTO usuarios (nombre, usuario, password, rol, activo, fecha_creacion, fecha_modificacion)
VALUES ('Dueño', 'dueno', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'DUENO', true, NOW(), NOW());
```

#### Pasos de Despliegue

1. **Compilar Backend**
```bash
mvn clean package -DskipTests
```

2. **Compilar Frontend**
```bash
cd tialola-frontend
npm run build
```

3. **Reiniciar Aplicación**
```bash
# Detener aplicación actual
taskkill /F /IM java.exe

# Iniciar nueva versión
java -jar target/restaurante-lola-*.jar
```

4. **Verificar**
- Abrir navegador en `http://localhost:8080`
- Verificar que aparezcan los dos botones
- Probar login de cajero (debe entrar directo)
- Probar login de dueño (debe pedir contraseña)

### ✅ Pruebas

#### Prueba 1: Login Cajero
1. Hacer clic en botón "Cajero"
2. Verificar redirección a `/cajero/venta`
3. Verificar que NO puede acceder a `/dueno/dashboard`

#### Prueba 2: Login Dueño
1. Hacer clic en botón "Dueño"
2. Verificar que aparece modal de contraseña
3. Ingresar contraseña correcta
4. Verificar redirección a `/dueno/dashboard`
5. Verificar acceso a todas las opciones del menú

#### Prueba 3: Contraseña Incorrecta
1. Hacer clic en botón "Dueño"
2. Ingresar contraseña incorrecta
3. Verificar mensaje de error
4. Verificar que NO se permite el acceso

#### Prueba 4: Cancelar Modal
1. Hacer clic en botón "Dueño"
2. Hacer clic en "Cancelar" o "X"
3. Verificar que vuelve a la pantalla de selección

### 🔧 Configuración

#### Cambiar Contraseña del Dueño

Para cambiar la contraseña del dueño, genera un nuevo hash BCrypt:

```java
// En Java
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hashedPassword = encoder.encode("nuevaContraseña");
System.out.println(hashedPassword);
```

O usa una herramienta online: https://bcrypt-generator.com/

Luego actualiza en la base de datos:

```sql
UPDATE usuarios 
SET password = '$2a$10$NUEVO_HASH_AQUI', 
    fecha_modificacion = NOW()
WHERE rol = 'DUENO';
```

#### Agregar Múltiples Cajeros

Si necesitas múltiples cajeros, todos compartirán el mismo acceso sin contraseña:

```sql
INSERT INTO usuarios (nombre, usuario, password, rol, activo, fecha_creacion, fecha_modificacion)
VALUES ('Cajero 2', 'cajero2', '$2a$10$dummy', 'CAJERO', true, NOW(), NOW());
```

El sistema tomará el primero que encuentre activo.

### 📊 Ventajas del Nuevo Sistema

✅ **Más rápido**: Cajero entra con un solo clic
✅ **Más seguro**: Dueño sigue protegido con contraseña
✅ **Más intuitivo**: Interfaz clara con iconos y descripciones
✅ **Responsive**: Funciona bien en tablets y móviles
✅ **Mantenible**: Código limpio y bien estructurado

### 🐛 Troubleshooting

#### Error: "No se encontró usuario cajero configurado"
**Causa**: No existe un usuario con rol CAJERO activo en la BD
**Solución**: Crear usuario cajero con el SQL proporcionado arriba

#### Error: "No se encontró usuario dueño configurado"
**Causa**: No existe un usuario con rol DUENO activo en la BD
**Solución**: Crear usuario dueño con el SQL proporcionado arriba

#### Error: "Contraseña incorrecta"
**Causa**: La contraseña ingresada no coincide con el hash en la BD
**Solución**: Verificar la contraseña o regenerar el hash

#### El cajero puede ver opciones del dueño
**Causa**: Las rutas no están protegidas correctamente
**Solución**: Verificar que `ProtectedRoute` tenga `allowedRoles={['DUENO', 'ADMIN']}`

### 📝 Notas Importantes

1. **Compatibilidad**: El endpoint antiguo `/api/auth/login` sigue funcionando para compatibilidad
2. **Tokens JWT**: Ambos flujos generan tokens JWT válidos con el rol correspondiente
3. **Sesiones**: Se registran en la tabla `sesiones` para auditoría
4. **Logout**: El logout funciona igual para ambos roles

### 🔄 Rollback

Si necesitas volver al sistema anterior:

1. Restaurar `LoginPage.tsx` desde el commit anterior
2. Restaurar `authApi.ts` desde el commit anterior
3. Los endpoints nuevos no afectan el funcionamiento anterior

### 📅 Fecha de Implementación

Implementado: [Fecha actual]
Responsable: Equipo de desarrollo
Estado: Listo para producción
