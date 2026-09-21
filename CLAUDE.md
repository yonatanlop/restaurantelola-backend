# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Restaurante Doña Lola** is a full-stack Point of Sale (POS) and restaurant management system. It handles sales, inventory, payroll, accounting, auditing, table management, and credit tracking for a restaurant operation.

**Project Type:** Full-stack monorepo (Java backend + React TypeScript frontend)  
**Version:** 1.1.1

## Technology Stack

### Backend
- **Framework:** Spring Boot 3.2.0
- **Language:** Java 17
- **Database:** PostgreSQL
- **Authentication:** JWT (jjwt 0.11.5)
- **ORM:** Spring Data JPA with Hibernate
- **Additional Libraries:**
  - Spring Security for authorization
  - Spring AOP for auditing
  - Apache POI for Excel export
  - iText for PDF generation

### Frontend
- **Framework:** React 18.2.0
- **Language:** TypeScript 5.3.3
- **Build Tool:** Vite 5.0.8
- **Routing:** React Router DOM 6.20.0
- **HTTP Client:** Axios 1.6.2
- **Charts:** Recharts 2.10.3
- **Utilities:** date-fns 2.30.0

### Infrastructure
- **Launcher:** Java Swing GUI launcher (`RestauranteLolaLauncher.java`) that orchestrates backend and frontend startup
- **Database:** PostgreSQL (port 5432, database: `contabilidadRestaurante`)
- **Backend Server:** Port 8080
- **Frontend Dev Server:** Port 3000

## Build & Run Commands

### Backend (Spring Boot)
```bash
# Build
mvn clean package

# Run development server
mvn spring-boot:run

# Run with Maven wrapper (if available)
mvn clean compile spring-boot:run
```

### Frontend (React + Vite)
```bash
# Navigate to frontend directory
cd tialola-frontend

# Install dependencies
npm install

# Run development server
npm run dev

# Build for production
npm run build

# Preview production build
npm preview
```

`npm run build` runs `tsc` before `vite build`, so it also serves as the frontend type-check. There is no configured test runner or lint script on either side (no `spring-boot-starter-test` in `pom.xml`, no test/lint entries in `package.json`, and no test files in the repo) — don't assume `mvn test` or `npm test` will do anything meaningful.

### Full System Start
The project includes multiple startup options documented in `doc/COMO_USAR_EL_SISTEMA.txt`:

1. **Launcher GUI (Recommended):** Double-click `RestauranteLola.jar`
2. **Batch Script:** Double-click `iniciar-restaurante-lola.bat`
3. **Manual Start:** Run backend and frontend commands separately

The launcher verifies Java and Node.js availability, starts both services, waits for ports 8080 (backend) and 3000 (frontend), then opens the browser.

## Project Structure

### Backend Architecture (`src/main/java/com/tialola/`)

The backend follows a **modular domain-driven structure** organized by business domain:

#### Core Modules
- **`auth/`** - Authentication, JWT, user sessions
  - Controllers, services, DTOs, security configuration
  - Custom JWT utility for token generation/validation
  
- **`mesas/`** - Table management (HU-011)
  - Models: `Mesa`, `Comanda`, `ComandaDetalle`, `TransferenciaMesa`
  - Mesa states: LIBRE, OCUPADA, RESERVADA, LIMPIEZA
  - Tracks current sale per table and occupation time
  
- **`contabilidad/`** - Accounting and cash management
  - Submodule `ventas/` (HU-001) has its own `config/controller/dto/mapper/model/repository/service` — the sales/POS domain lives here, separate from `mesas/`
  - Payment methods and tips tracking; settlement and reporting
  
- **`menu/`** - Menu management
  - Models: `Plato` (dishes), categories
  - Price management, availability tracking
  
- **`inventario/`** - Inventory and recipes
  - Models: `Insumo` (ingredients), `Receta` (recipes), `MovimientoInventario`
  - Stock tracking, recipe-to-ingredient mapping
  
- **`compras/`** - Purchase management
  - Models: `Proveedor` (suppliers), `Compra`, `CompraDetalle`
  - Supplier tracking and purchase history
  
- **`nomina/`** - Payroll
  - Models: `Empleado` (employees), `NominaDiaria` (daily payroll)
  - Attendance registration, daily wage calculation
  
- **`caja/`** - Cash desk operations (HU-013)
  - Models: `CierreCaja`, `DetalleArqueo`
  - Denomination counting, cash reconciliation
  
- **`credito/`** - Credit/account management
  - Customer credit tracking and payments
  
- **`impresion/`** - Printing (HU-010)
  - Models: `ConfiguracionImpresora`, `ColaImpresion`
  - Ticket formatting and printer queue management
  
- **`auditoria/`** - Audit logging (HU-012)
  - Models: `AuditoriaLog`
  - Aspect-based automatic operation tracking
  
- **`reportes/`** - Report generation
  - Excel and PDF export services

A few top-level, non-domain files sit directly under `com.tialola/` rather than in a module: `controller/DashboardController.java`, `service/DashboardService.java`, `dto/DashboardDTO.java` (dashboard KPI aggregation), and `config/HikariMonitorConfig.java`.

Most modules were built against a numbered user story (`HU-XXX`) and ship a `README_HU0XX.md` (or `README.md`) in their package root documenting the original requirements and endpoints (e.g. `auth/README.md` + `auth/README_HU016.md`, `mesas/README_HU011.md`, `auditoria/README_HU012.md`, `inventario/README_HU003.md`). Check the module directory for one before making non-trivial changes there — it's often more current and detailed than this file.

#### Data Access Pattern
- Each domain has `repository/` (Spring Data JPA interfaces)
- `service/` provides business logic
- `controller/` exposes REST endpoints at `/api/{module}/`
- `dto/` for data transfer between layers
- `model/` defines JPA entities with automatic timestamp management

### Frontend Architecture (`tialola-frontend/src/`)

**Feature-based module structure with shared components:**

```
app/
  ├── routes/        # AppRouter - defines all routes and role-based access
  ├── layouts/       # DuenoLayout, DuenoAvanzadoLayout, CajeroLayout
  ├── guards/        # ProtectedRoute - role-based route protection
  └── providers/     # AuthProvider - global auth state & JWT token management

modules/            # Feature modules (one per business domain)
  ├── auth/         # Login, unauthorized page
  ├── ventas/       # Sales/POS - includes VentaTactilPage (touchscreen) and VentasDiaPage
  ├── menu/         # Menu management UI
  ├── mesas/        # Table management
  ├── inventario/   # Inventory management
  ├── compras/      # Purchases
  ├── nomina/       # Payroll
  ├── contabilidad/ # Accounting & cash close
  ├── creditos/     # Credit management
  ├── auditoria/    # Audit logs
  ├── reportes/     # Reports
  ├── dashboard/    # KPI dashboard
  ├── dueno/        # Owner-specific pages
  ├── configuracion/# System configuration
  └── [module]/
      ├── pages/        # Full page components
      ├── components/   # Reusable UI components
      ├── services/     # API service layer (using Axios)
      ├── hooks/        # Custom React hooks
      ├── types/        # TypeScript interfaces
      └── styles/       # Module-specific CSS

shared/             # Cross-module utilities
  ├── api/          # Axios instance configuration
  ├── components/   # Generic UI components (Button, Card, Modal, Table)
  ├── services/     # Shared services
  ├── types/        # Shared TypeScript types
  └── utils/        # Utility functions

styles/             # Global and feature-specific CSS
```

### Key Architectural Patterns

#### Frontend
- **Role-based Access Control:** `ProtectedRoute` (`app/guards/`) wraps route subtrees with `allowedRoles`; roles are `DUENO`, `CAJERO`, `ADMIN`. Route tree, defined in `app/routes/AppRouter.tsx`:
  - `/cajero` (CAJERO, ADMIN) → `CajeroLayout` → `venta` (touchscreen POS, `VentaTactilPage`)
  - `/dueno` (DUENO, ADMIN) → `DuenoLayout` (quick menu) → `dashboard`, `ventas-dia`
  - `/dueno/avanzado` (DUENO, ADMIN) → `DuenoAvanzadoLayout` (full menu) → `menu`, `inventario`, `compras`, `nomina`, `contabilidad`, `cierre-caja`, `reportes`, `mesas`, `creditos`, `configuracion`, `auditoria`
  - Unmatched paths and `/` redirect to `/login`
- **Layout Separation:** Different UI layouts for different user roles (cashier view vs. owner quick/advanced menus)
- **API Layer:** Centralized `shared/api` with Axios proxy to backend at `/api/*`
- **Auth Context:** `AuthProvider` (`app/providers/`) manages JWT token, user info, and session state; exposes `hasRole()` for conditional UI, backed by `localStorage`
- **Module Isolation:** Each feature module manages its own pages, services, and types

#### Backend
- **Layered Architecture:** Controller → Service → Repository → Entity (standard Spring pattern)
- **JWT Security:** Custom `JwtUtil` validates all protected endpoints
- **AOP Auditing:** `@Aspect` automatically logs operations to `AuditoriaLog` table
- **DTOs for API:** All REST responses use DTOs to decouple API contracts from entities
- **Mappers:** Custom mappers (e.g., `VentaMapper`) transform entities to/from DTOs

## Database

**Database Name:** `contabilidadRestaurante`  
**Connection:** PostgreSQL at `localhost:5432` (credentials in `application.properties`)

### Setup Scripts
Located in `database/scripts/`, executed in order:
1. `00_create_database.sql` - Creates database
2. `01_create_tables.sql` - Core tables
3. `02_insert_initial_data.sql` - Initial users and categories
4. `05_impresion_tables.sql` - Printer config
5. `06_mesas_tables.sql` - Table/command structure
6. `07_auditoria_tables.sql` - Audit table
7. `08_*.sql` - Additional incremental updates

### Default Users (Change in Production)
- **dueno** / dueno123 (Owner - full access)
- **cajero** / cajero123 (Cashier - sales only)
- **admin** / admin123 (Administrator)

## Configuration

### Backend Configuration
**File:** `src/main/resources/application.properties`

Key settings:
- Server port: 8080
- PostgreSQL connection with HikariCP pool (optimized for limited hardware)
- JWT secret and expiration (24 hours)
- Hibernate auto-update DDL
- Serial cash register port (COM1 by default)
- Feature flags: `propinas.enabled` (tips feature toggle)
- Static resources serve frontend build from `classpath:/static/`

### Frontend Configuration
**File:** `tialola-frontend/vite.config.ts`

- Dev server on port 3000, accessible from all network interfaces
- Proxy: `/api/*` requests forward to `http://localhost:8080`
- React plugin and TypeScript support

## Development Workflow

### Making Backend Changes
1. Edit Java files in `src/main/java/com/tialola/{module}/`
2. If adding new endpoints, create/modify:
   - `{module}/model/{Entity}.java` - JPA entity
   - `{module}/dto/{EntityDTO}.java` - Data transfer object
   - `{module}/repository/{EntityRepository}.java` - Repository interface
   - `{module}/service/{EntityService}.java` - Business logic
   - `{module}/controller/{EntityController}.java` - REST endpoints
3. Run `mvn spring-boot:run` to reload (DevTools enables automatic restart)
4. Test endpoints at `http://localhost:8080/api/{path}`

### Making Frontend Changes
1. Edit TypeScript/React files in `tialola-frontend/src/`
2. Vite hot-reload automatically reflects changes
3. Use `@` alias for imports: `import X from '@/modules/...'`
4. API calls use centralized Axios instance; token is auto-added to headers by `AuthProvider`
5. Component CSS can be colocated with components or in `styles/`

### Database Changes
1. Modify entity models (JPA will auto-update with `ddl-auto=update`)
2. Or create SQL script in `database/scripts/` with next sequential number
3. Run scripts manually if needed: `psql -U postgres -d contabilidadRestaurante -f script.sql`

## Important Notes

### Hardware Optimization
The HikariCP connection pool is explicitly optimized for limited hardware:
- Min pool: 2 connections
- Max pool: 5 connections (reduced from default)
- Initialization timeout: 90 seconds (accommodates slow hardware)
- Connection test queries validate connection health

This is intentional for the target deployment environment (small restaurant).

### Sales & Table Management
- Sales (`Venta`) are linked to tables (`Mesa`) via `venta_actual_id`
- `Comanda` (kitchen order) is separate from `Venta` (customer billing)
- Multiple vendas can be managed per table; details in `VentaDetalle`
- Transfer between tables tracked in `TransferenciaMesa`

### Security
- All passwords hashed with BCrypt
- JWT tokens expire after 24 hours (configurable in `application.properties`)
- CORS enabled (`CrossOrigin` on controllers)
- Role-based access enforced at frontend (ProtectedRoute) and backend (Spring Security)

### Printing & Cash Register
- Supports serial printer via `caja.registradora.puerto` configuration (e.g., COM1)
- Tickets formatted by `FormateadorTicketService`
- Print queue managed in `ColaImpresion` table

### Audit Logging
- Automatic via AOP aspect; logs to `auditoria` table
- Tracks user, operation, timestamp, entity, and changes
- Queryable via `/api/auditoria` endpoints with filtering

## IDE Setup (Optional)

### IntelliJ IDEA / Eclipse
- Import as Maven project
- Java version: 17 (configured in pom.xml)
- Auto-format on save recommended

### VS Code (for frontend development)
- Install ESLint and TypeScript extensions
- Root `tsconfig.json` configured with path alias `@`
- Vite dev server auto-refreshes

## Common Troubleshooting

### Port Already in Use
- Backend (8080): `detener-sistema.bat` or kill process on port 8080
- Frontend (3000): Kill process on port 3000
- PostgreSQL (5432): Ensure PostgreSQL service is running

### Database Connection Failures
- Verify PostgreSQL is running: Windows Services (services.msc)
- Check credentials in `application.properties`
- Ensure database exists: `contabilidadRestaurante`

### Frontend Not Finding Backend
- Ensure backend is running on port 8080
- Check Vite proxy in `vite.config.ts` points to correct backend URL
- Browser console may show CORS errors (cross-origin requests)

## Deployment Considerations

The project is designed for **single-machine deployment** (all services on one Windows PC):
- No containerization (Docker) currently
- Backend and frontend run as native processes
- Database on local PostgreSQL
- Launcher JAR orchestrates startup for non-technical users

For production:
- Change default user passwords in `database/scripts/`
- Update JWT secret in `application.properties`
- Consider environment-specific config files
- Review HikariCP pool settings for actual hardware
- Ensure PostgreSQL backup strategy
