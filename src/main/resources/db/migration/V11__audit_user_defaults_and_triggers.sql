/* ============================================================
   TT2025 - FLYWAY V11__audit_user_defaults_and_triggers.sql
   ------------------------------------------------------------
   Objetivo:
   - Backfill de created_by_user / updated_by_user a admin cuando estén NULL.
   - Garantizar en INSERT/UPDATE que, si la app no informa usuario,
     se use admin por defecto sin romper flujos actuales.
   ============================================================ */

-- id admin fijo por email semilla
SET @admin_user_id := (
  SELECT u.id_user
  FROM users u
  WHERE u.email = 'admin@tt2025.local'
  LIMIT 1
);

-- =========================
-- Backfill datos existentes
-- =========================
UPDATE users SET
  created_by_user = COALESCE(created_by_user, @admin_user_id),
  updated_by_user = COALESCE(updated_by_user, created_by_user, @admin_user_id)
WHERE created_by_user IS NULL OR updated_by_user IS NULL;

UPDATE roles SET
  created_by_user = COALESCE(created_by_user, @admin_user_id),
  updated_by_user = COALESCE(updated_by_user, created_by_user, @admin_user_id)
WHERE created_by_user IS NULL OR updated_by_user IS NULL;

UPDATE user_roles SET
  created_by_user = COALESCE(created_by_user, @admin_user_id),
  updated_by_user = COALESCE(updated_by_user, created_by_user, @admin_user_id)
WHERE created_by_user IS NULL OR updated_by_user IS NULL;

UPDATE customers SET
  created_by_user = COALESCE(created_by_user, @admin_user_id),
  updated_by_user = COALESCE(updated_by_user, created_by_user, @admin_user_id)
WHERE created_by_user IS NULL OR updated_by_user IS NULL;

UPDATE employees SET
  created_by_user = COALESCE(created_by_user, @admin_user_id),
  updated_by_user = COALESCE(updated_by_user, created_by_user, @admin_user_id)
WHERE created_by_user IS NULL OR updated_by_user IS NULL;

UPDATE vehicles SET
  created_by_user = COALESCE(created_by_user, @admin_user_id),
  updated_by_user = COALESCE(updated_by_user, created_by_user, @admin_user_id)
WHERE created_by_user IS NULL OR updated_by_user IS NULL;

UPDATE services SET
  created_by_user = COALESCE(created_by_user, @admin_user_id),
  updated_by_user = COALESCE(updated_by_user, created_by_user, @admin_user_id)
WHERE created_by_user IS NULL OR updated_by_user IS NULL;

UPDATE business_hours SET
  created_by_user = COALESCE(created_by_user, @admin_user_id),
  updated_by_user = COALESCE(updated_by_user, created_by_user, @admin_user_id)
WHERE created_by_user IS NULL OR updated_by_user IS NULL;

UPDATE holidays SET
  created_by_user = COALESCE(created_by_user, @admin_user_id),
  updated_by_user = COALESCE(updated_by_user, created_by_user, @admin_user_id)
WHERE created_by_user IS NULL OR updated_by_user IS NULL;

UPDATE appointments SET
  created_by_user = COALESCE(created_by_user, @admin_user_id),
  updated_by_user = COALESCE(updated_by_user, created_by_user, @admin_user_id)
WHERE created_by_user IS NULL OR updated_by_user IS NULL;

UPDATE appointment_status_history SET
  created_by_user = COALESCE(created_by_user, changed_by_user, @admin_user_id),
  updated_by_user = COALESCE(updated_by_user, created_by_user, changed_by_user, @admin_user_id)
WHERE created_by_user IS NULL OR updated_by_user IS NULL;

UPDATE work_orders SET
  created_by_user = COALESCE(created_by_user, @admin_user_id),
  updated_by_user = COALESCE(updated_by_user, created_by_user, @admin_user_id)
WHERE created_by_user IS NULL OR updated_by_user IS NULL;

UPDATE work_order_services SET
  created_by_user = COALESCE(created_by_user, @admin_user_id),
  updated_by_user = COALESCE(updated_by_user, created_by_user, @admin_user_id)
WHERE created_by_user IS NULL OR updated_by_user IS NULL;

UPDATE parts SET
  created_by_user = COALESCE(created_by_user, @admin_user_id),
  updated_by_user = COALESCE(updated_by_user, created_by_user, @admin_user_id)
WHERE created_by_user IS NULL OR updated_by_user IS NULL;

UPDATE work_order_parts SET
  created_by_user = COALESCE(created_by_user, @admin_user_id),
  updated_by_user = COALESCE(updated_by_user, created_by_user, @admin_user_id)
WHERE created_by_user IS NULL OR updated_by_user IS NULL;

UPDATE invoices SET
  created_by_user = COALESCE(created_by_user, @admin_user_id),
  updated_by_user = COALESCE(updated_by_user, created_by_user, @admin_user_id)
WHERE created_by_user IS NULL OR updated_by_user IS NULL;

UPDATE payments SET
  created_by_user = COALESCE(created_by_user, @admin_user_id),
  updated_by_user = COALESCE(updated_by_user, created_by_user, @admin_user_id)
WHERE created_by_user IS NULL OR updated_by_user IS NULL;

UPDATE notifications SET
  created_by_user = COALESCE(created_by_user, @admin_user_id),
  updated_by_user = COALESCE(updated_by_user, created_by_user, @admin_user_id)
WHERE created_by_user IS NULL OR updated_by_user IS NULL;

DELIMITER //

DROP TRIGGER IF EXISTS trg_users_audit_ins//
CREATE TRIGGER trg_users_audit_ins BEFORE INSERT ON users
FOR EACH ROW
BEGIN
  SET NEW.created_by_user = COALESCE(NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_users_audit_upd//
CREATE TRIGGER trg_users_audit_upd BEFORE UPDATE ON users
FOR EACH ROW
BEGIN
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, OLD.updated_by_user, OLD.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_roles_audit_ins//
CREATE TRIGGER trg_roles_audit_ins BEFORE INSERT ON roles
FOR EACH ROW
BEGIN
  SET NEW.created_by_user = COALESCE(NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_roles_audit_upd//
CREATE TRIGGER trg_roles_audit_upd BEFORE UPDATE ON roles
FOR EACH ROW
BEGIN
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, OLD.updated_by_user, OLD.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_user_roles_audit_ins//
CREATE TRIGGER trg_user_roles_audit_ins BEFORE INSERT ON user_roles
FOR EACH ROW
BEGIN
  SET NEW.created_by_user = COALESCE(NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_user_roles_audit_upd//
CREATE TRIGGER trg_user_roles_audit_upd BEFORE UPDATE ON user_roles
FOR EACH ROW
BEGIN
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, OLD.updated_by_user, OLD.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_customers_audit_ins//
CREATE TRIGGER trg_customers_audit_ins BEFORE INSERT ON customers
FOR EACH ROW
BEGIN
  SET NEW.created_by_user = COALESCE(NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_customers_audit_upd//
CREATE TRIGGER trg_customers_audit_upd BEFORE UPDATE ON customers
FOR EACH ROW
BEGIN
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, OLD.updated_by_user, OLD.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_employees_audit_ins//
CREATE TRIGGER trg_employees_audit_ins BEFORE INSERT ON employees
FOR EACH ROW
BEGIN
  SET NEW.created_by_user = COALESCE(NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_employees_audit_upd//
CREATE TRIGGER trg_employees_audit_upd BEFORE UPDATE ON employees
FOR EACH ROW
BEGIN
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, OLD.updated_by_user, OLD.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_vehicles_audit_ins//
CREATE TRIGGER trg_vehicles_audit_ins BEFORE INSERT ON vehicles
FOR EACH ROW
BEGIN
  SET NEW.created_by_user = COALESCE(NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_vehicles_audit_upd//
CREATE TRIGGER trg_vehicles_audit_upd BEFORE UPDATE ON vehicles
FOR EACH ROW
BEGIN
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, OLD.updated_by_user, OLD.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_services_audit_ins//
CREATE TRIGGER trg_services_audit_ins BEFORE INSERT ON services
FOR EACH ROW
BEGIN
  SET NEW.created_by_user = COALESCE(NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_services_audit_upd//
CREATE TRIGGER trg_services_audit_upd BEFORE UPDATE ON services
FOR EACH ROW
BEGIN
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, OLD.updated_by_user, OLD.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_business_hours_audit_ins//
CREATE TRIGGER trg_business_hours_audit_ins BEFORE INSERT ON business_hours
FOR EACH ROW
BEGIN
  SET NEW.created_by_user = COALESCE(NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_business_hours_audit_upd//
CREATE TRIGGER trg_business_hours_audit_upd BEFORE UPDATE ON business_hours
FOR EACH ROW
BEGIN
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, OLD.updated_by_user, OLD.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_holidays_audit_ins//
CREATE TRIGGER trg_holidays_audit_ins BEFORE INSERT ON holidays
FOR EACH ROW
BEGIN
  SET NEW.created_by_user = COALESCE(NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_holidays_audit_upd//
CREATE TRIGGER trg_holidays_audit_upd BEFORE UPDATE ON holidays
FOR EACH ROW
BEGIN
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, OLD.updated_by_user, OLD.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_appointments_audit_ins//
CREATE TRIGGER trg_appointments_audit_ins BEFORE INSERT ON appointments
FOR EACH ROW
BEGIN
  SET NEW.created_by_user = COALESCE(NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_appointments_audit_upd//
CREATE TRIGGER trg_appointments_audit_upd BEFORE UPDATE ON appointments
FOR EACH ROW
BEGIN
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, OLD.updated_by_user, OLD.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_appt_hist_audit_ins//
CREATE TRIGGER trg_appt_hist_audit_ins BEFORE INSERT ON appointment_status_history
FOR EACH ROW
BEGIN
  SET NEW.created_by_user = COALESCE(NEW.created_by_user, NEW.changed_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, NEW.created_by_user, NEW.changed_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_appt_hist_audit_upd//
CREATE TRIGGER trg_appt_hist_audit_upd BEFORE UPDATE ON appointment_status_history
FOR EACH ROW
BEGIN
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, OLD.updated_by_user, OLD.created_by_user,
    OLD.changed_by_user, (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_work_orders_audit_ins//
CREATE TRIGGER trg_work_orders_audit_ins BEFORE INSERT ON work_orders
FOR EACH ROW
BEGIN
  SET NEW.created_by_user = COALESCE(NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_work_orders_audit_upd//
CREATE TRIGGER trg_work_orders_audit_upd BEFORE UPDATE ON work_orders
FOR EACH ROW
BEGIN
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, OLD.updated_by_user, OLD.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_wos_audit_ins//
CREATE TRIGGER trg_wos_audit_ins BEFORE INSERT ON work_order_services
FOR EACH ROW
BEGIN
  SET NEW.created_by_user = COALESCE(NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_wos_audit_upd//
CREATE TRIGGER trg_wos_audit_upd BEFORE UPDATE ON work_order_services
FOR EACH ROW
BEGIN
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, OLD.updated_by_user, OLD.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_parts_audit_ins//
CREATE TRIGGER trg_parts_audit_ins BEFORE INSERT ON parts
FOR EACH ROW
BEGIN
  SET NEW.created_by_user = COALESCE(NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_parts_audit_upd//
CREATE TRIGGER trg_parts_audit_upd BEFORE UPDATE ON parts
FOR EACH ROW
BEGIN
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, OLD.updated_by_user, OLD.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_wop_audit_ins//
CREATE TRIGGER trg_wop_audit_ins BEFORE INSERT ON work_order_parts
FOR EACH ROW
BEGIN
  SET NEW.created_by_user = COALESCE(NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_wop_audit_upd//
CREATE TRIGGER trg_wop_audit_upd BEFORE UPDATE ON work_order_parts
FOR EACH ROW
BEGIN
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, OLD.updated_by_user, OLD.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_invoices_audit_ins//
CREATE TRIGGER trg_invoices_audit_ins BEFORE INSERT ON invoices
FOR EACH ROW
BEGIN
  SET NEW.created_by_user = COALESCE(NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_invoices_audit_upd//
CREATE TRIGGER trg_invoices_audit_upd BEFORE UPDATE ON invoices
FOR EACH ROW
BEGIN
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, OLD.updated_by_user, OLD.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_payments_audit_ins//
CREATE TRIGGER trg_payments_audit_ins BEFORE INSERT ON payments
FOR EACH ROW
BEGIN
  SET NEW.created_by_user = COALESCE(NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_payments_audit_upd//
CREATE TRIGGER trg_payments_audit_upd BEFORE UPDATE ON payments
FOR EACH ROW
BEGIN
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, OLD.updated_by_user, OLD.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_notifications_audit_ins//
CREATE TRIGGER trg_notifications_audit_ins BEFORE INSERT ON notifications
FOR EACH ROW
BEGIN
  SET NEW.created_by_user = COALESCE(NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, NEW.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DROP TRIGGER IF EXISTS trg_notifications_audit_upd//
CREATE TRIGGER trg_notifications_audit_upd BEFORE UPDATE ON notifications
FOR EACH ROW
BEGIN
  SET NEW.updated_by_user = COALESCE(NEW.updated_by_user, OLD.updated_by_user, OLD.created_by_user,
    (SELECT id_user FROM users WHERE email = 'admin@tt2025.local' LIMIT 1));
END//

DELIMITER ;
