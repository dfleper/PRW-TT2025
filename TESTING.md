# Testing en Turbo Taller 2025 (TT2025)

Este documento describe cómo están organizadas y cómo funcionan las pruebas (tests) en TT2025. Incluye unit tests, integration tests, configuración de H2, Testcontainers y recomendaciones de ejecución.

---

# Objetivos del sistema de tests

El entorno de testing de TT2025 está diseñado para:

- Validar lógica de negocio sin depender de la base de datos.
- Validar consultas SQL críticas en una MariaDB real, igual que en producción.
- Mantener los tests rápidos y reproducibles.
- Asegurar que Flyway genera el esquema correctamente.
- Evitar falsos positivos típicos de H2.

Se utilizan **dos tipos de tests**:

- **Unit Tests** (sin DB)
- **Integration Tests** (con DB real mediante Testcontainers)

---

# 1. Unit Tests (sin base de datos)

Los unit tests usan **JUnit + Mockito** y no arrancan una base de datos real.  
Sirven para probar lógica pura, flujos de servicio y validaciones.

## Tests incluidos

```
|          Clase            |                  Ubicación                  |  Tipo  |            Cobertura             |
|---------------------------|---------------------------------------------|--------|----------------------------------|
| `AvailabilityServiceTest` |    `es.prw.features.availability.service`   |  Unit  |     Reglas de disponibilidad     |
|   `VehicleServiceTest`    |     `es.prw.features.vehicles.service`      |  Unit  |    Ownership + errores 401/404   |
```

## Características

- No requieren H2.
- No requieren Docker.
- Se ejecutan en milisegundos.
- Simulan repositorios con Mockito.
- Ideales para lógica de negocio.

## Ejemplos de validación

**AvailabilityServiceTest**
- Solapes de fecha
- Límite `end == start`
- Excluir CANCELADAS
- Cálculo de duración del servicio

**VehicleServiceTest**
- 404 si el vehículo no pertenece al usuario
- 401 si el email no existe

---

# 2. Integration Tests (con MariaDB real)

Algunas funcionalidades requieren validar SQL real.

Para eso usamos:

- **Testcontainers**
- **MariaDB 11.4**
- **Flyway migrations** (esquema real)

## Test incluido

```

|              Clase                  |    Tipo     |   DB    |        Cobertura         |
|-------------------------------------|-------------|---------|--------------------------|
|    `AppointmentRepositoryTest`      | Integration | MariaDB |   Lógica real de solapes |

```


## ✔ Cómo funciona

1. Testcontainers arranca un contenedor MariaDB.
2. Flyway aplica todas las migraciones reales.
3. Se crean User + Customer + Vehicle + Service + Appointment.
4. Se validan:
   - Solape parcial → true
   - `end == start` → false
   - Canceladas ignoradas → false
5. El contenedor se destruye automáticamente.

## ✔ ¿Por qué MariaDB real?

Porque TT2025 usa:
- ENUM
- current_timestamp
- Foreign Keys reales
- Checks (`fin > inicio`)

H2 no reproduce estas reglas con exactitud.

---

# Configuración del entorno de tests

## `application-test.properties` (para tests con H2)

```properties
spring.datasource.url=jdbc:h2:mem:tt2025_test;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect

spring.flyway.enabled=false

spring.jpa.open-in-view=false
logging.level.org.hibernate.SQL=warn
