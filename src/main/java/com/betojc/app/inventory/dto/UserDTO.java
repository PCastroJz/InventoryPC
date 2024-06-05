package com.betojc.app.inventory.dto;

import com.betojc.app.inventory.model.User;

public class UserDTO {
    private String id;
    private User user;

    public UserDTO(String id, User user) {
        this.id = id;
        this.user = user;
    }

    // Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
