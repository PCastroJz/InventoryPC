package com.betojc.app.inventory.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;

public class Lot {

    private int cantidad;

    @NotNull(message = "La fecha de inicio no puede ser nula")
    private Date inicio;

    @NotNull(message = "La fecha de fin no puede ser nula")
    private Date fin;

    @NotNull(message = "El producto no puede ser nulo")
    @Size(min = 1, message = "El producto no debe estar vacío")
    private String producto;

    // Constructor vacío necesario para Firebase
    public Lot() {}

    public Lot(int cantidad, Date inicio, Date fin, String producto) {
        this.cantidad = cantidad;
        this.inicio = inicio;
        this.fin = fin;
        this.producto = producto;
    }

    // Getters y Setters
    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFin() {
        return fin;
    }

    public void setFin(Date fin) {
        this.fin = fin;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lot lot = (Lot) o;
        return cantidad == lot.cantidad &&
                Objects.equals(inicio, lot.inicio) &&
                Objects.equals(fin, lot.fin) &&
                Objects.equals(producto, lot.producto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cantidad, inicio, fin, producto);
    }

    @Override
    public String toString() {
        return "Lot{" +
                "cantidad=" + cantidad +
                ", inicio=" + inicio +
                ", fin=" + fin +
                ", producto='" + producto + '\'' +
                '}';
    }
}
