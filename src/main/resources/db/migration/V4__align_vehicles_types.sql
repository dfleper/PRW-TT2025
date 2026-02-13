/* ============================================================
   TURBO TALLER (TT2025) - FLYWAY V4__align_vehicles_types.sql
   ------------------------------------------------------------
   Ajustes de estructura en tabla vehicles:
   - Alinea tipo de datos con el modelo JPA actual.
   - anio → SMALLINT UNSIGNED NULL
   - notas → TEXT NULL
   ------------------------------------------------------------
   Nota:
   - Migración correctiva sobre esquema existente.
   - No modifica datos, solo definición de columnas.
   ============================================================ */

ALTER TABLE vehicles
  MODIFY COLUMN anio SMALLINT UNSIGNED NULL;

ALTER TABLE vehicles
  MODIFY COLUMN notas TEXT NULL;
