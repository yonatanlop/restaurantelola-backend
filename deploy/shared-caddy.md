# Integración con el Caddy compartido de otros proyectos

Esta VM ya corre otros proyectos (`gamificacion-fundacion`, `pedidosavoz`,
`detectoria`) cuyo contenedor `caddy` (del proyecto `gamificacion-fundacion`)
es el único proceso escuchando en los puertos 80/443 de la IP pública. Solo
un proceso puede escuchar en esos puertos, así que Restaurante Lola no puede
traer su propio Caddy — tiene que sumarse al que ya existe (mismo patrón
usado antes para integrar `pedidosavoz` y `detectoria`).

**Decisión explícita**: no se modifica el repositorio ni el `docker-compose`
de `gamificacion-fundacion`. En su lugar, `deploy/patch-shared-caddy.sh` hace
dos cosas en caliente sobre el contenedor `caddy` que ya está corriendo:

1. Lo conecta a la red Docker compartida `edge` (ya existe, creada para
   `detectoria`/`pedidosavoz`), sin desconectarlo de la suya propia — los
   sitios existentes siguen funcionando exactamente igual.
2. Le agrega un bloque de sitio más al Caddyfile en ejecución (`docker exec`
   + `caddy reload`, sin reiniciar el contenedor, sin downtime) que enruta
   `restaurantelola.duckdns.org` hacia `restaurantelola-web:80` en esa red.

El servicio `frontend` se conecta a esa misma red `edge` con
`container_name: restaurantelola-web` (nombre único) vía
`docker-compose.deploy.yml`, para evitar la colisión de resolución DNS que ya
ocurrió una vez cuando dos proyectos distintos usaban el mismo nombre de
servicio (`web`) en la red compartida.

## Importante: esto no es persistente

El `docker-compose.prod.yml` de `gamificacion-fundacion` regenera el
Caddyfile desde cero (solo con su propio dominio) cada vez que ese contenedor
arranca. Como no tocamos ese archivo, **si el contenedor `caddy` de ese otro
proyecto se recrea** (un redeploy de `gamificacion-fundacion`, un reinicio de
la VM, etc.), el bloque de Restaurante Lola se pierde y hay que volver a
correr:

```bash
./deploy/patch-shared-caddy.sh
```

Es idempotente — se puede correr las veces que haga falta sin duplicar nada
ni afectar el sitio de los otros proyectos.

## Orden de despliegue en la VM

```bash
git clone <url-del-repo-backend> restaurantelola
cd restaurantelola
git clone <url-del-repo-frontend> tialola-frontend

cp .env.example .env   # completar POSTGRES_PASSWORD y JWT_SECRET reales

docker compose -f docker-compose.yml -f docker-compose.deploy.yml up -d --build

./deploy/patch-shared-caddy.sh
```

Verificar: `https://restaurantelola.duckdns.org/` y que los demás dominios
(`gamificaciones`, `pedidoavoz`, `detectoria`) sigan funcionando igual.
