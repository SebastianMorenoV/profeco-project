package com.itson.notificaciones.dto;

public class OfertaEventDTO {
    private Long comercioId;
    private String titulo;
    private String descripcion;
    private Double precioOriginal;
    private Double precioOferta;
    private Double porcentajeDescuento;
    private String tipoPromocion;
    private Long productoId;

    public OfertaEventDTO() {}

    public OfertaEventDTO(Long comercioId, String titulo, String descripcion, Double precioOriginal, Double precioOferta, Double porcentajeDescuento, String tipoPromocion, Long productoId) {
        this.comercioId = comercioId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precioOriginal = precioOriginal;
        this.precioOferta = precioOferta;
        this.porcentajeDescuento = porcentajeDescuento;
        this.tipoPromocion = tipoPromocion;
        this.productoId = productoId;
    }

    // Getters
    public Long getComercioId() { return comercioId; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public Double getPrecioOriginal() { return precioOriginal; }
    public Double getPrecioOferta() { return precioOferta; }
    public Double getPorcentajeDescuento() { return porcentajeDescuento; }
    public String getTipoPromocion() { return tipoPromocion; }
    public Long getProductoId() { return productoId; }

    // Setters
    public void setComercioId(Long comercioId) { this.comercioId = comercioId; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setPrecioOriginal(Double precioOriginal) { this.precioOriginal = precioOriginal; }
    public void setPrecioOferta(Double precioOferta) { this.precioOferta = precioOferta; }
    public void setPorcentajeDescuento(Double porcentajeDescuento) { this.porcentajeDescuento = porcentajeDescuento; }
    public void setTipoPromocion(String tipoPromocion) { this.tipoPromocion = tipoPromocion; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
}