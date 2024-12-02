package com.inventory.betojc.services;

import com.inventory.betojc.models.Token;
import com.inventory.betojc.repositories.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

import com.inventory.betojc.models.User;
import com.inventory.betojc.exceptions.TokenInvalidoException;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    public Token generarTokenConfirmacion(User usuario) {

        Token tokenExistente = tokenRepository.findByUser(usuario);

        if (tokenExistente != null) {
            tokenExistente.setToken(UUID.randomUUID().toString());
            tokenExistente.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            tokenExistente.setExpireAt(calcularFechaExpiracion());
            tokenExistente.setConfirmed(false);
            return tokenRepository.save(tokenExistente);
        } else {
            Token nuevoToken = new Token();
            nuevoToken.setToken(UUID.randomUUID().toString());
            nuevoToken.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            nuevoToken.setExpireAt(calcularFechaExpiracion());
            nuevoToken.setConfirmed(false);
            nuevoToken.setUser(usuario);
            return tokenRepository.save(nuevoToken);
        }
    }

    public Token confirmarToken(String tokenValor) {
        Token token = tokenRepository.findByToken(tokenValor);
        if (token != null && !token.isConfirmed() && token.getExpireAt().after(new Timestamp(System.currentTimeMillis()))) {
            token.setConfirmed(true);
            return tokenRepository.save(token);
        }else {
            throw new TokenInvalidoException("Token inválido o expirado."); 
        }
    }

    private Timestamp calcularFechaExpiracion() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        return new Timestamp(calendar.getTimeInMillis());
    }
}