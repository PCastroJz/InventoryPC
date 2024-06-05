package com.betojc.app.inventory.dto;

import com.betojc.app.inventory.model.Lot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for Lot entity.
 */
public class LotDTO {

    @NotBlank(message = "ID cannot be null or empty")
    private String id;

    @NotNull(message = "Lot cannot be null")
    private Lot lot;

    /**
     * Default constructor required for Firebase.
     */
    public LotDTO() {
        // Default constructor required for Firebase
    }

    /**
     * Parameterized constructor for creating LotDTO instances.
     *
     * @param id  the unique identifier of the LotDTO
     * @param lot the associated Lot entity
     */
    public LotDTO(String id, Lot lot) {
        this.id = id;
        this.lot = lot;
    }

    /**
     * Gets the unique identifier of the LotDTO.
     *
     * @return the unique identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the LotDTO.
     *
     * @param id the unique identifier to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the associated Lot entity.
     *
     * @return the associated Lot entity
     */
    public Lot getLot() {
        return lot;
    }

    /**
     * Sets the associated Lot entity.
     *
     * @param lot the Lot entity to set
     */
    public void setLot(Lot lot) {
        this.lot = lot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LotDTO lotDTO = (LotDTO) o;

        if (!id.equals(lotDTO.id)) return false;
        return lot.equals(lotDTO.lot);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + lot.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "LotDTO{" +
                "id='" + id + '\'' +
                ", lot=" + lot +
                '}';
    }
}
