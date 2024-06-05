package com.betojc.app.inventory.dto;

import com.betojc.app.inventory.model.Product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for Product entity.
 */
public class ProductDTO {
    
    @NotBlank(message = "ID cannot be null or empty")
    private String id;

    @NotNull(message = "Product cannot be null")
    @Valid
    private Product product;

    /**
     * Default constructor required for serialization/deserialization.
     */
    public ProductDTO() {
        // Default constructor
    }

    /**
     * Parameterized constructor for creating ProductDTO instances.
     *
     * @param id      the unique identifier of the ProductDTO
     * @param product the associated Product entity
     */
    public ProductDTO(String id, Product product) {
        this.id = id;
        this.product = product;
    }

    /**
     * Gets the unique identifier of the ProductDTO.
     *
     * @return the unique identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the ProductDTO.
     *
     * @param id the unique identifier to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the associated Product entity.
     *
     * @return the associated Product entity
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Sets the associated Product entity.
     *
     * @param product the Product entity to set
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductDTO that = (ProductDTO) o;

        if (!id.equals(that.id)) return false;
        return product.equals(that.product);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + product.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ProductDTO{" +
                "id='" + id + '\'' +
                ", product=" + product +
                '}';
    }
}
