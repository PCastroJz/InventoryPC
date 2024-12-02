package com.inventory.betojc.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.inventory.betojc.models.User;



@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    List<User> findByFirstname(String firstname);

    List<User> findByLastname(String lastname);

    List<User> findByFirstnameContainingAndLastnameContaining(String firstname, String lastname);
}