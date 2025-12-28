/* ============================================================
   TT2025 - SEED USUARIOS (ADMIN + CLIENTE opcional)
   Credenciales:
   - admin@tt2025.local / 1234
   - cliente@tt2025.local / 1234 (opcional)
   ============================================================ */

-- 1) Roles mínimos
INSERT INTO roles (nombre) VALUES ('ADMIN')
ON DUPLICATE KEY UPDATE nombre = nombre;

INSERT INTO roles (nombre) VALUES ('CLIENTE')
ON DUPLICATE KEY UPDATE nombre = nombre;

-- 2) Usuario ADMIN (password 1234 en BCrypt)
-- Hash BCrypt para "1234":
-- $2b$10$zH36n8v9F/ay9TYuxJqD6ODNTTQpTTLI2knmTrkfGBfWl7YH.Qxgm
INSERT INTO users (email, password_hash, nombre, apellidos, telefono, activo)
VALUES (
  'admin@tt2025.local',
  '$2b$10$zH36n8v9F/ay9TYuxJqD6ODNTTQpTTLI2knmTrkfGBfWl7YH.Qxgm',
  'Admin',
  'Turbo Taller',
  NULL,
  1
)
ON DUPLICATE KEY UPDATE
  password_hash = VALUES(password_hash),
  nombre = VALUES(nombre),
  apellidos = VALUES(apellidos),
  telefono = VALUES(telefono),
  activo = VALUES(activo);

-- Asignar rol ADMIN al usuario admin (idempotente)
INSERT IGNORE INTO user_roles (id_user, id_role)
SELECT u.id_user, r.id_role
FROM users u
JOIN roles r ON r.nombre = 'ADMIN'
WHERE u.email = 'admin@tt2025.local';

-- 3) (OPCIONAL) Usuario demo CLIENTE (password 1234 en BCrypt)
-- Si no lo quieres, puedes borrar este bloque completo.
INSERT INTO users (email, password_hash, nombre, apellidos, telefono, activo)
VALUES (
  'cliente@tt2025.local',
  '$2b$10$zH36n8v9F/ay9TYuxJqD6ODNTTQpTTLI2knmTrkfGBfWl7YH.Qxgm',
  'Cliente',
  'Demo',
  NULL,
  1
)
ON DUPLICATE KEY UPDATE
  password_hash = VALUES(password_hash),
  nombre = VALUES(nombre),
  apellidos = VALUES(apellidos),
  telefono = VALUES(telefono),
  activo = VALUES(activo);

INSERT IGNORE INTO user_roles (id_user, id_role)
SELECT u.id_user, r.id_role
FROM users u
JOIN roles r ON r.nombre = 'CLIENTE'
WHERE u.email = 'cliente@tt2025.local';
