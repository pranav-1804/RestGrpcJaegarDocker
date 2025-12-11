package com.restmds.rest_service.model;
import jakarta.persistence.*;
import org.hibernate.annotations.DialectOverride;

import java.util.Objects;


@Entity
@Table(name = "amazonOrders")

public class OrderData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    private String product;
    private Integer quantity;

    public OrderData(){};

    public OrderData(String product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        OrderData order = (OrderData) object;
        return Objects.equals(orderId, order.orderId) && Objects.equals(product, order.product) && Objects.equals(quantity, order.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, product, quantity);
    }
}