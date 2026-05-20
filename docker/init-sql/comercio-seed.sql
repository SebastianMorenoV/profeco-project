CREATE TABLE IF NOT EXISTS comercios (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    nombre_comercial  VARCHAR(200) NOT NULL,
    razon_social      VARCHAR(200),
    rfc               VARCHAR(13),
    direccion         VARCHAR(300) NOT NULL,
    ciudad            VARCHAR(100) NOT NULL,
    estado            VARCHAR(100) NOT NULL,
    codigo_postal     VARCHAR(10),
    telefono          VARCHAR(20),
    email             VARCHAR(100),
    tipo_comercio     VARCHAR(50)  NOT NULL,
    latitud           DOUBLE,
    longitud          DOUBLE,
    id_propietario    BIGINT,
    activo            BIT(1)       NOT NULL DEFAULT b'1',
    fecha_registro    DATETIME(6)  DEFAULT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT IGNORE INTO comercios (id, nombre_comercial, razon_social, rfc, direccion, ciudad, estado, codigo_postal, telefono, email, tipo_comercio, latitud, longitud, id_propietario, activo, fecha_registro)
VALUES
    (1, 'Supermercado Soriana Centro', 'Tiendas Soriana S.A. de C.V.', 'TSO991022AA1', 'Calle Miguel Alemán 200, Centro', 'Obregón', 'Sonora', '85000', '6444123456', 'sucursal.centro@soriana.com', 'Supermercado', 27.4939, -109.9322, 4, b'1', NOW()),
    (2, 'Bodega Aurrera Obregón', 'Nueva Wal-Mart de México S. de R.L. de C.V.', 'NWM970924BB2', 'Blvd. Rodolfo Elías Calles 1500, Bella Vista', 'Obregón', 'Sonora', '85130', '6444159874', 'contacto@aurrera.com.mx', 'Supermercado', 27.4812, -109.9451, 5, b'1', NOW()),
    (3, 'Abarrotes El Güero', 'Juan Pérez López', 'PELJ800510XX3', 'Calle Jalisco 405, Zona Norte', 'Obregón', 'Sonora', '85010', '6444102030', 'elguero@gmail.com', 'Tienda de Conveniencia', 27.5021, -109.9288, 6, b'1', NOW());
