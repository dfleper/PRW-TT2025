# TT2025 — Turbo Taller

Turbo Taller (TT2025) digitaliza la gestión de un taller de mecánica rápida. Permite a los clientes registrarse, añadir sus vehículos y reservar citas online seleccionando servicio, fecha y franja horaria. En el backoffice, el personal del taller puede gestionar la agenda, asignar trabajos, actualizar estados (pendiente/en curso/finalizada/cancelada), registrar intervenciones y piezas, y consultar el historial por cliente y vehículo. El objetivo es reducir errores, evitar solapamientos y mejorar la experiencia del cliente con un sistema centralizado y accesible desde cualquier dispositivo.

---

## STACK

Proyecto TT2025 – Turbo Taller: aplicación web Spring Boot (Java 17) con Thymeleaf + HTML/CSS/JS y MariaDB, orientada a la gestión de citas, agenda interna, órdenes de trabajo, historial y notificaciones.

---

## Stack / Tecnologías

- **Java:** 17  
- **IDE:** Spring Tool Suite **4.32.0 (STS)**
- **Framework:** Spring Boot + Thymeleaf
- **Frontend:** HTML5, CSS3, JavaScript
- **DB:** MariaDB **12.1.2**
- **Build:** Maven

---

## Requisitos

- Java **17** instalado (JAVA_HOME configurado)
- STS **4.32.0** (recomendado)
- MariaDB **12.1.2**
- Maven (o usar el wrapper `mvnw` si está incluido)

---

## Puesta en marcha (local)

### 1) Base de datos
Crea una base de datos en MariaDB:

```sql
DROP DATABASE IF EXISTS tt2025;
CREATE DATABASE tt2025 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2) Configuración del proyecto
Configura la conexión en `src/main/resources/application-dev.properties` (o `application.properties`):

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/tt2025
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD
```

> Recomendado: usar variables de entorno en lugar de credenciales en el repo.

### 3) Ejecutar la aplicación

**Opción A (STS):**
- Import → Existing Maven Project
- Run → Spring Boot App

**Opción B (terminal):**
```bash
./mvnw spring-boot:run
```

En Windows:
```bat
mvnw.cmd spring-boot:run
```

### 4) Abrir en el navegador
- App: `http://localhost:8080`

---

## Accesos y herramientas de desarrollo

### Swagger (OpenAPI)

El proyecto expone una **API REST** para catálogo, vehículos y citas, documentada mediante **Swagger / OpenAPI.**

- **URL:**
```
http://localhost:8080/swagger-ui/index.html
```

- **Acceso:**  
🔐 **Solo usuarios con rol ADMIN**

Swagger documenta únicamente endpoints REST (`@RestController`).
Las rutas MVC (`@Controller` + Thymeleaf) no aparecen en Swagger.

En producción, Swagger puede deshabilitarse mediante configuración por perfil.

---

### Actuator (monitorización)

Se utiliza **Spring Boot Actuator** para inspección técnica en entorno de desarrollo.

- Endpoints disponibles (según configuración):
```
/actuator
/actuator/health
/actuator/mappings
```

- **Acceso:**  
🔐 **Solo usuarios con rol ADMIN**

Actuator permite visualizar mappings, beans y estado de la aplicación.  
Por seguridad, no debe exponerse públicamente en producción.

---

### Dev mappings (rutas MVC + REST)

Para facilitar el desarrollo y depuración, el proyecto incluye una **vista HTML propia** que lista **todas las rutas registradas en Spring** (MVC y REST).

- **URL:**
```
http://localhost:8080/dev/mappings
```

- **Incluye:**
  - Rutas `@Controller` (Thymeleaf)
  - Rutas `@RestController`
  - Método HTTP y handler

- **Acceso:**  
🔐 **Solo usuarios con rol ADMIN**

- **Disponibilidad:**  
✔️ Solo en perfil `dev` (`@Profile("dev")`)

Esta vista es una alternativa visual a `/actuator/mappings`, pensada para desarrollo local.

---

## Seguridad y roles

El proyecto utiliza **Spring Security** con autenticación basada en formulario (`formLogin`) y control de acceso por roles.

### Roles principales

**CLIENTE**
- Perfil
- Vehículos
- Citas

**PERSONAL / TALLER**
- Agenda
- Órdenes de trabajo
- Estados de servicio

**ADMIN**
- Acceso a Swagger
- Acceso a Actuator
- Acceso a `/dev/mappings`
- Funciones de administración del sistema

---

## Zonas protegidas (resumen)

**Control de acceso por rutas**
<table>
  <thead>
    <tr>
      <th>Ruta</th>
      <th>Acceso</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>/swagger-ui/**</code>, <code>/v3/api-docs/**</code></td>
      <td style="padding-left:90px;">ADMIN</td>
    </tr>
    <tr>
      <td><code>/actuator/**</code></td>
      <td style="padding-left:90px;">ADMIN</td>
    </tr>
    <tr>
      <td><code>/dev/**</code></td>
      <td style="padding-left:90px;">ADMIN</td>
    </tr>
    <tr>
      <td><code>/cliente/**</code></td>
      <td style="padding-left:90px;">CLIENTE</td>
    </tr>
    <tr>
      <td><code>/backoffice/**</code></td>
      <td style="padding-left:90px;">PERSONAL / ADMIN</td>
    </tr>
    <tr>
      <td><code>/admin/**</code></td>
      <td style="padding-left:90px;">ADMIN</td>
    </tr>
  </tbody>
</table>

---

## Convenciones de Git

### Ramas
- `main`: estable (lista para entrega/despliegue)
- `develop`: integración
- `feature/*`: nuevas funcionalidades (desde `develop`)
  - Ej: `feature/citas`, `feature/login`
- `fix/*`: correcciones de bugs
  - Ej: `fix/solape-citas`

### Commits
Formato:
`<tipo>: <mensaje corto en infinitivo>`

- `feat:` nueva funcionalidad  
- `fix:` corrección de bug  
- `docs:` documentación  
- `chore:` mantenimiento/configuración  

Ejemplos:
- `feat: crear formulario de registro`
- `fix: evitar solapes en reservas`
- `docs: actualizar README con ejecución`
- `chore: añadir .gitignore para STS`

---

## Alcance MVP (mínimo viable)

- Registro/login + roles básicos
- Alta de vehículos
- Catálogo de servicios (listar)
- Reserva de cita con validación de disponibilidad (sin solapes)
- Backoffice: agenda diaria + cambio de estado
- Orden de trabajo: intervención + piezas + cierre
- Historial por vehículo/cliente
- Email de confirmación (mínimo)

---

## Estructura del proyecto (orientativa)

```text
es.prw
 ├─ Tt2025Application.java
 ├─ config
 │   ├─ security
 │   ├─ openapi
 │   └─ web
 ├─ common
 │   ├─ exception
 │   ├─ util
 │   └─ constants
 └─ features
     ├─ cliente
     │   ├─ vehiculos
     │   │   ├─ web
     │   │   ├─ dto
     │   │   ├─ domain
     │   │   ├─ repository
     │   │   ├─ service
     │   │   └─ validation
     │   ├─ servicios
     │   │   ├─ web
     │   │   ├─ dto
     │   │   ├─ domain
     │   │   ├─ repository
     │   │   ├─ service
     │   │   └─ validation
     │   └─ citas
     │       └─ ...
     ├─ empleado
     │   └─ ...
     └─ admin
         └─ ...
```

---

## Estado del proyecto

En desarrollo.  
Las tareas se gestionan en GitHub Projects (Backlog → Ready → In Progress → In Review → Done).

---

## Legal
- Véase 📄 **[AVISO.md](AVISO.md).** Todos los derechos reservados.
- See 📄 **[NOTICE.md](NOTICE.md).** All rights reserved.