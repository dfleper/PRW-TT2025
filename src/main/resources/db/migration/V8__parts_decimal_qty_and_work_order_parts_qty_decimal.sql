/* ============================================================
   V8__parts_decimal_qty.sql
   - parts: añade allows_decimal (si no existe)
   - work_order_parts: cantidad -> DECIMAL(10,2)
   - decimales SOLO para aceite (no filtros)
   ============================================================ */

-- 1) Añadir flag allows_decimal si NO existe
SET @col_exists := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'parts'
    AND COLUMN_NAME = 'allows_decimal'
);

SET @sql := IF(
  @col_exists = 0,
  'ALTER TABLE parts ADD COLUMN allows_decimal TINYINT(1) NOT NULL DEFAULT 0 AFTER stock_qty',
  'SELECT 1'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2) Cambiar cantidad en work_order_parts para permitir decimales
ALTER TABLE work_order_parts
  MODIFY COLUMN cantidad DECIMAL(10,2) NOT NULL DEFAULT 1.00;

-- 3) Reset (por si antes marcaste de más)
UPDATE parts SET allows_decimal = 0;

-- 4) Marcar SOLO aceite como decimal:
--    ✅ Reglas:
--    - sku empieza por 'ACE-'  -> decimal
--    - sku empieza por 'FIL-'  -> NO decimal (ej: FIL-OIL-STD)
UPDATE parts
SET allows_decimal = 1
WHERE sku LIKE 'ACE-%'
  AND sku NOT LIKE 'FIL-%';
