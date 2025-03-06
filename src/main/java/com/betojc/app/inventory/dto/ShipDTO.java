package com.betojc.app.inventory.dto;

import com.betojc.app.inventory.model.Ship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for Ship entity.
 */
public class ShipDTO {

    @NotBlank(message = "ID cannot be null or empty")
    private String id;

    @NotNull(message = "Ship cannot be null")
    private Ship ship;

    /**
     * Default constructor required for Firebase.
     */
    public ShipDTO() {
        // Default constructor required for Firebase
    }

    /**
     * Parameterized constructor for creating ShipDTO instances.
     *
     * @param id  the unique identifier of the ShipDTO
     * @param ship the associated Ship entity
     */
    public ShipDTO(String id, Ship ship) {
        this.id = id;
        this.ship = ship;
    }

    /**
     * Gets the unique identifier of the ShipDTO.
     *
     * @return the unique identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the ShipDTO.
     *
     * @param id the unique identifier to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the associated Ship entity.
     *
     * @return the associated Ship entity
     */
    public Ship getShip() {
        return ship;
    }

    /**
     * Sets the associated Ship entity.
     *
     * @param ship the Ship entity to set
     */
    public void setShip(Ship ship) {
        this.ship = ship;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShipDTO shipDTO = (ShipDTO) o;

        if (!id.equals(shipDTO.id)) return false;
        return ship.equals(shipDTO.ship);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + ship.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ShipDTO{" +
                "id='" + id + '\'' +
                ", ship=" + ship +
                '}';
    }
}
