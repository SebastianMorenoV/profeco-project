package com.itson.ms_usuarios.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "lista_compras",
    uniqueConstraints = @UniqueConstraint(name = "uk_usuario_item_local", columnNames = {"usuario_id", "id_local"})
)
public class ItemListaCompras {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "id_local", nullable = false)
    private Integer idLocal;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(nullable = false)
    private Boolean marcado = false;

    public ItemListaCompras() {}

    public ItemListaCompras(Long usuarioId, Integer idLocal, String nombre, Boolean marcado) {
        this.usuarioId = usuarioId;
        this.idLocal = idLocal;
        this.nombre = nombre;
        this.marcado = marcado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Integer getIdLocal() { return idLocal; }
    public void setIdLocal(Integer idLocal) { this.idLocal = idLocal; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Boolean getMarcado() { return marcado; }
    public void setMarcado(Boolean marcado) { this.marcado = marcado; }
}
