-- ============================================================
-- SEED: Usuarios de prueba para PROFECO, COMERCIO y CONSUMIDOR
-- ============================================================
-- Se ejecuta automáticamente la PRIMERA VEZ que se crea el
-- contenedor de MySQL (vía /docker-entrypoint-initdb.d/).
--
-- Contraseña para TODOS los usuarios de prueba: Test1234
-- Hash BCrypt generado con cost 10
--
-- INSERT IGNORE evita errores si el email (UNIQUE) ya existe,
-- por lo que es seguro re-ejecutar sin crear duplicados.
-- ============================================================

-- Crear la tabla si no existe (Hibernate la crea después, pero
-- esto garantiza que los INSERTs no fallen en el primer arranque)
CREATE TABLE IF NOT EXISTS usuarios (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    activo          BIT(1)       NOT NULL DEFAULT b'1',
    apellido        VARCHAR(100) NOT NULL,
    email           VARCHAR(150) NOT NULL,
    fecha_registro  DATETIME(6)  DEFAULT NULL,
    nombre          VARCHAR(100) NOT NULL,
    password        VARCHAR(120) NOT NULL DEFAULT '',
    telefono        VARCHAR(20)  DEFAULT NULL,
    tipo_usuario    VARCHAR(20)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY UK_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ============================================================
-- USUARIOS PROFECO (Funcionarios)
-- ============================================================
INSERT IGNORE INTO usuarios (nombre, apellido, email, telefono, tipo_usuario, password, activo, fecha_registro)
VALUES
    ('Carlos',   'García López',    'carlos.garcia@profeco.gob.mx',    '5551001001', 'PROFECO',
     '$2a$10$aaiFz4.3MzHkeJoKdrObleEgb3Tpo7X5OTtYl3PlfawmFbYUlcHyy', b'1', NOW()),

    ('María',    'Rodríguez Pérez', 'maria.rodriguez@profeco.gob.mx',  '5551001002', 'PROFECO',
     '$2a$10$aaiFz4.3MzHkeJoKdrObleEgb3Tpo7X5OTtYl3PlfawmFbYUlcHyy', b'1', NOW()),

    ('Roberto',  'Hernández Díaz',  'roberto.hernandez@profeco.gob.mx','5551001003', 'PROFECO',
     '$2a$10$aaiFz4.3MzHkeJoKdrObleEgb3Tpo7X5OTtYl3PlfawmFbYUlcHyy', b'1', NOW());

-- ============================================================
-- USUARIOS COMERCIO (Dueños de establecimientos)
-- ============================================================
INSERT IGNORE INTO usuarios (nombre, apellido, email, telefono, tipo_usuario, password, activo, fecha_registro)
VALUES
    ('Luis',     'Martínez Soto',   'luis@mitienda.com',      '6441001001', 'COMERCIANTE',
     '$2a$10$aaiFz4.3MzHkeJoKdrObleEgb3Tpo7X5OTtYl3PlfawmFbYUlcHyy', b'1', NOW()),

    ('Ana',      'López Ruiz',      'ana@superahorro.com',    '6441001002', 'COMERCIANTE',
     '$2a$10$aaiFz4.3MzHkeJoKdrObleEgb3Tpo7X5OTtYl3PlfawmFbYUlcHyy', b'1', NOW()),

    ('Fernando', 'Castillo Vega',   'fernando@frescomart.com','6441001003', 'COMERCIANTE',
     '$2a$10$aaiFz4.3MzHkeJoKdrObleEgb3Tpo7X5OTtYl3PlfawmFbYUlcHyy', b'1', NOW());

-- ============================================================
-- USUARIOS CONSUMIDOR
-- ============================================================
INSERT IGNORE INTO usuarios (nombre, apellido, email, telefono, tipo_usuario, password, activo, fecha_registro)
VALUES
    ('Pedro',    'Sánchez Moreno',   'pedro.sanchez@gmail.com',  '6442001001', 'CONSUMIDOR',
     '$2a$10$aaiFz4.3MzHkeJoKdrObleEgb3Tpo7X5OTtYl3PlfawmFbYUlcHyy', b'1', NOW()),

    ('Laura',    'Torres Jiménez',   'laura.torres@gmail.com',   '6442001002', 'CONSUMIDOR',
     '$2a$10$aaiFz4.3MzHkeJoKdrObleEgb3Tpo7X5OTtYl3PlfawmFbYUlcHyy', b'1', NOW()),

    ('Miguel',   'Flores Ramírez',   'miguel.flores@hotmail.com','6442001003', 'CONSUMIDOR',
     '$2a$10$aaiFz4.3MzHkeJoKdrObleEgb3Tpo7X5OTtYl3PlfawmFbYUlcHyy', b'1', NOW());
