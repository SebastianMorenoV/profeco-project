CREATE TABLE IF NOT EXISTS reportes (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    usuario_id      BIGINT       NOT NULL,
    comercio_id     BIGINT       NOT NULL,
    motivo          VARCHAR(50)  NOT NULL,
    descripcion     TEXT         NOT NULL,
    estatus         VARCHAR(30)  NOT NULL DEFAULT 'PENDIENTE',
    multa_id        BIGINT,
    fecha_creacion  DATETIME(6)  DEFAULT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS multas (
    id                     BIGINT       NOT NULL AUTO_INCREMENT,
    comercio_id            BIGINT       NOT NULL,
    motivo                 VARCHAR(50)  NOT NULL,
    descripcion            TEXT         NOT NULL,
    monto                  DECIMAL(12,2)NOT NULL,
    estatus                VARCHAR(20)  NOT NULL DEFAULT 'PENDIENTE',
    fecha_emision          DATETIME(6)  DEFAULT NULL,
    fecha_resolucion       DATETIME(6)  DEFAULT NULL,
    emitido_por_usuario_id BIGINT,
    reporte_id             BIGINT,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT IGNORE INTO reportes (id, usuario_id, comercio_id, motivo, descripcion, estatus, multa_id, fecha_creacion)
VALUES
    (1, 7, 1, 'Precios abusivos', 'La leche entera Lala cuesta 35 pesos en caja pero el cartel dice 24.50.', 'RESUELTO', 1, NOW() - INTERVAL 5 DAY),
    (2, 8, 2, 'Producto caducado', 'Pan blanco Bimbo caducado desde hace una semana en los estantes.', 'PENDIENTE', NULL, NOW() - INTERVAL 2 DAY),
    (3, 7, 3, 'Sin precios exhibidos', 'No tienen etiquetas de precio en los refrigeradores de refrescos.', 'PENDIENTE', NULL, NOW() - INTERVAL 1 DAY);

INSERT IGNORE INTO multas (id, comercio_id, motivo, descripcion, monto, estatus, fecha_emision, fecha_resolucion, emitido_por_usuario_id, reporte_id)
VALUES
    (1, 1, 'Precios abusivos', 'Diferencia excesiva entre precio exhibido y cobrado en lácteos.', 5500.00, 'PAGADA', NOW() - INTERVAL 4 DAY, NOW() - INTERVAL 2 DAY, 1, 1);
