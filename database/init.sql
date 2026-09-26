CREATE TABLE IF NOT EXISTS machines (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(40) NOT NULL UNIQUE,
  name VARCHAR(120) NOT NULL,
  model VARCHAR(80) NOT NULL,
  horsepower INT NOT NULL,
  field_name VARCHAR(120) NOT NULL,
  status VARCHAR(40) NOT NULL,
  qr_code VARCHAR(120) NOT NULL,
  work_hours DECIMAL(10,2) NOT NULL DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS drivers (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(80) NOT NULL,
  license_no VARCHAR(80) NOT NULL,
  phone VARCHAR(40) NOT NULL,
  shift_name VARCHAR(40) NOT NULL,
  rest_day VARCHAR(40) NOT NULL,
  rating DECIMAL(3,2) NOT NULL DEFAULT 5
);

CREATE TABLE IF NOT EXISTS farm_tasks (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  task_type VARCHAR(40) NOT NULL,
  field_name VARCHAR(120) NOT NULL,
  area_mu DECIMAL(10,2) NOT NULL,
  estimated_hours DECIMAL(10,2) NOT NULL,
  status VARCHAR(40) NOT NULL,
  recommended_machine VARCHAR(40),
  recommended_driver VARCHAR(80)
);

CREATE TABLE IF NOT EXISTS work_records (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  machine_code VARCHAR(40) NOT NULL,
  driver_name VARCHAR(80) NOT NULL,
  work_date DATE NOT NULL,
  task_type VARCHAR(40) NOT NULL,
  actual_hours DECIMAL(10,2) NOT NULL,
  fuel_liters DECIMAL(10,2) NOT NULL,
  area_mu DECIMAL(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS maintenance_reminders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  machine_code VARCHAR(40) NOT NULL,
  title VARCHAR(160) NOT NULL,
  due_date DATE NOT NULL,
  remaining_hours INT NOT NULL,
  level_name VARCHAR(40) NOT NULL
);

INSERT INTO machines(code, name, model, horsepower, field_name, status, qr_code, work_hours)
VALUES ('NJ-2026-001', '东方红 1804', 'LX1804', 180, '北岭 1 号田', '作业中', 'QR-NJ-001', 284.5);
