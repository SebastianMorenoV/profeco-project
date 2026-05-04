package com.itson.ms_usuarios.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "wishlist_productos",
    uniqueConstraints = @UniqueConstraint(name = "uk_usuario_producto", columnNames = {"usuario_id", "producto_id"})
)
public class ProductoWishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    public ProductoWishlist() {}

    public ProductoWishlist(Long usuarioId, Long productoId) {
        this.usuarioId = usuarioId;
        this.productoId = productoId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
}
