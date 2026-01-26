# TT2025 — Turbo Taller

Turbo Taller (TT2025) digitaliza la gestión de un taller de mecánica rápida. Permite a los clientes registrarse, añadir sus vehículos y reservar citas online seleccionando servicio, fecha y franja horaria. En el backoffice, el personal del taller puede gestionar la agenda, asignar trabajos, actualizar estados (pendiente/en curso/finalizada/cancelada), registrar intervenciones y piezas, y consultar el historial por cliente y vehículo. El objetivo es reducir errores, evitar solapamientos y mejorar la experiencia del cliente con un sistema centralizado y accesible desde cualquier dispositivo.

---

## STACK

Proyecto DAW TT2025 – Turbo Taller: aplicación web Spring Boot (Java 17) con Thymeleaf + HTML/CSS/JS y MariaDB, orientada a la gestión de citas, agenda interna, órdenes de trabajo, historial y notificaciones.

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

## Roles (idea base)
- **CLIENTE:** gestiona perfil, vehículos y citas.
- **PERSONAL / TALLER:** gestiona agenda, estados, órdenes de trabajo.
- **ADMIN:** administración del sistema (si aplica).

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

En desarrollo (DAW 2025).  
Las tareas se gestionan en GitHub Projects (Backlog → Ready → In Progress → In Review → Done).

## Legal
- Véase AVISO.md. Todos los derechos reservados.
- See NOTICE.md. All rights reserved.