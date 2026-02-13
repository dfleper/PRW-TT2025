/* ============================================================
   TT2025 - FLYWAY V9__seed_parts_base_data.sql
   ------------------------------------------------------------
   Seed de piezas base (alineadas con catálogo de servicios).
   - Idempotente por uk_parts_sku
   - Asigna auditoría al admin si existe
   ============================================================ */

-- Inserta piezas (created_by/updated_by se asigna luego con UPDATE)
INSERT INTO parts (sku, nombre, descripcion, precio_unit, stock_qty, allows_decimal, activo)
VALUES
  -- CAM_ACEITE
  ('ACEITE_5W30_1L',  'Aceite 5W30 (1L)',           'Aceite motor 5W30. Se usa por litros.',             12.50,  50, 1, 1),
  ('FILTRO_ACEITE_STD','Filtro de aceite (std)',     'Filtro de aceite estándar.',                         9.90,  25, 0, 1),
  ('ARANDELA_TAPON',  'Arandela tapón cárter',      'Arandela para tapón de vaciado.',                    0.60, 100, 0, 1),

  -- PAST_FRE
  ('PASTILLAS_FRENO_DEL','Pastillas freno delanteras','Juego pastillas freno eje delantero.',            45.00,  10, 0, 1),
  ('PASTILLAS_FRENO_TRAS','Pastillas freno traseras', 'Juego pastillas freno eje trasero.',              39.00,  10, 0, 1),
  ('LIQUIDO_FRENOS_DOT4','Líquido frenos DOT4 (1L)',  'Líquido de frenos DOT4. Se usa por unidades.',     8.50,  20, 0, 1),

  -- BATERIA
  ('BATERIA_12V_STD', 'Batería 12V (std)',          'Batería 12V estándar (según compatibilidad).',     85.00,   6, 0, 1),

  -- AIRE_AC
  ('GAS_R134A_100G',  'Gas A/C R134a (100g)',       'Gas refrigerante R134a. Se usa por unidades.',      6.00,  30, 0, 1),

  -- NEUM
  ('VALVULA_NEUM',    'Válvula neumático',          'Válvula estándar para neumático.',                  1.50, 100, 0, 1),
  ('PLOMOS_EQ_10G',   'Plomos equilibrado (10g)',   'Peso equilibrado 10g. Se usa por unidades.',        0.20, 300, 0, 1),

  -- LIMPIEZA
  ('ADITIVO_INY',     'Aditivo limpia inyectores',  'Aditivo limpia inyectores (básico).',              12.00,  15, 0, 1)

ON DUPLICATE KEY UPDATE
  nombre         = VALUES(nombre),
  descripcion    = VALUES(descripcion),
  precio_unit    = VALUES(precio_unit),
  stock_qty      = VALUES(stock_qty),
  allows_decimal = VALUES(allows_decimal),
  activo         = VALUES(activo);

-- Auditoría: asigna created_by/updated_by al admin si existe (solo si está NULL)
UPDATE parts p
JOIN users u ON u.email = 'admin@tt2025.local'
SET
  p.created_by_user = COALESCE(p.created_by_user, u.id_user),
  p.updated_by_user = COALESCE(p.updated_by_user, u.id_user)
WHERE p.created_by_user IS NULL OR p.updated_by_user IS NULL;
