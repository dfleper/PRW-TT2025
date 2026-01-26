/* ============================================================
   V6__unique_vin_vehicles.sql
   - VIN opcional pero único cuando se informa
   ============================================================ */

ALTER TABLE vehicles
  ADD CONSTRAINT uk_vehicles_vin UNIQUE (vin);
