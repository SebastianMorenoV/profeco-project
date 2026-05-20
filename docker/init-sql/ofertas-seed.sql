CREATE TABLE IF NOT EXISTS ofertas (
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    comercio_id          BIGINT       NOT NULL,
    titulo               VARCHAR(200) NOT NULL,
    descripcion          TEXT,
    precio_original      DECIMAL(10,2)NOT NULL,
    precio_oferta        DECIMAL(10,2)NOT NULL,
    porcentaje_descuento DECIMAL(5,2),
    fecha_inicio         DATE         NOT NULL,
    fecha_fin            DATE         NOT NULL,
    activa               BIT(1)       NOT NULL DEFAULT b'1',
    fecha_creacion       DATETIME(6)  DEFAULT NULL,
    tipo_promocion       VARCHAR(50),
    producto_id          BIGINT,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT IGNORE INTO ofertas (id, comercio_id, titulo, descripcion, precio_original, precio_oferta, porcentaje_descuento, fecha_inicio, fecha_fin, activa, fecha_creacion, tipo_promocion, producto_id)
VALUES
    (1, 1, 'Oferta Leche Lala', '¡Lleva tu leche entera Lala con descuento especial esta semana!', 24.50, 19.90, 18.77, CURDATE(), CURDATE() + INTERVAL 5 DAY, b'1', NOW(), 'Descuento directo', 1),
    (2, 2, 'Combo Desayuno', 'Pan Bimbo + Huevo San Juan a precio inigualable.', 130.00, 110.00, 15.38, CURDATE(), CURDATE() + INTERVAL 7 DAY, b'1', NOW(), 'Combo', 3),
    (3, 3, 'Refrescos Helados', 'Coca Cola 2.5L con descuento.', 38.00, 32.00, 15.79, CURDATE() - INTERVAL 2 DAY, CURDATE() + INTERVAL 2 DAY, b'1', NOW(), 'Descuento directo', 7);
