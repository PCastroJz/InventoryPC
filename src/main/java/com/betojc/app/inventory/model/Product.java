package com.betojc.app.inventory.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Objects;


public class Product {

    @NotBlank(message = "El nombre del producto no debe estar vacío ni contener solo espacios en blanco")
    @Size(min = 1, message = "El nombre del producto debe tener al menos 1 carácter")
    private String nombre;

    @NotBlank(message = "La descripción del producto no debe estar vacía ni contener solo espacios en blanco")
    @Size(min = 1, message = "La descripción del producto debe tener al menos 1 carácter")
    private String descripcion;

    // Constructor sin argumentos requerido para la deserialización de Firebase
    public Product() {}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product producto = (Product) o;
        return Objects.equals(nombre, producto.nombre) &&
                Objects.equals(descripcion, producto.descripcion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, descripcion);
    }

    @Override
    public String toString() {
        return "Product{" +
                "nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
