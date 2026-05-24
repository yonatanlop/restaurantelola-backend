# Acceso por IP - Restaurante Doña Lola

## Configuración Completada

Se ha configurado el sistema para que funcione tanto con `localhost` como con la IP de tu computadora.

### Cambios Realizados

1. **Backend (Spring Boot)**: Configurado para escuchar en todas las interfaces de red (`0.0.0.0`)
2. **Frontend (Vite)**: Configurado para escuchar en todas las interfaces de red (`0.0.0.0`)
3. **httpClient**: Detecta automáticamente si debe usar `localhost` o la IP actual

## Cómo Usar

### 1. Obtener tu IP local

**Windows:**
```cmd
ipconfig
```
Busca "Dirección IPv4" en la sección de tu adaptador de red activo (por ejemplo: `192.168.1.100`)

**Linux/Mac:**
```bash
ip addr show
# o
ifconfig
```

### 2. Iniciar los Servidores

**Backend:**
```bash
mvn spring-boot:run
```
El backend estará disponible en:
- `http://localhost:8080`
- `http://TU_IP:8080` (ejemplo: `http://192.168.1.100:8080`)

**Frontend:**
```bash
cd tialola-frontend
npm run dev
```
El frontend estará disponible en:
- `http://localhost:3000`
- `http://TU_IP:3000` (ejemplo: `http://192.168.1.100:3000`)

### 3. Acceder desde Otra Máquina

1. Asegúrate de que ambas máquinas estén en la misma red
2. Abre el navegador en la otra máquina y accede a: `http://TU_IP:3000`
3. El sistema detectará automáticamente que debe usar la IP para las peticiones al backend

## Configuración Avanzada (Opcional)

Si necesitas usar una URL específica del backend, puedes crear un archivo `.env` en `tialola-frontend/`:

```env
VITE_API_URL=http://192.168.1.100:8080
```

Esto sobrescribirá la detección automática.

## Notas Importantes

- **Firewall**: Asegúrate de que el firewall de Windows permita conexiones en los puertos 3000 y 8080
- **Misma Red**: Las máquinas deben estar en la misma red local para acceder por IP
- **CORS**: Ya está configurado para permitir acceso desde cualquier origen
- **Seguridad**: Esta configuración es para desarrollo. En producción, considera restricciones de seguridad aprobadas

## Solución de Problemas

### No puedo acceder desde otra máquina

1. Verifica que el firewall permita los puertos 3000 y 8080
2. Asegúrate de que ambas máquinas estén en la misma red
3. Verifica que los servidores estén escuchando en `0.0.0.0` (ya configurado)

### Error de conexión al backend

1. Verifica que el backend esté corriendo
2. Prueba acceder directamente a `http://TU_IP:8080/api/auth/login` desde el navegador
3. Revisa la consola del navegador para ver errores de CORS

