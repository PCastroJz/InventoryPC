package com.inventory.betojc.models;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "shipping")
public class Shipping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Timestamp date;

    @Column(nullable = false)
    private Timestamp startTime;

    @Column(nullable = false)
    private Timestamp finishTime;

    @Column(nullable = false)
    private Integer crates;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "id_product")
    private Product product;

    public Shipping() {
    }

    public Shipping(Long id, Timestamp date, Timestamp startTime, Timestamp finishTime, Integer crates,
            Integer quantity, Product product) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.crates = crates;
        this.quantity = quantity;
        this.product = product;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Timestamp finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getCrates() {
        return crates;
    }

    public void setCrates(Integer crates) {
        this.crates = crates;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "Shipping [id=" + id + ", date=" + date + ", startTime=" + startTime + ", finishTime=" + finishTime
                + ", crates=" + crates + ", quantity=" + quantity + ", product=" + product + "]";
    }

    
}
