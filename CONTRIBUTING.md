# Contributing — TT2025 (Turbo Taller)

Gracias por contribuir a **TT2025 — Turbo Taller**. Este documento define cómo trabajar en el repositorio para mantener un flujo limpio y consistente.

---

## 1) Requisitos del entorno

- **Java:** 17  
- **IDE recomendado:** Spring Tool Suite (STS) **4.32.0**  
- **DB:** MariaDB **12.1.2**  
- **Build:** Maven  

---

## 2) Flujo de trabajo (Branching)

### Ramas principales
- **`main`**: estable (lista para entrega/despliegue).
- **`develop`**: integración de trabajo (base para features y fixes).

### Ramas de trabajo
- **`feature/*`**: funcionalidades nuevas.
  - Ej: `feature/login`, `feature/appointments`
- **`fix/*`**: correcciones de bugs.
  - Ej: `fix/overlap-validation`, `fix/login-redirect`

**Regla:** crea tu rama desde `develop` y abre PR hacia `develop`.

---

## 3) Convención de commits

Formato:
`<type>: <short message in imperative>`

Tipos permitidos:
- **`feat:`** nueva funcionalidad
- **`fix:`** corrección de bug
- **`docs:`** documentación
- **`chore:`** mantenimiento / configuración
- **`refactor:`** refactor sin cambios funcionales visibles
- **`test:`** añadir o mejorar tests

Ejemplos:
- `feat: implement appointment booking`
- `fix: prevent appointment overlaps`
- `docs: update setup instructions`
- `chore: add gitignore for STS`
- `refactor: simplify service validation`
- `test: add unit tests for availability`

---

## 4) Estilo de código (Java)

- Mantener **nombres claros** y consistentes (inglés recomendado).
- Evitar lógica compleja en controladores: mover a **Services**.
- Validaciones en:
  - DTOs (Bean Validation) cuando aplique
  - Servicios para reglas de negocio (ej. solapes)
- No hardcodear credenciales ni secretos.
- Evitar código duplicado: extraer métodos/clases si se repite.
- Mantener métodos pequeños y con una responsabilidad.

---

## 5) Convenciones de estructura (orientativa)

```text
src/main/java/...
  controller/   -> controladores web
  service/      -> lógica de negocio
  repository/   -> acceso a datos (JPA/DAO)
  model/        -> entidades / dominio
  dto/          -> objetos de transferencia
  config/       -> configuración (security, etc.)

src/main/resources/
  templates/    -> Thymeleaf
  static/       -> css/js/img
```

---

## 6) Base de datos y configuración

- No subir credenciales al repositorio.
- Usar perfiles por entorno (ej. `dev` / `prod`).
- Mantener scripts/migraciones versionados (Flyway/Liquibase si aplica).
- Cualquier cambio de esquema debe quedar reflejado en migraciones y/o documentación.

---

## 7) Issues, labels y definición de tareas

Antes de programar, crea un Issue o selecciona uno del Project.

Labels recomendados:

**Áreas**
- `backend`, `frontend`, `db`, `security`, `devops`, `testing`, `docs`

**Prioridad**
- `P0-MVP`: imprescindible para que exista el MVP
- `P1`: importante, pero no bloquea el MVP
- `P2`: si sobra tiempo

**Tamaño**
- `S`: 1–3h
- `M`: 4–8h
- `L`: 1–2 días (8–16h)
- `XL`: 3–5 días (24–40h) (recomendado partir)

**Tipo**
- `feature`, `bug`, `chore`, `refactor`, `enhancement`

---

## 8) Pull Requests (PR)

### Antes de abrir PR
- [ ] La rama está actualizada con `develop`
- [ ] Compila y arranca en local
- [ ] No rompe funcionalidades existentes
- [ ] Incluye capturas o notas si hay cambios UI
- [ ] Añade/actualiza tests si aplica
- [ ] No incluye secretos (passwords, tokens, keys)

### Contenido del PR
Incluye:
- **Qué se hizo** (resumen)
- **Cómo probarlo** (pasos)
- **Issue relacionado** (ej. `Closes #12`)

---

## 9) Cómo reportar bugs

Crea un Issue con:
- Pasos para reproducir
- Resultado esperado vs actual
- Logs/capturas
- Entorno (Java, DB, SO)

---

## 10) Notas finales

- Si una tarea es **XL**, divídela en varias tarjetas más pequeñas.
- Si una decisión técnica afecta a varias partes del sistema, documenta la decisión en el Issue.
- Mantén el Project actualizado moviendo tarjetas por columnas (Backlog → Ready → In Progress → In Review → Done).
