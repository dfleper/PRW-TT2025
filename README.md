![GitHub repo size](https://img.shields.io/github/repo-size/dfleper/PRW-TT2025?logo=github)
![GitHub last commit](https://img.shields.io/github/last-commit/dfleper/PRW-TT2025?color=blue&label=last-commit&logo=github&logoColor=white)

<div style="display: flex; align-items: center; gap: 12px;">
  <img src="./screenshots/logo.png" alt="TT2025 Logo" width="60">
</div>

# DEPLOY TT2025 — Turbo Taller 

## Deploy (Render + Docker + MariaDB Cloud 11.8 LTS)

Este proyecto se despliega en **Render** utilizando **Docker**, ejecutando el binario **WAR** generado por Maven, y conectando a una base de datos externa **MariaDB Cloud**.

### Requisitos técnicos (según el proyecto)
- **Java 17**
- **Spring Boot 3.5.8**
- **Maven 3.9** (se usa dentro del Docker build)
- Artefacto: **WAR** (`target/TT2025.war`)
- Base de datos: **MariaDB Cloud 11.8 LTS**

---

### 1) Archivos clave del despliegue

En la raíz del repositorio deben existir:

- `Dockerfile`
- `.dockerignore`
- `pom.xml`
- `src/`
- `src/main/resources/application-prod.properties`
- `src/main/resources/application.properties`

El `Dockerfile` compila el proyecto y ejecuta el WAR como “WAR ejecutable”:

- Build: `mvn -q -DskipTests clean package`
- Run: `java -jar /app/app.war`

---

### 2) Configuración en Render (variables de entorno)

En Render → *Dashboard* → *Environment* definir:

**Obligatorias**
- `SPRING_PROFILES_ACTIVE=prod`
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

**Opcionales (pool Hikari)**
- `TT2025_DB_POOL_MAX` (por defecto 5)
- `TT2025_DB_POOL_MIN` (por defecto 0)

**Puerto (Render)**
- Render inyecta `PORT`.  
  La app lo soporta mediante:
  - `server.port=${PORT:8080}` (en `application-prod.properties`)

---

### 3) Base de datos (MariaDB Cloud)

La app usa Flyway para migraciones:

- `spring.flyway.enabled=true`
- `spring.flyway.locations=classpath:db/migration`

En producción:
- Hibernate NO crea ni modifica tablas (`spring.jpa.hibernate.ddl-auto=validate`)
- Flyway aplica las migraciones al arrancar.

**Nota TLS/SSL (MariaDB Cloud)**
Si el proveedor exige CA cert, la ruta debe ser **Linux dentro del contenedor**, no rutas Windows.
En ese caso, ajustar la URL JDBC directamente en Render.

---

### 4) Despliegue

1. Crear un **Web Service** en Render.
2. Conectar el repositorio GitHub.
3. Render detectará `Dockerfile` y construirá la imagen.
4. Configurar variables de entorno.
5. Lanzar el deploy y revisar logs.

---

### 5) Verificación de éxito

- El servicio queda en estado **Running**.
- No hay errores de conexión a MariaDB.
- Flyway aplica migraciones sin fallos.
- La app escucha en el puerto inyectado por Render (`PORT`).

## Legal
- Véase 📄 **[AVISO.md](AVISO.md).** Todos los derechos reservados.
- See 📄 **[NOTICE.md](NOTICE.md).** All rights reserved.

