/* ============================================================
   TT2025 - V3 SEED: ROLES + USUARIOS DEMO
   ------------------------------------------------------------
   Credenciales:
   - admin@tt2025.local   / 1234   (ROL: ADMIN)
   - cliente@tt2025.local / 1234   (ROL: CLIENTE)
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
-- 2) USUARIO ADMIN (1234)
-- =========================
-- Hash BCrypt para "1234" (válido para BCryptPasswordEncoder):
-- $2b$10$zH36n8v9F/ay9TYuxJqD6ODNTTQpTTLI2knmTrkfGBfWl7YH.Qxgm
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
