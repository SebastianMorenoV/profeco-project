package com.itson.ms_usuarios.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "comercios_favoritos",
    uniqueConstraints = @UniqueConstraint(name = "uk_usuario_comercio", columnNames = {"usuario_id", "comercio_id"})
)
public class ComercioFavorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "comercio_id", nullable = false)
    private Long comercioId;

    public ComercioFavorito() {}

    public ComercioFavorito(Long usuarioId, Long comercioId) {
        this.usuarioId = usuarioId;
        this.comercioId = comercioId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Long getComercioId() { return comercioId; }
    public void setComercioId(Long comercioId) { this.comercioId = comercioId; }
}
