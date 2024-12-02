package com.inventory.betojc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventory.betojc.models.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
}
