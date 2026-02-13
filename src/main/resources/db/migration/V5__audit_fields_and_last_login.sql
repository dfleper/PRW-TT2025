/* ============================================================
   TURBO TALLER (TT2025) - FLYWAY V5__audit_fields_and_last_login.sql
   ------------------------------------------------------------
   Ajustes:
   - Añade trazabilidad (created_by/at, updated_by/at) a todas las tablas
   - Añade last_login_at a users
   ============================================================ */

-- USERS
ALTER TABLE users
  ADD COLUMN last_login_at   DATETIME NULL,
  ADD COLUMN created_by_user BIGINT UNSIGNED NULL,
  ADD COLUMN updated_by_user BIGINT UNSIGNED NULL,
  ADD KEY idx_users_created_by (created_by_user),
  ADD KEY idx_users_updated_by (updated_by_user),
  ADD CONSTRAINT fk_users_created_by
    FOREIGN KEY (created_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT fk_users_updated_by
    FOREIGN KEY (updated_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE;

-- ROLES
ALTER TABLE roles
  ADD COLUMN created_by_user BIGINT UNSIGNED NULL,
  ADD COLUMN created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN updated_by_user BIGINT UNSIGNED NULL,
  ADD COLUMN updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  ADD KEY idx_roles_created_by (created_by_user),
  ADD KEY idx_roles_updated_by (updated_by_user),
  ADD CONSTRAINT fk_roles_created_by
    FOREIGN KEY (created_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT fk_roles_updated_by
    FOREIGN KEY (updated_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE;

-- USER_ROLES
ALTER TABLE user_roles
  ADD COLUMN created_by_user BIGINT UNSIGNED NULL,
  ADD COLUMN created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN updated_by_user BIGINT UNSIGNED NULL,
  ADD COLUMN updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  ADD KEY idx_user_roles_created_by (created_by_user),
  ADD KEY idx_user_roles_updated_by (updated_by_user),
  ADD CONSTRAINT fk_user_roles_created_by
    FOREIGN KEY (created_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT fk_user_roles_updated_by
    FOREIGN KEY (updated_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE;

-- CUSTOMERS
ALTER TABLE customers
  ADD COLUMN created_by_user BIGINT UNSIGNED NULL,
  ADD COLUMN updated_by_user BIGINT UNSIGNED NULL,
  ADD KEY idx_customers_created_by (created_by_user),
  ADD KEY idx_customers_updated_by (updated_by_user),
  ADD CONSTRAINT fk_customers_created_by
    FOREIGN KEY (created_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT fk_customers_updated_by
    FOREIGN KEY (updated_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE;

-- EMPLOYEES
ALTER TABLE employees
  ADD COLUMN created_by_user BIGINT UNSIGNED NULL,
  ADD COLUMN updated_by_user BIGINT UNSIGNED NULL,
  ADD KEY idx_employees_created_by (created_by_user),
  ADD KEY idx_employees_updated_by (updated_by_user),
  ADD CONSTRAINT fk_employees_created_by
    FOREIGN KEY (created_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT fk_employees_updated_by
    FOREIGN KEY (updated_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE;

-- VEHICLES
ALTER TABLE vehicles
  ADD COLUMN created_by_user BIGINT UNSIGNED NULL,
  ADD COLUMN updated_by_user BIGINT UNSIGNED NULL,
  ADD KEY idx_vehicles_created_by (created_by_user),
  ADD KEY idx_vehicles_updated_by (updated_by_user),
  ADD CONSTRAINT fk_vehicles_created_by
    FOREIGN KEY (created_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT fk_vehicles_updated_by
    FOREIGN KEY (updated_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE;

-- SERVICES
ALTER TABLE services
  ADD COLUMN created_by_user BIGINT UNSIGNED NULL,
  ADD COLUMN updated_by_user BIGINT UNSIGNED NULL,
  ADD KEY idx_services_created_by (created_by_user),
  ADD KEY idx_services_updated_by (updated_by_user),
  ADD CONSTRAINT fk_services_created_by
    FOREIGN KEY (created_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT fk_services_updated_by
    FOREIGN KEY (updated_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE;

-- BUSINESS_HOURS
ALTER TABLE business_hours
  ADD COLUMN created_by_user BIGINT UNSIGNED NULL,
  ADD COLUMN created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN updated_by_user BIGINT UNSIGNED NULL,
  ADD COLUMN updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  ADD KEY idx_bh_created_by (created_by_user),
  ADD KEY idx_bh_updated_by (updated_by_user),
  ADD CONSTRAINT fk_bh_created_by
    FOREIGN KEY (created_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT fk_bh_updated_by
    FOREIGN KEY (updated_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE;

-- HOLIDAYS
ALTER TABLE holidays
  ADD COLUMN created_by_user BIGINT UNSIGNED NULL,
  ADD COLUMN created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN updated_by_user BIGINT UNSIGNED NULL,
  ADD COLUMN updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  ADD KEY idx_holidays_created_by (created_by_user),
  ADD KEY idx_holidays_updated_by (updated_by_user),
  ADD CONSTRAINT fk_holidays_created_by
    FOREIGN KEY (created_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT fk_holidays_updated_by
    FOREIGN KEY (updated_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE;

-- APPOINTMENTS (ya tiene created_by_user + created_at/updated_at)
ALTER TABLE appointments
  ADD COLUMN updated_by_user BIGINT UNSIGNED NULL,
  ADD KEY idx_appt_updated_by (updated_by_user),
  ADD CONSTRAINT fk_appt_updated_by
    FOREIGN KEY (updated_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE;

-- APPOINTMENT_STATUS_HISTORY (CORRECCIÓN: añadir los 4 campos transversales)
ALTER TABLE appointment_status_history
  ADD COLUMN created_by_user BIGINT UNSIGNED NULL,
  ADD COLUMN created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN updated_by_user BIGINT UNSIGNED NULL,
  ADD COLUMN updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  ADD KEY idx_hist_created_by (created_by_user),
  ADD KEY idx_hist_updated_by (updated_by_user),
  ADD CONSTRAINT fk_hist_created_by
    FOREIGN KEY (created_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT fk_hist_updated_by
    FOREIGN KEY (updated_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE;

-- (Opcional) backfill coherente con tu modelo actual
UPDATE appointment_status_history
SET created_by_user = changed_by_user,
    created_at      = fecha_cambio
WHERE created_by_user IS NULL;

-- WORK_ORDERS
ALTER TABLE work_orders
  ADD COLUMN created_by_user BIGINT UNSIGNED NULL,
  ADD COLUMN created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN updated_by_user BIGINT UNSIGNED NULL,
  ADD COLUMN updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  ADD KEY idx_wo_created_by (created_by_user),
  ADD KEY idx_wo_updated_by (updated_by_user),
  ADD CONSTRAINT fk_wo_created_by
    FOREIGN KEY (created_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT fk_wo_updated_by
    FOREIGN KEY (updated_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE;

-- WORK_ORDER_SERVICES
ALTER TABLE work_order_services
  ADD COLUMN created_by_user BIGINT UNSIGNED NULL,
  ADD COLUMN created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN updated_by_user BIGINT UNSIGNED NULL,
  ADD COLUMN updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  ADD KEY idx_wos_created_by (created_by_user),
  ADD KEY idx_wos_updated_by (updated_by_user),
  ADD CONSTRAINT fk_wos_created_by
    FOREIGN KEY (created_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT fk_wos_updated_by
    FOREIGN KEY (updated_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE;

-- PARTS
ALTER TABLE parts
  ADD COLUMN created_by_user BIGINT UNSIGNED NULL,
  ADD COLUMN updated_by_user BIGINT UNSIGNED NULL,
  ADD KEY idx_parts_created_by (created_by_user),
  ADD KEY idx_parts_updated_by (updated_by_user),
  ADD CONSTRAINT fk_parts_created_by
    FOREIGN KEY (created_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT fk_parts_updated_by
    FOREIGN KEY (updated_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE;

-- WORK_ORDER_PARTS
ALTER TABLE work_order_parts
  ADD COLUMN created_by_user BIGINT UNSIGNED NULL,
  ADD COLUMN created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN updated_by_user BIGINT UNSIGNED NULL,
  ADD COLUMN updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  ADD KEY idx_wop_created_by (created_by_user),
  ADD KEY idx_wop_updated_by (updated_by_user),
  ADD CONSTRAINT fk_wop_created_by
    FOREIGN KEY (created_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT fk_wop_updated_by
    FOREIGN KEY (updated_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE;

-- INVOICES
ALTER TABLE invoices
  ADD COLUMN created_by_user BIGINT UNSIGNED NULL,
  ADD COLUMN created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN updated_by_user BIGINT UNSIGNED NULL,
  ADD COLUMN updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  ADD KEY idx_invoices_created_by (created_by_user),
  ADD KEY idx_invoices_updated_by (updated_by_user),
  ADD CONSTRAINT fk_invoices_created_by
    FOREIGN KEY (created_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT fk_invoices_updated_by
    FOREIGN KEY (updated_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE;

-- PAYMENTS
ALTER TABLE payments
  ADD COLUMN created_by_user BIGINT UNSIGNED NULL,
  ADD COLUMN created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN updated_by_user BIGINT UNSIGNED NULL,
  ADD COLUMN updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  ADD KEY idx_payments_created_by (created_by_user),
  ADD KEY idx_payments_updated_by (updated_by_user),
  ADD CONSTRAINT fk_payments_created_by
    FOREIGN KEY (created_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT fk_payments_updated_by
    FOREIGN KEY (updated_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE;

-- NOTIFICATIONS (ya tiene created_at)
ALTER TABLE notifications
  ADD COLUMN created_by_user BIGINT UNSIGNED NULL,
  ADD COLUMN updated_by_user BIGINT UNSIGNED NULL,
  ADD COLUMN updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  ADD KEY idx_notif_created_by (created_by_user),
  ADD KEY idx_notif_updated_by (updated_by_user),
  ADD CONSTRAINT fk_notif_created_by
    FOREIGN KEY (created_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT fk_notif_updated_by
    FOREIGN KEY (updated_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE;
