package com.inventory.betojc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventory.betojc.models.Shipping;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, Long>{
    
}
