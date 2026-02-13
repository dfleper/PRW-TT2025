/* ============================================================
   TURBO TALLER (TT2025) - FLYWAY V6__unique_vin_vehicles.sql
   ------------------------------------------------------------
   Ajustes:
   - VIN opcional pero único cuando se informa
   ============================================================ */

ALTER TABLE vehicles
  ADD CONSTRAINT uk_vehicles_vin UNIQUE (vin);
