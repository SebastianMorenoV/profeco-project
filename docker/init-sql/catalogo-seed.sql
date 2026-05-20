CREATE TABLE IF NOT EXISTS productos (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(200) NOT NULL,
    descripcion     TEXT,
    marca           VARCHAR(100),
    categoria       VARCHAR(50)  NOT NULL,
    codigo_barras   VARCHAR(50),
    unidad_medida   VARCHAR(20),
    activo          BIT(1)       NOT NULL DEFAULT b'1',
    fecha_creacion  DATETIME(6)  DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY UK_codigo_barras (codigo_barras)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS precios_productos (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    producto_id     BIGINT       NOT NULL,
    comercio_id     BIGINT       NOT NULL,
    precio          DECIMAL(10,2)NOT NULL,
    fecha_reporte   DATETIME(6)  DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY UK_prod_com (producto_id, comercio_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Seed products
INSERT IGNORE INTO productos (id, nombre, descripcion, marca, categoria, codigo_barras, unidad_medida, activo, fecha_creacion)
VALUES
    (1, 'Leche Entera 1L', 'Leche entera pasteurizada adicionada con vitaminas A y D', 'Lala', 'Lácteos', '7501020512341', 'Litro', b'1', NOW()),
    (2, 'Leche Entera 1L Premium', 'Leche entera ultrapasteurizada', 'Alpura', 'Lácteos', '7501020512342', 'Litro', b'1', NOW()),
    (3, 'Pan Blanco Grande 680g', 'Pan de caja blanco clásico', 'Bimbo', 'Panadería', '7501000111223', 'Pieza', b'1', NOW()),
    (4, 'Arroz Súper Extra 1kg', 'Arroz blanco grano largo súper extra', 'Verde Valle', 'Abarrotes', '7501003112345', 'Kg', b'1', NOW()),
    (5, 'Frijol Negro 1kg', 'Frijol negro querétaro seleccionado', 'Verde Valle', 'Abarrotes', '7501003112346', 'Kg', b'1', NOW()),
    (6, 'Aceite Vegetal 1L', 'Aceite comestible puro de soya', '1-2-3', 'Abarrotes', '7501005113456', 'Litro', b'1', NOW()),
    (7, 'Coca Cola Original 2.5L', 'Refresco de cola sabor original', 'Coca Cola', 'Bebidas', '7501011156789', 'Pieza', b'1', NOW()),
    (8, 'Detergente Polvo 1kg', 'Detergente multiusos en polvo biodegradable', 'Foca', 'Limpieza', '7501012345678', 'Kg', b'1', NOW()),
    (9, 'Jabón de Tocador 150g', 'Jabón neutro balanceado', 'Zest', 'Higiene', '7501021234567', 'Pieza', b'1', NOW()),
    (10, 'Huevo Blanco 30 piezas', 'Huevo blanco fresco de granja seleccionado', 'San Juan', 'Lácteos', '7501034567890', 'Pieza', b'1', NOW());

-- Seed prices
INSERT IGNORE INTO precios_productos (id, producto_id, comercio_id, precio, fecha_reporte)
VALUES
    (1, 1, 1, 24.50, NOW()),
    (2, 1, 2, 23.90, NOW()),
    (3, 1, 3, 25.00, NOW()),
    (4, 2, 1, 26.00, NOW()),
    (5, 2, 2, 25.50, NOW()),
    (6, 3, 1, 45.00, NOW()),
    (7, 3, 2, 42.50, NOW()),
    (8, 3, 3, 44.00, NOW()),
    (9, 4, 1, 36.50, NOW()),
    (10, 4, 2, 34.00, NOW()),
    (11, 5, 2, 38.00, NOW()),
    (12, 5, 3, 39.50, NOW()),
    (13, 6, 1, 41.00, NOW()),
    (14, 6, 2, 39.90, NOW()),
    (15, 7, 1, 38.00, NOW()),
    (16, 7, 3, 36.00, NOW()),
    (17, 8, 2, 35.00, NOW()),
    (18, 9, 3, 18.50, NOW()),
    (19, 10, 1, 85.00, NOW()),
    (20, 10, 2, 82.00, NOW());
