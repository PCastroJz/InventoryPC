package com.betojc.app.inventory.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;

public class Ship {

    private int cantidad;

    private int jabas;

    @NotNull(message = "La fecha de inicio no puede ser nula")
    private Date fecha;

    @NotNull(message = "El producto no puede ser nulo")
    @Size(min = 1, message = "El producto no debe estar vacío")
    private String producto;

    @NotNull(message = "El repartidor no puede ser nulo")
    @Size(min = 1, message = "El repartidor no debe estar vacío")
    private String repartidor;

    // Constructor vacío necesario para Firebase
    public Ship() {
    }

    public Ship(int cantidad, int jabas, @NotNull(message = "La fecha de inicio no puede ser nula") Date fecha,
            @NotNull(message = "El producto no puede ser nulo") @Size(min = 1, message = "El producto no debe estar vacío") String producto,
            @NotNull(message = "El repartidor no puede ser nulo") @Size(min = 1, message = "El repartidor no debe estar vacío") String repartidor) {
        this.cantidad = cantidad;
        this.jabas = jabas;
        this.fecha = fecha;
        this.producto = producto;
        this.repartidor = repartidor;
    }

    // Getters y Setters

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getJabas() {
        return jabas;
    }

    public void setJabas(int jabas) {
        this.jabas = jabas;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getRepartidor() {
        return repartidor;
    }

    public void setRepartidor(String repartidor) {
        this.repartidor = repartidor;
    }

    @Override
    public String toString() {
        return "Ship [cantidad=" + cantidad + ", jabas=" + jabas + ", fecha=" + fecha + ", producto=" + producto
                + ", repartidor=" + repartidor + "]";
    }

    
}

