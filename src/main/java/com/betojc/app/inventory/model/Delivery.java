package com.betojc.app.inventory.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Objects;

public class Delivery {

    @NotBlank(message = "El nombre del repartidor no debe estar vacío ni contener solo espacios en blanco")
    @Size(min = 1, message = "El nombre del repartidor debe tener al menos 1 carácter")
    private String nombre;

    private Integer costo;

    // Constructor sin argumentos requerido para la deserialización de Firebase
    public Delivery() {}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCosto() {
        return costo;
    }

    public void setCosto(Integer costo) {
        this.costo = costo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Delivery delivery = (Delivery) o;
        return Objects.equals(nombre, delivery.nombre) &&
                Objects.equals(costo, delivery.costo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, costo);
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "nombre='" + nombre + '\'' +
                ", costo='" + costo + '\'' +
                '}';
    }
}
