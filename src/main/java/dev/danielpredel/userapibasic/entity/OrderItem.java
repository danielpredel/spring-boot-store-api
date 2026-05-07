package dev.danielpredel.userapibasic.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "order_item")
@Getter
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @Setter
    private Order order;

    @ManyToOne
    @Setter
    private Product product;

    @Setter
    private String productName;

    @Setter
    private int quantity;

    @Setter
    private BigDecimal priceAtPurchase;

    public OrderItem() {}

    public OrderItem(String productName, int quantity, BigDecimal priceAtPurchase) {
        this.productName = productName;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
    }
}
