package com.inventory.betojc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import com.inventory.betojc.models.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByDescripcion(String descripcion);
}