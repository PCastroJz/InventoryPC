package com.inventory.betojc.models;

import jakarta.persistence.*;
import java.util.List;
import java.sql.Timestamp;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private Boolean validEmail;
    private Boolean auth;
    private String firstname;
    private String lastname;
    private Integer palmImage;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "user")
    private List<Product> products;

    @OneToMany(mappedBy = "user")
    private List<Process> processes;

    @ManyToMany(mappedBy = "users")
    private List<Role> roles;

    public User() {
    }

    public User(Long id, String email, String password, Boolean validEmail, Boolean auth, String firstname,
            String lastname, Integer palmImage, Timestamp createdAt, Timestamp updatedAt, List<Product> products,
            List<Process> processes, List<Role> roles) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.validEmail = validEmail;
        this.auth = auth;
        this.firstname = firstname;
        this.lastname = lastname;
        this.palmImage = palmImage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.products = products;
        this.processes = processes;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getValidEmail() {
        return validEmail;
    }

    public void setValidEmail(Boolean validEmail) {
        this.validEmail = validEmail;
    }

    public Boolean getAuth() {
        return auth;
    }

    public void setAuth(Boolean auth) {
        this.auth = auth;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Integer getPalmImage() {
        return palmImage;
    }

    public void setPalmImage(Integer palmImage) {
        this.palmImage = palmImage;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Process> getProcesses() {
        return processes;
    }

    public void setProcesses(List<Process> processes) {
        this.processes = processes;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", email=" + email + ", password=" + password + ", validEmail=" + validEmail
                + ", auth=" + auth + ", firstname=" + firstname + ", lastname=" + lastname + ", palmImage=" + palmImage
                + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", products=" + products + ", processes="
                + processes + ", roles=" + roles + "]";
    }

}
