# Módulo de Autenticación - HU-015

## Descripción
Módulo de autenticación con login simple para Dueño y Cajero, usando JWT para gestión de sesiones.

## Componentes

### Backend
- **AuthController**: Endpoints de login, logout y obtener usuario actual
- **AuthService**: Lógica de negocio de autenticación
- **JwtUtil**: Utilidad para generar y validar tokens JWT
- **SecurityConfig**: Configuración de Spring Security
- **Usuario**: Entidad de usuario
- **Sesion**: Entidad para registro de sesiones

### Frontend
- **LoginPage**: Página de inicio de sesión táctil
- **useLogin**: Hook para manejar el login
- **authApi**: Servicios de API para autenticación

## Endpoints

### POST /api/auth/login
Autentica un usuario y devuelve un token JWT.

**Request:**
```json
{
  "usuario": "dueno",
  "password": "dueno123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "usuario": {
    "id": 1,
    "nombre": "Lola (Dueña)",
    "usuario": "dueno",
    "rol": "DUENO"
  }
}
```

### POST /api/auth/logout
Cierra la sesión del usuario actual.

### GET /api/auth/me
Obtiene información del usuario autenticado.

## Roles
- **ADMIN**: Acceso total al sistema
- **DUENO**: Acceso a todas las funcionalidades de gestión
- **CAJERO**: Acceso limitado a ventas y consultas básicas

## Usuarios de Prueba
| Usuario | Contraseña | Rol |
|---------|-----------|-----|
| admin   | admin123  | ADMIN |
| dueno   | dueno123  | DUENO |
| cajero  | cajero123 | CAJERO |

## Seguridad
- Contraseñas hasheadas con BCrypt (10 rounds)
- Tokens JWT con expiración de 24 horas
- Sesiones registradas en base de datos para auditoría
