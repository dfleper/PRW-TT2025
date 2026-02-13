/* ============================================================
   TURBO TALLER (TT2025) - FLYWAY V2__seed_base_data.sql
   ------------------------------------------------------------
   Seed mínimo:
   - Roles base
   - Servicios base (catálogo)
   ------------------------------------------------------------
   Nota:
   - Uso ON DUPLICATE KEY UPDATE para que, si por cualquier motivo
     la BD ya tuviera esos registros (p.ej. restauración), no falle.
   ============================================================ */

-- ============================================================
-- 1) ROLES BASE
-- ============================================================

INSERT INTO roles (nombre) VALUES
  ('CLIENTE'),
  ('RECEPCION'),
  ('MECANICO'),
  ('JEFE_TALLER'),
  ('ADMIN')
ON DUPLICATE KEY UPDATE
  nombre = VALUES(nombre);

-- ============================================================
-- 2) SERVICIOS BASE (5–10)
-- ============================================================

INSERT INTO services (codigo, nombre, descripcion, precio_base, minutos_estimados, activo) VALUES
  ('REV_BAS',     'Revisión básica',              'Chequeo rápido: niveles, luces, presión y diagnóstico visual.',     29.90,  30, 1),
  ('CAM_ACEITE',  'Cambio de aceite y filtro',    'Sustitución de aceite + filtro (material no incluido si aplica).', 79.90,  60, 1),
  ('PAST_FRE',    'Cambio de pastillas de freno', 'Sustitución de pastillas (eje) y revisión del sistema.',            99.00,  90, 1),
  ('DIAG_OBD',    'Diagnóstico OBD',              'Lectura de averías y reporte básico de códigos.',                   35.00,  30, 1),
  ('BATERIA',     'Sustitución de batería',       'Revisión y sustitución de batería (material no incluido).',         25.00,  30, 1),
  ('AIRE_AC',     'Carga de aire acondicionado',  'Carga de gas A/C + comprobación básica de estanqueidad.',           59.00,  45, 1),
  ('ALINEA',      'Alineación de dirección',      'Ajuste de paralelismo/dirección y comprobación de desgaste.',        39.90,  45, 1),
  ('NEUM',        'Cambio de neumáticos',         'Montaje y equilibrado (precio por 2 neumáticos, sin material).',     50.00,  60, 1),
  ('ITV_PREP',    'Preparación ITV',              'Revisión orientada a ITV: luces, emisiones, frenos y seguridad.',    45.00,  45, 1),
  ('LIMPIEZA',    'Limpieza de inyectores',       'Limpieza básica / aditivo (según tipo de motor y disponibilidad).',  49.00,  45, 1)
ON DUPLICATE KEY UPDATE
  nombre = VALUES(nombre),
  descripcion = VALUES(descripcion),
  precio_base = VALUES(precio_base),
  minutos_estimados = VALUES(minutos_estimados),
  activo = VALUES(activo);
