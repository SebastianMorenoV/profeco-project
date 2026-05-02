-- =============================================
-- PROFECO SYSTEM — Database Schemas
-- Ejecutar en MySQL local antes de levantar los microservicios
-- =============================================

-- 1. BASE DE DATOS: USUARIOS
CREATE DATABASE IF NOT EXISTS usuarios_db;
USE usuarios_db;

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    tipo_usuario ENUM('CONSUMIDOR','COMERCIANTE','PROFECO') NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Seed data
INSERT INTO usuarios (nombre, apellido, email, telefono, tipo_usuario) VALUES
('Juan', 'Pérez', 'juan.perez@mail.com', '6441234567', 'CONSUMIDOR'),
('María', 'López', 'maria.lopez@mail.com', '6449876543', 'CONSUMIDOR'),
('Carlos', 'García', 'carlos.garcia@comercio.com', '6441112233', 'COMERCIANTE'),
('Ana', 'Martínez', 'ana.martinez@comercio.com', '6442223344', 'COMERCIANTE'),
('Roberto', 'Hernández', 'roberto.hdz@profeco.gob.mx', '6443334455', 'PROFECO');

-- =============================================

-- 2. BASE DE DATOS: COMERCIO
CREATE DATABASE IF NOT EXISTS comercio_db;
USE comercio_db;

CREATE TABLE IF NOT EXISTS comercios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_comercial VARCHAR(200) NOT NULL,
    razon_social VARCHAR(200),
    rfc VARCHAR(13),
    direccion VARCHAR(300) NOT NULL,
    ciudad VARCHAR(100) NOT NULL,
    estado VARCHAR(100) NOT NULL,
    codigo_postal VARCHAR(10),
    telefono VARCHAR(20),
    email VARCHAR(100),
    tipo_comercio ENUM('SUPERMERCADO','TIENDA_CONVENIENCIA','FARMACIA','MERCADO','DEPARTAMENTAL') NOT NULL,
    latitud DOUBLE,
    longitud DOUBLE,
    id_propietario BIGINT,
    activo BOOLEAN DEFAULT TRUE,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Seed data
INSERT INTO comercios (nombre_comercial, razon_social, rfc, direccion, ciudad, estado, codigo_postal, telefono, email, tipo_comercio, id_propietario) VALUES
('Soriana Hiper Obregón', 'Organización Soriana S.A.', 'SOR850101AAA', 'Blvd. Las Torres 100', 'Ciudad Obregón', 'Sonora', '85000', '6444101010', 'soriana@mail.com', 'SUPERMERCADO', 3),
('Ley Express Centro', 'Casa Ley S.A.', 'LEY900101BBB', 'Calle Sufragio 200', 'Ciudad Obregón', 'Sonora', '85010', '6444202020', 'ley@mail.com', 'SUPERMERCADO', 4),
('Farmacia Guadalajara Norte', 'Farmacias Guadalajara S.A.', 'FGU880101CCC', 'Av. Miguel Alemán 300', 'Ciudad Obregón', 'Sonora', '85020', '6444303030', 'farma@mail.com', 'FARMACIA', 4),
('Walmart Obregón', 'Walmart de México S.A.', 'WMX910101DDD', 'Blvd. Rodolfo Elías Calles 400', 'Ciudad Obregón', 'Sonora', '85030', '6444404040', 'walmart@mail.com', 'SUPERMERCADO', 3);

-- =============================================

-- 3. BASE DE DATOS: CATÁLOGO
CREATE DATABASE IF NOT EXISTS catalogo_db;
USE catalogo_db;

CREATE TABLE IF NOT EXISTS productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    marca VARCHAR(100),
    categoria ENUM('ALIMENTOS','BEBIDAS','HIGIENE','LIMPIEZA','MEDICAMENTOS','ELECTRONICA','ROPA','HOGAR') NOT NULL,
    codigo_barras VARCHAR(50),
    unidad_medida VARCHAR(20),
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS precios_productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    comercio_id BIGINT NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    fecha_reporte TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (producto_id) REFERENCES productos(id),
    UNIQUE KEY uk_producto_comercio (producto_id, comercio_id)
);

-- Seed data
INSERT INTO productos (nombre, descripcion, marca, categoria, codigo_barras, unidad_medida) VALUES
('Leche Entera 1L', 'Leche entera pasteurizada 1 litro', 'Lala', 'ALIMENTOS', '7501005102001', 'Litro'),
('Huevo Blanco 18 pzs', 'Charola de 18 huevos blancos', 'San Juan', 'ALIMENTOS', '7501005102002', 'Pieza'),
('Pan Blanco Grande', 'Pan de caja blanco 680g', 'Bimbo', 'ALIMENTOS', '7501005102003', 'Pieza'),
('Coca-Cola 600ml', 'Refresco de cola 600ml', 'Coca-Cola', 'BEBIDAS', '7501005102004', 'Mililitro'),
('Jabón de Tocador', 'Jabón antibacterial 150g', 'Escudo', 'HIGIENE', '7501005102005', 'Gramo'),
('Paracetamol 500mg', 'Tabletas de paracetamol 500mg 20 pzs', 'Genérico', 'MEDICAMENTOS', '7501005102006', 'Pieza');

-- Precios en diferentes comercios (comercio_id referencia comercio_db.comercios.id)
INSERT INTO precios_productos (producto_id, comercio_id, precio) VALUES
(1, 1, 28.50), (1, 2, 27.90), (1, 4, 29.00),
(2, 1, 62.90), (2, 2, 64.50), (2, 4, 59.90),
(3, 1, 45.00), (3, 2, 48.00), (3, 4, 43.50),
(4, 1, 18.00), (4, 2, 17.50), (4, 4, 19.00),
(5, 1, 32.00), (5, 3, 29.90), (5, 4, 33.50),
(6, 3, 45.00), (6, 4, 52.00);

-- =============================================

-- 4. BASE DE DATOS: OFERTAS
CREATE DATABASE IF NOT EXISTS ofertas_db;
USE ofertas_db;

CREATE TABLE IF NOT EXISTS ofertas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    comercio_id BIGINT NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT,
    precio_original DECIMAL(10,2) NOT NULL,
    precio_oferta DECIMAL(10,2) NOT NULL,
    porcentaje_descuento DECIMAL(5,2),
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    activa BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Seed data
INSERT INTO ofertas (comercio_id, titulo, descripcion, precio_original, precio_oferta, porcentaje_descuento, fecha_inicio, fecha_fin) VALUES
(1, '3x2 en Leche Lala', 'Lleva 3 litros de leche y paga solo 2', 85.50, 57.00, 33.33, '2026-05-01', '2026-05-15'),
(2, 'Huevo a precio especial', 'Charola de 18 huevos con descuento', 64.50, 49.90, 22.64, '2026-05-01', '2026-05-07'),
(4, 'Coca-Cola 2x1', 'Compra una Coca-Cola 600ml y llévate otra gratis', 38.00, 19.00, 50.00, '2026-05-02', '2026-05-10');

-- =============================================

-- 5. BASE DE DATOS: RESEÑAS
CREATE DATABASE IF NOT EXISTS resenias_db;
USE resenias_db;

CREATE TABLE IF NOT EXISTS resenias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    comercio_id BIGINT NOT NULL,
    calificacion INT NOT NULL,
    comentario TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_calificacion CHECK (calificacion BETWEEN 1 AND 5)
);

-- Seed data
INSERT INTO resenias (usuario_id, comercio_id, calificacion, comentario) VALUES
(1, 1, 4, 'Buen surtido y precios accesibles, pero a veces hay mucha fila.'),
(1, 2, 5, 'Excelente atención y siempre tienen todo lo que busco.'),
(2, 1, 3, 'Regular, los precios han subido mucho últimamente.'),
(2, 4, 4, 'Walmart siempre tiene los mejores precios en electrónica.');

-- =============================================

-- 6. BASE DE DATOS: MULTAS Y REPORTES
CREATE DATABASE IF NOT EXISTS multas_db;
USE multas_db;

CREATE TABLE IF NOT EXISTS reportes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    comercio_id BIGINT NOT NULL,
    motivo ENUM('PRECIO_EXCESIVO','PRODUCTO_ADULTERADO','PUBLICIDAD_ENGANOSA','NEGACION_SERVICIO','INCUMPLIMIENTO_OFERTA','OTRO') NOT NULL,
    descripcion TEXT NOT NULL,
    estatus ENUM('PENDIENTE','EN_REVISION','RESUELTA_CON_MULTA','RESUELTA_SIN_MULTA','RECHAZADA') NOT NULL DEFAULT 'PENDIENTE',
    multa_id BIGINT DEFAULT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS multas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    comercio_id BIGINT NOT NULL,
    motivo ENUM('PRECIO_EXCESIVO','PRODUCTO_ADULTERADO','PUBLICIDAD_ENGANOSA','NEGACION_SERVICIO','INCUMPLIMIENTO_OFERTA') NOT NULL,
    descripcion TEXT NOT NULL,
    monto DECIMAL(12,2) NOT NULL,
    estatus ENUM('PENDIENTE','PAGADA','APELACION','CANCELADA') NOT NULL DEFAULT 'PENDIENTE',
    fecha_emision TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_resolucion TIMESTAMP NULL,
    emitido_por_usuario_id BIGINT,
    reporte_id BIGINT DEFAULT NULL
);

-- Seed data: Reportes de consumidores
INSERT INTO reportes (usuario_id, comercio_id, motivo, descripcion, estatus, multa_id) VALUES
(1, 1, 'PRECIO_EXCESIVO', 'El kilo de tortilla lo venden a $35 cuando el precio tope es $22.', 'RESUELTA_CON_MULTA', 1),
(2, 4, 'PUBLICIDAD_ENGANOSA', 'Anunciaron 2x1 en Coca-Cola pero en caja cobraron ambas.', 'EN_REVISION', NULL),
(1, 2, 'NEGACION_SERVICIO', 'No me quisieron aceptar una devolución con ticket vigente.', 'PENDIENTE', NULL);

-- Seed data: Multas emitidas por PROFECO
INSERT INTO multas (comercio_id, motivo, descripcion, monto, estatus, emitido_por_usuario_id, reporte_id) VALUES
(1, 'PRECIO_EXCESIVO', 'Se detectó un incremento injustificado del 40% en productos de la canasta básica.', 50000.00, 'PENDIENTE', 5, 1),
(4, 'PUBLICIDAD_ENGANOSA', 'La promoción 2x1 anunciada no se respetó en caja.', 30000.00, 'APELACION', 5, NULL);