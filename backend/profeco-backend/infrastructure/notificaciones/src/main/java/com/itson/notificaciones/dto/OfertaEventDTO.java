package com.itson.notificaciones.dto;
public class OfertaEventDTO {
    private String titulo;
    private String descripcion;
    private Double precioOriginal;
    private Double precioOferta;
    private Double porcentajeDescuento;

    public OfertaEventDTO() {}

    public OfertaEventDTO(String titulo, String descripcion, Double precioOriginal, Double precioOferta, Double porcentajeDescuento) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precioOriginal = precioOriginal;
        this.precioOferta = precioOferta;
        this.porcentajeDescuento = porcentajeDescuento;
    }

    // Getters
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public Double getPrecioOriginal() { return precioOriginal; }
    public Double getPrecioOferta() { return precioOferta; }
    public Double getPorcentajeDescuento() { return porcentajeDescuento; }

    // Setters
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setPrecioOriginal(Double precioOriginal) { this.precioOriginal = precioOriginal; }
    public void setPrecioOferta(Double precioOferta) { this.precioOferta = precioOferta; }
    public void setPorcentajeDescuento(Double porcentajeDescuento) { this.porcentajeDescuento = porcentajeDescuento; }
}