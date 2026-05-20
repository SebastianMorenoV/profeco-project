CREATE TABLE IF NOT EXISTS resenias (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    usuario_id      BIGINT       NOT NULL,
    comercio_id     BIGINT       NOT NULL,
    calificacion    INT          NOT NULL,
    comentario      TEXT,
    fecha_creacion  DATETIME(6)  DEFAULT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT IGNORE INTO resenias (id, usuario_id, comercio_id, calificacion, comentario, fecha_creacion)
VALUES
    (1, 7, 1, 4, 'Excelente surtido de lácteos, aunque a veces las cajas se llenan mucho.', NOW() - INTERVAL 10 DAY),
    (2, 8, 1, 5, 'Muy limpio y bien organizando.', NOW() - INTERVAL 8 DAY),
    (3, 7, 2, 3, 'Tienen buenos precios pero el servicio al cliente es bastante lento.', NOW() - INTERVAL 5 DAY),
    (4, 9, 3, 5, 'El Güero siempre tiene todo lo necesario para emergencias, muy recomendado.', NOW() - INTERVAL 1 DAY);
