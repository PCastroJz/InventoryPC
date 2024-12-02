package com.inventory.betojc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventory.betojc.models.Token;
import com.inventory.betojc.models.User;


@Repository
public interface TokenRepository extends JpaRepository<Token, Long>{
    Token findByToken(String token);
    Token findByUser(User user);
}
