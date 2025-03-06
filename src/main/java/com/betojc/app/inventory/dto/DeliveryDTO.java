package com.betojc.app.inventory.dto;

import com.betojc.app.inventory.model.Delivery;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for Product entity.
 */
public class DeliveryDTO {
    
    @NotBlank(message = "ID cannot be null or empty")
    private String id;

    @NotNull(message = "Delivery cannot be null")
    @Valid
    private Delivery delivery;

    /**
     * Default constructor required for serialization/deserialization.
     */
    public DeliveryDTO() {
        // Default constructor
    }

    /**
     * Parameterized constructor for creating ProductDTO instances.
     *
     * @param id      the unique identifier of the ProductDTO
     * @param delivery the associated Product entity
     */
    public DeliveryDTO(String id, Delivery delivery) {
        this.id = id;
        this.delivery = delivery;
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
    public Delivery getDelivery() {
        return delivery;
    }

    /**
     * Sets the associated Product entity.
     *
     * @param product the Product entity to set
     */
    public void setProduct(Delivery delivery) {
        this.delivery = delivery;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeliveryDTO that = (DeliveryDTO) o;

        if (!id.equals(that.id)) return false;
        return delivery.equals(that.delivery);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + delivery.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "DeliveryDTO{" +
                "id='" + id + '\'' +
                ", delivery=" + delivery +
                '}';
    }
}
