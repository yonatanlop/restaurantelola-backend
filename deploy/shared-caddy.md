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

## Primer despliegue en la VM

Las imágenes ya vienen construidas por GitHub Actions (`.github/workflows/build-image.yml`
en este repo y en el de `tialola-frontend`) y publicadas en ghcr.io, así que
**solo hace falta clonar este repo** — el de `tialola-frontend` no se clona
en la VM, su build ya está empaquetado en la imagen.

```bash
git clone <url-del-repo-backend> restaurantelola
cd restaurantelola

cp .env.example .env   # completar POSTGRES_PASSWORD y JWT_SECRET reales

docker network inspect edge >/dev/null 2>&1 || docker network create edge

docker compose -f docker-compose.deploy.yml pull
docker compose -f docker-compose.deploy.yml up -d

./deploy/patch-shared-caddy.sh
```

Verificar: `https://restaurantelola.duckdns.org/` y que los demás dominios
(`gamificaciones`, `pedidoavoz`, `detectoria`) sigan funcionando igual.

## Actualizar después de un cambio de código (mismo flujo que gamificacion-fundacion)

1. `git push origin master` en el repo que cambió (backend o frontend) → el
   workflow de GitHub Actions de ese repo construye y publica la imagen
   nueva en ghcr.io (~1-2 min). Revisar en la pestaña "Actions" de ese repo.
2. Por SSH en la VM, dentro de `~/restaurantelola`:
   - Si el cambio fue solo código de la app (no tocó `docker-compose*.yml`
     ni `.env`):
     ```bash
     docker compose -f docker-compose.deploy.yml pull backend   # o frontend
     docker compose -f docker-compose.deploy.yml up -d backend  # o frontend
     ```
     (`pull`/`up -d` solo del servicio que cambió; los demás siguen
     corriendo sin tocarse)
   - Si cambió el propio `docker-compose.deploy.yml`, `.env.example` o algo
     en `deploy/`: esos archivos viven en el checkout de git de la VM, no
     dentro de las imágenes, así que primero `git pull` en `~/restaurantelola`
     antes de lo anterior.
3. Verificar con `curl` que el backend y la web respondan bien.
