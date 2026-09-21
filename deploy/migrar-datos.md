# Migración / respaldo de datos hacia la VM de Oracle Cloud

## Generar el respaldo (computadora del restaurante)

1. Copiar `respaldar-base-datos.bat` + `respaldar-base-datos.ps1` a la raíz
   del proyecto en esa computadora (junto a `pom.xml`, al mismo nivel que la
   carpeta `src/`).
2. Doble clic en `respaldar-base-datos.bat`. Es automático: detecta dónde
   está instalado PostgreSQL, lee host/puerto/base/usuario/contraseña del
   `application.properties` real de esa instalación, verifica la conexión y
   corre `pg_dump` en formato custom (`-Fc`, comprimido) con
   `--no-owner --no-privileges`.
3. El resultado queda en `backups\restaurantelola_<fecha_hora>.dump`, junto
   al script.

## Por qué el contenedor `db` usa `postgres:18-alpine` y no `16-alpine`

El `pg_dump` que genera el respaldo es el que esté instalado en cada
computadora — no lo fijamos nosotros. La primera vez que se migraron datos
(2026-09-21), la computadora del restaurante tenía **PostgreSQL 18.1**
instalado, y su `pg_dump` produce un archivo en un formato interno
(`Dump Version: 1.16`) que el `pg_restore` de PostgreSQL 16 ni siquiera
puede abrir (`unsupported version (1.16) in file header`). `pg_restore` sí
puede leer un dump generado por una versión igual o **anterior** a la suya,
nunca más nueva. Por eso el contenedor `db`, tanto en `docker-compose.yml`
(local) como en `docker-compose.deploy.yml` (VM), quedó en `postgres:18-alpine`.

Si en el futuro la computadora del restaurante tiene una versión de
PostgreSQL distinta, hay que volver a alinear la versión del contenedor
`db` con la del `pg_dump` que generó el respaldo más reciente (revisar el
encabezado del dump con `pg_restore -l archivo.dump`, línea
"Dumped by pg_dump version").

## Restaurar el dump en la VM

Requiere que el archivo `.dump` ya esté copiado a esta computadora (la de
desarrollo) — ver instrucciones de transferencia dadas caso a caso, no hay
un medio fijo (USB, correo, etc.).

```bash
# 1) Copiar el dump a la VM
scp -i <llave-ssh> restaurantelola_<fecha>.dump ubuntu@<ip-vm>:~/restaurantelola/

# 2) En la VM: detener el backend para que no escriba mientras se restaura
cd ~/restaurantelola
docker compose -f docker-compose.deploy.yml stop backend

# 3) Meter el dump dentro del contenedor db y restaurar
docker cp restaurantelola_<fecha>.dump restaurantelola-db-1:/tmp/backup.dump
docker exec restaurantelola-db-1 pg_restore -U postgres -d contabilidadRestaurante \
    --clean --if-exists --no-owner --no-privileges -v /tmp/backup.dump
docker exec restaurantelola-db-1 rm /tmp/backup.dump
rm restaurantelola_<fecha>.dump   # ya no hace falta en el checkout de git

# 4) Volver a levantar el backend
docker compose -f docker-compose.deploy.yml start backend
```

`--clean --if-exists` hace que primero borre las tablas vacías que Hibernate
ya había creado al arrancar el backend contra la base nueva, y las recree
con los datos del dump — seguro porque, antes de la primera restauración,
esa base no tiene datos reales que perder.

## Verificar después de restaurar

```bash
curl -s -o /dev/null -w "%{http_code}\n" https://restaurantelola.duckdns.org/
docker exec restaurantelola-db-1 psql -U postgres -d contabilidadRestaurante -c "select count(*) from usuarios;"
```

Y probar login real (`dueno` / `cajero`) desde `https://restaurantelola.duckdns.org/`.
