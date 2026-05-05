package com.itson.notificaciones.dto;

public class MultaEventDTO {
    private Long comercioId;
    private String motivo;
    private Double monto;
    private String descripcion;

    // Constructores, Getters y Setters
    public MultaEventDTO() {}
    public MultaEventDTO(Long comercioId, String motivo, Double monto, String descripcion) {
        this.comercioId = comercioId;
        this.motivo = motivo;
        this.monto = monto;
        this.descripcion = descripcion;
    }
    // (Añade los getters y setters aquí)

    public Long getComercioId() {
        return comercioId;
    }

    public void setComercioId(Long comercioId) {
        this.comercioId = comercioId;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    
}