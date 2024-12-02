package com.inventory.betojc.models;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Timestamp createdAt;

    @Column(nullable = false)
    private Timestamp expireAt;

    @Column(nullable = false)
    private boolean isConfirmed;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    public Token() {
    }

    public Token(Long id, String token, Timestamp createdAt, Timestamp expireAt, boolean isConfirmed, User user) {
        this.id = id;
        this.token = token;
        this.createdAt = createdAt;
        this.expireAt = expireAt;
        this.isConfirmed = isConfirmed;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Timestamp expireAt) {
        this.expireAt = expireAt;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Token [id=" + id + ", token=" + token + ", createdAt=" + createdAt + ", expireAt=" + expireAt
                + ", isConfirmed=" + isConfirmed + ", user=" + user + "]";
    }

    
}
