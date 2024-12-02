package com.inventory.betojc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventory.betojc.models.Process;

@Repository
public interface ProcessRepository extends JpaRepository<Process, Long>{
}
