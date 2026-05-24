# Restaurante Doña Lola - Sistema POS

Sistema de punto de venta para Restaurante Doña Lola con gestión de ventas, inventario, nómina y contabilidad.

## Tecnologías

### Backend
- Java 17
- Spring Boot 3.2.0
- PostgreSQL
- Maven

### Frontend
- React 18
- TypeScript
- Vite
- React Router

## Estructura del Proyecto

```
restaurante-lola/
├── src/main/java/com/tialola/
│   ├── RestauranteLolaApplication.java
│   └── contabilidad/ventas/
│       ├── controller/
│       ├── service/
│       ├── repository/
│       ├── model/
│       ├── dto/
│       ├── mapper/
│       └── config/
├── tialola-frontend/
│   └── src/
│       ├── app/
│       ├── modules/
│       ├── shared/
│       └── styles/
└── pom.xml
```

## Configuración

### Base de Datos PostgreSQL

1. Crear la base de datos:
```sql
CREATE DATABASE restaurante_lola;
```

2. Configurar credenciales en `src/main/resources/application.properties`

### Backend

```bash
# Compilar y ejecutar
mvn clean install
mvn spring-boot:run
```

El servidor estará disponible en: http://localhost:8080

### Frontend

```bash
cd tialola-frontend
npm install
npm run dev
```

El frontend estará disponible en: http://localhost:3000

## Usuarios de Prueba

- **Cajero**: usuario: `cajero` / password: `cajero`
- **Dueño**: usuario: `dueno` / password: `dueno`

## Módulos

- **Ventas**: Pantalla táctil POS, consulta de ventas
- **Inventario**: Gestión de insumos y recetas
- **Nómina**: Control de empleados y pagos
- **Contabilidad**: Resumen de caja y reportes
- **Cierre de Caja**: Arqueo diario exclusivo para el dueño
- **Propinas (estructura)**: Lista para activarse vía `propinas.enabled`
- **Configuración**: Ajustes del sistema
- **Auditoría**: Registro de operaciones

## API Endpoints

- `POST /api/ventas` - Crear nueva venta
- `GET /api/ventas/dia` - Obtener ventas del día
- `GET /api/ventas` - Obtener todas las ventas
- `GET /api/cierre-caja/estado` - Resumen y alertas del cierre diario
- `POST /api/cierre-caja/iniciar` - Abrir cierre diario
- `POST /api/cierre-caja/{id}/cerrar` - Registrar arqueo contado
- `GET /api/cierre-caja/historial` - Listar cierres por rango
- `GET /api/propinas/estado` - Consultar estado del feature de propinas
