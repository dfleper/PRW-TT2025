/* ============================================================
   TURBO TALLER (TT2025) - FLYWAY V1__init_schema.sql
   ------------------------------------------------------------
   Nota Flyway:
   - NO incluye: CREATE DATABASE, USE, DROPs, ni resets.
   - Este script debe ejecutarse 1 sola vez (migración V1).
   - La BBDD/schema (tt2025) debe venir definido por el datasource.
   ============================================================ */

-- ============================================================
-- A) USUARIOS Y SEGURIDAD (RBAC)
-- ============================================================

CREATE TABLE users (
  id_user            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  email              VARCHAR(120) NOT NULL,
  password_hash      VARCHAR(255) NOT NULL,
  nombre             VARCHAR(80)  NOT NULL,
  apellidos          VARCHAR(120) NOT NULL,
  telefono           VARCHAR(20)  NULL,
  activo             TINYINT(1)   NOT NULL DEFAULT 1,
  created_at         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
                                 ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_user),
  UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE roles (
  id_role            SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
  nombre             VARCHAR(30) NOT NULL,
  PRIMARY KEY (id_role),
  UNIQUE KEY uk_roles_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_roles (
  id_user            BIGINT UNSIGNED NOT NULL,
  id_role            SMALLINT UNSIGNED NOT NULL,
  PRIMARY KEY (id_user, id_role),
  KEY idx_user_roles_role (id_role),
  CONSTRAINT fk_user_roles_user
    FOREIGN KEY (id_user) REFERENCES users(id_user)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_user_roles_role
    FOREIGN KEY (id_role) REFERENCES roles(id_role)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- B) PERFILES (CLIENTE / EMPLEADO)
-- ============================================================

CREATE TABLE customers (
  id_customer        BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_user            BIGINT UNSIGNED NOT NULL,
  nif                VARCHAR(15)  NULL,
  direccion          VARCHAR(200) NULL,
  ciudad             VARCHAR(80)  NULL,
  cp                 VARCHAR(10)  NULL,
  created_at         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
                                 ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_customer),
  UNIQUE KEY uk_customers_user (id_user),
  KEY idx_customers_nif (nif),
  CONSTRAINT fk_customers_user
    FOREIGN KEY (id_user) REFERENCES users(id_user)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE employees (
  id_employee        BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_user            BIGINT UNSIGNED NOT NULL,
  tipo               ENUM('recepcionista','mecanico','jefe') NOT NULL,
  especialidad       VARCHAR(80) NULL,
  fecha_alta         DATE NULL,
  activo             TINYINT(1) NOT NULL DEFAULT 1,
  created_at         DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at         DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP
                               ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_employee),
  UNIQUE KEY uk_employees_user (id_user),
  KEY idx_employees_tipo (tipo),
  CONSTRAINT fk_employees_user
    FOREIGN KEY (id_user) REFERENCES users(id_user)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- C) DOMINIO DEL TALLER (VEHICULOS / SERVICIOS)
-- ============================================================

CREATE TABLE vehicles (
  id_vehicle         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_customer        BIGINT UNSIGNED NOT NULL,
  matricula          VARCHAR(12) NOT NULL,
  marca              VARCHAR(60) NULL,
  modelo             VARCHAR(60) NULL,
  anio               SMALLINT UNSIGNED NULL,
  combustible        VARCHAR(20) NULL,
  vin                VARCHAR(20) NULL,
  notas              TEXT NULL,
  activo             TINYINT(1) NOT NULL DEFAULT 1,
  created_at         DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at         DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP
                               ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_vehicle),
  UNIQUE KEY uk_vehicles_matricula (matricula),
  KEY idx_vehicles_customer (id_customer),
  KEY idx_vehicles_marca_modelo (marca, modelo),
  CONSTRAINT fk_vehicles_customer
    FOREIGN KEY (id_customer) REFERENCES customers(id_customer)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE services (
  id_service         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  codigo             VARCHAR(20) NOT NULL,
  nombre             VARCHAR(80) NOT NULL,
  descripcion        TEXT NULL,
  precio_base        DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  minutos_estimados  SMALLINT UNSIGNED NOT NULL DEFAULT 30,
  activo             TINYINT(1) NOT NULL DEFAULT 1,
  created_at         DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at         DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP
                               ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_service),
  UNIQUE KEY uk_services_codigo (codigo),
  KEY idx_services_activo (activo),
  KEY idx_services_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- D) DISPONIBILIDAD DEL TALLER (HORARIO / FESTIVOS)
-- ============================================================

CREATE TABLE business_hours (
  id_bh              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  dia_semana         TINYINT UNSIGNED NOT NULL,   -- 1..7
  apertura           TIME NULL,
  cierre             TIME NULL,
  cerrado            TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (id_bh),
  UNIQUE KEY uk_bh_dia (dia_semana),
  CONSTRAINT chk_bh_dia_semana CHECK (dia_semana BETWEEN 1 AND 7)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE holidays (
  id_holiday         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  fecha              DATE NOT NULL,
  descripcion        VARCHAR(120) NULL,
  PRIMARY KEY (id_holiday),
  UNIQUE KEY uk_holidays_fecha (fecha)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- E) CITAS (AGENDA)
-- ============================================================

CREATE TABLE appointments (
  id_appointment         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_customer            BIGINT UNSIGNED NOT NULL,
  id_vehicle             BIGINT UNSIGNED NOT NULL,
  id_service             BIGINT UNSIGNED NOT NULL,
  id_employee_asignado   BIGINT UNSIGNED NULL,
  created_by_user        BIGINT UNSIGNED NULL,
  inicio                 DATETIME NOT NULL,
  fin                    DATETIME NOT NULL,
  estado                 ENUM('pendiente','confirmada','en_curso','finalizada','cancelada')
                         NOT NULL DEFAULT 'pendiente',
  created_at             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                   ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_appointment),

  KEY idx_appt_customer (id_customer),
  KEY idx_appt_vehicle (id_vehicle),
  KEY idx_appt_service (id_service),
  KEY idx_appt_employee (id_employee_asignado),
  KEY idx_appt_inicio (inicio),
  KEY idx_appt_estado (estado),
  KEY idx_appt_employee_inicio (id_employee_asignado, inicio),

  CONSTRAINT fk_appt_customer
    FOREIGN KEY (id_customer) REFERENCES customers(id_customer)
    ON DELETE RESTRICT ON UPDATE CASCADE,

  CONSTRAINT fk_appt_vehicle
    FOREIGN KEY (id_vehicle) REFERENCES vehicles(id_vehicle)
    ON DELETE RESTRICT ON UPDATE CASCADE,

  CONSTRAINT fk_appt_service
    FOREIGN KEY (id_service) REFERENCES services(id_service)
    ON DELETE RESTRICT ON UPDATE CASCADE,

  CONSTRAINT fk_appt_employee
    FOREIGN KEY (id_employee_asignado) REFERENCES employees(id_employee)
    ON DELETE SET NULL ON UPDATE CASCADE,

  CONSTRAINT fk_appt_created_by
    FOREIGN KEY (created_by_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE,

  CONSTRAINT chk_appt_fechas CHECK (fin > inicio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE appointment_status_history (
  id_hist             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_appointment      BIGINT UNSIGNED NOT NULL,
  changed_by_user     BIGINT UNSIGNED NOT NULL,
  estado_anterior     VARCHAR(20) NULL,
  estado_nuevo        VARCHAR(20) NOT NULL,
  fecha_cambio        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  nota                TEXT NULL,
  PRIMARY KEY (id_hist),

  KEY idx_hist_appt (id_appointment),
  KEY idx_hist_user (changed_by_user),
  KEY idx_hist_fecha (fecha_cambio),

  CONSTRAINT fk_hist_appt
    FOREIGN KEY (id_appointment) REFERENCES appointments(id_appointment)
    ON DELETE CASCADE ON UPDATE CASCADE,

  CONSTRAINT fk_hist_user
    FOREIGN KEY (changed_by_user) REFERENCES users(id_user)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- F) ORDEN DE TRABAJO (EJECUCION REAL)
-- ============================================================

CREATE TABLE work_orders (
  id_work_order      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_appointment     BIGINT UNSIGNED NOT NULL,
  estado             ENUM('abierta','en_proceso','espera_piezas','cerrada')
                     NOT NULL DEFAULT 'abierta',
  opened_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  closed_at          DATETIME NULL,
  diagnostico        TEXT NULL,
  observaciones      TEXT NULL,
  PRIMARY KEY (id_work_order),
  UNIQUE KEY uk_work_orders_appt (id_appointment),

  KEY idx_wo_estado (estado),
  KEY idx_wo_opened_at (opened_at),

  CONSTRAINT fk_work_orders_appt
    FOREIGN KEY (id_appointment) REFERENCES appointments(id_appointment)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE work_order_services (
  id_wos             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_work_order      BIGINT UNSIGNED NOT NULL,
  id_service         BIGINT UNSIGNED NULL,
  descripcion        VARCHAR(200) NOT NULL,
  cantidad           SMALLINT UNSIGNED NOT NULL DEFAULT 1,
  precio_unit        DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  total              DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  PRIMARY KEY (id_wos),

  KEY idx_wos_wo (id_work_order),
  KEY idx_wos_service (id_service),

  CONSTRAINT fk_wos_wo
    FOREIGN KEY (id_work_order) REFERENCES work_orders(id_work_order)
    ON DELETE CASCADE ON UPDATE CASCADE,

  CONSTRAINT fk_wos_service
    FOREIGN KEY (id_service) REFERENCES services(id_service)
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE parts (
  id_part            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  sku                VARCHAR(40) NOT NULL,
  nombre             VARCHAR(120) NOT NULL,
  descripcion        TEXT NULL,
  precio_unit        DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  stock_qty          INT NOT NULL DEFAULT 0,
  activo             TINYINT(1) NOT NULL DEFAULT 1,
  created_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                               ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_part),
  UNIQUE KEY uk_parts_sku (sku),
  KEY idx_parts_nombre (nombre),
  KEY idx_parts_activo (activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE work_order_parts (
  id_wop             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_work_order      BIGINT UNSIGNED NOT NULL,
  id_part            BIGINT UNSIGNED NOT NULL,
  cantidad           SMALLINT UNSIGNED NOT NULL DEFAULT 1,
  precio_unit        DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  total              DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  PRIMARY KEY (id_wop),

  KEY idx_wop_wo (id_work_order),
  KEY idx_wop_part (id_part),

  CONSTRAINT fk_wop_wo
    FOREIGN KEY (id_work_order) REFERENCES work_orders(id_work_order)
    ON DELETE CASCADE ON UPDATE CASCADE,

  CONSTRAINT fk_wop_part
    FOREIGN KEY (id_part) REFERENCES parts(id_part)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- G) FACTURACION Y PAGOS
-- ============================================================

CREATE TABLE invoices (
  id_invoice         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_work_order      BIGINT UNSIGNED NOT NULL,
  numero_factura     VARCHAR(30) NOT NULL,
  fecha_emision      DATE NOT NULL,
  subtotal           DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  tipo_iva           DECIMAL(5,2)  NOT NULL DEFAULT 21.00,
  iva                DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  total              DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  estado             ENUM('pendiente','pagada','anulada') NOT NULL DEFAULT 'pendiente',
  PRIMARY KEY (id_invoice),
  UNIQUE KEY uk_invoices_wo (id_work_order),
  UNIQUE KEY uk_invoices_numero (numero_factura),
  KEY idx_invoices_fecha (fecha_emision),
  KEY idx_invoices_estado (estado),

  CONSTRAINT fk_invoices_wo
    FOREIGN KEY (id_work_order) REFERENCES work_orders(id_work_order)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE payments (
  id_payment         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_invoice         BIGINT UNSIGNED NOT NULL,
  metodo             ENUM('efectivo','tpv','online') NOT NULL,
  importe            DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  paid_at            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  referencia         VARCHAR(80) NULL,
  estado             ENUM('aceptado','fallido') NOT NULL DEFAULT 'aceptado',
  PRIMARY KEY (id_payment),

  KEY idx_payments_invoice (id_invoice),
  KEY idx_payments_paid_at (paid_at),

  CONSTRAINT fk_payments_invoice
    FOREIGN KEY (id_invoice) REFERENCES invoices(id_invoice)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- H) NOTIFICACIONES
-- ============================================================

CREATE TABLE notifications (
  id_notification     BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_user             BIGINT UNSIGNED NOT NULL,
  id_appointment      BIGINT UNSIGNED NULL,
  canal               ENUM('email','sms') NOT NULL,
  asunto              VARCHAR(120) NOT NULL,
  mensaje             TEXT NOT NULL,
  estado              ENUM('pendiente','enviada','error') NOT NULL DEFAULT 'pendiente',
  sent_at             DATETIME NULL,
  error_text          TEXT NULL,
  created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_notification),

  KEY idx_notif_user (id_user),
  KEY idx_notif_appt (id_appointment),
  KEY idx_notif_estado (estado),
  KEY idx_notif_created_at (created_at),

  CONSTRAINT fk_notif_user
    FOREIGN KEY (id_user) REFERENCES users(id_user)
    ON DELETE RESTRICT ON UPDATE CASCADE,

  CONSTRAINT fk_notif_appt
    FOREIGN KEY (id_appointment) REFERENCES appointments(id_appointment)
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- (OPCIONAL) TRIGGERS ANTI-SOLAPE PARA CITAS POR MECANICO
-- ============================================================

DELIMITER //

CREATE TRIGGER trg_appt_no_overlap_ins
BEFORE INSERT ON appointments
FOR EACH ROW
BEGIN
  IF NEW.id_employee_asignado IS NOT NULL THEN
    IF EXISTS (
      SELECT 1
      FROM appointments a
      WHERE a.id_employee_asignado = NEW.id_employee_asignado
        AND a.estado <> 'cancelada'
        AND NOT (NEW.fin <= a.inicio OR NEW.inicio >= a.fin)
    ) THEN
      SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Solape de citas: el mecanico ya tiene una cita en ese rango';
    END IF;
  END IF;
END//

CREATE TRIGGER trg_appt_no_overlap_upd
BEFORE UPDATE ON appointments
FOR EACH ROW
BEGIN
  IF NEW.id_employee_asignado IS NOT NULL THEN
    IF EXISTS (
      SELECT 1
      FROM appointments a
      WHERE a.id_employee_asignado = NEW.id_employee_asignado
        AND a.id_appointment <> NEW.id_appointment
        AND a.estado <> 'cancelada'
        AND NOT (NEW.fin <= a.inicio OR NEW.inicio >= a.fin)
    ) THEN
      SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Solape de citas: el mecanico ya tiene una cita en ese rango';
    END IF;
  END IF;
END//

DELIMITER ;
