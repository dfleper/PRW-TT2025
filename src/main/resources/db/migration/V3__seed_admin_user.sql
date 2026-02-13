/* ============================================================
  TURBO TALLER (TT2025) - FLYWAY V3__seed_admin_user.sql
   ------------------------------------------------------------
   Credenciales:
   - admin@tt2025.local      / 1234   (ROL: ADMIN)
   - cliente@tt2025.local    / 1234   (ROL: CLIENTE)
   - recepcion@tt2025.local  / 1234   (ROL: RECEPCION)
   - mecanico@tt2025.local   / 1234   (ROL: MECANICO)
   - jefetaller@tt2025.local / 1234   (ROL: JEFE_TALLER)
   ------------------------------------------------------------
   Notas:
   - Idempotente (puedes ejecutar en dev sin duplicar).
   - Incluye created_at/updated_at por compatibilidad.
   ============================================================ */

-- =========================
-- 1) ROLES DEL SISTEMA
-- =========================
INSERT INTO roles (nombre) VALUES ('ADMIN')
ON DUPLICATE KEY UPDATE nombre = nombre;

INSERT INTO roles (nombre) VALUES ('CLIENTE')
ON DUPLICATE KEY UPDATE nombre = nombre;

INSERT INTO roles (nombre) VALUES ('RECEPCION')
ON DUPLICATE KEY UPDATE nombre = nombre;

INSERT INTO roles (nombre) VALUES ('MECANICO')
ON DUPLICATE KEY UPDATE nombre = nombre;

INSERT INTO roles (nombre) VALUES ('JEFE_TALLER')
ON DUPLICATE KEY UPDATE nombre = nombre;


-- =========================
-- Hash BCrypt para "1234" (válido para BCryptPasswordEncoder):
-- $2b$10$zH36n8v9F/ay9TYuxJqD6ODNTTQpTTLI2knmTrkfGBfWl7YH.Qxgm
-- =========================


-- =========================
-- 2) USUARIO ADMIN (1234)
-- =========================
INSERT INTO users (email, password_hash, nombre, apellidos, telefono, activo, created_at, updated_at)
VALUES (
  'admin@tt2025.local',
  '$2b$10$zH36n8v9F/ay9TYuxJqD6ODNTTQpTTLI2knmTrkfGBfWl7YH.Qxgm',
  'Admin',
  'Turbo Taller',
  NULL,
  1,
  NOW(),
  NOW()
)
ON DUPLICATE KEY UPDATE
  password_hash = VALUES(password_hash),
  nombre        = VALUES(nombre),
  apellidos     = VALUES(apellidos),
  telefono      = VALUES(telefono),
  activo        = VALUES(activo),
  updated_at    = NOW();

-- Asignación rol ADMIN (idempotente)
INSERT IGNORE INTO user_roles (id_user, id_role)
SELECT u.id_user, r.id_role
FROM users u
JOIN roles r ON r.nombre = 'ADMIN'
WHERE u.email = 'admin@tt2025.local';


-- =========================
-- 3) USUARIO CLIENTE DEMO (1234)
-- =========================
INSERT INTO users (email, password_hash, nombre, apellidos, telefono, activo, created_at, updated_at)
VALUES (
  'cliente@tt2025.local',
  '$2b$10$zH36n8v9F/ay9TYuxJqD6ODNTTQpTTLI2knmTrkfGBfWl7YH.Qxgm',
  'Cliente',
  'Demo',
  NULL,
  1,
  NOW(),
  NOW()
)
ON DUPLICATE KEY UPDATE
  password_hash = VALUES(password_hash),
  nombre        = VALUES(nombre),
  apellidos     = VALUES(apellidos),
  telefono      = VALUES(telefono),
  activo        = VALUES(activo),
  updated_at    = NOW();

-- Asignación rol CLIENTE (idempotente)
INSERT IGNORE INTO user_roles (id_user, id_role)
SELECT u.id_user, r.id_role
FROM users u
JOIN roles r ON r.nombre = 'CLIENTE'
WHERE u.email = 'cliente@tt2025.local';


-- =========================
-- 4) USUARIO RECEPCIÓN DEMO (1234)
-- =========================
INSERT INTO users (email, password_hash, nombre, apellidos, telefono, activo, created_at, updated_at)
VALUES (
  'recepcion@tt2025.local',
  '$2b$10$zH36n8v9F/ay9TYuxJqD6ODNTTQpTTLI2knmTrkfGBfWl7YH.Qxgm',
  'Recepcion',
  'Demo',
  NULL,
  1,
  NOW(),
  NOW()
)
ON DUPLICATE KEY UPDATE
  password_hash = VALUES(password_hash),
  nombre        = VALUES(nombre),
  apellidos     = VALUES(apellidos),
  telefono      = VALUES(telefono),
  activo        = VALUES(activo),
  updated_at    = NOW();

-- Asignación rol RECEPCION (idempotente)
INSERT IGNORE INTO user_roles (id_user, id_role)
SELECT u.id_user, r.id_role
FROM users u
JOIN roles r ON r.nombre = 'RECEPCION'
WHERE u.email = 'recepcion@tt2025.local';


-- =========================
-- 5) USUARIO MECÁNICO DEMO (1234)
-- =========================
INSERT INTO users (email, password_hash, nombre, apellidos, telefono, activo, created_at, updated_at)
VALUES (
  'mecanico@tt2025.local',
  '$2b$10$zH36n8v9F/ay9TYuxJqD6ODNTTQpTTLI2knmTrkfGBfWl7YH.Qxgm',
  'Mecanico',
  'Demo',
  NULL,
  1,
  NOW(),
  NOW()
)
ON DUPLICATE KEY UPDATE
  password_hash = VALUES(password_hash),
  nombre        = VALUES(nombre),
  apellidos     = VALUES(apellidos),
  telefono      = VALUES(telefono),
  activo        = VALUES(activo),
  updated_at    = NOW();

-- Asignación rol MECANICO (idempotente)
INSERT IGNORE INTO user_roles (id_user, id_role)
SELECT u.id_user, r.id_role
FROM users u
JOIN roles r ON r.nombre = 'MECANICO'
WHERE u.email = 'mecanico@tt2025.local';


-- =========================
-- 6) USUARIO JEFE DE TALLER DEMO (1234)
-- =========================
INSERT INTO users (email, password_hash, nombre, apellidos, telefono, activo, created_at, updated_at)
VALUES (
  'jefetaller@tt2025.local',
  '$2b$10$zH36n8v9F/ay9TYuxJqD6ODNTTQpTTLI2knmTrkfGBfWl7YH.Qxgm',
  'Jefe',
  'Taller',
  NULL,
  1,
  NOW(),
  NOW()
)
ON DUPLICATE KEY UPDATE
  password_hash = VALUES(password_hash),
  nombre        = VALUES(nombre),
  apellidos     = VALUES(apellidos),
  telefono      = VALUES(telefono),
  activo        = VALUES(activo),
  updated_at    = NOW();

-- Asignación rol JEFE_TALLER (idempotente)
INSERT IGNORE INTO user_roles (id_user, id_role)
SELECT u.id_user, r.id_role
FROM users u
JOIN roles r ON r.nombre = 'JEFE_TALLER'
WHERE u.email = 'jefetaller@tt2025.local';
