/* ============================================================
   V7__work_order_parts_notes.sql
   - Añade notas opcionales a las piezas usadas en una OT
   ============================================================ */

ALTER TABLE work_order_parts
  ADD COLUMN IF NOT EXISTS notes VARCHAR(255) NULL AFTER total;
