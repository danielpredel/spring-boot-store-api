package dev.danielpredel.userapibasic.entity;

import dev.danielpredel.userapibasic.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @Setter
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @Setter
    private List<OrderItem> orderItems;

    @Setter
    private BigDecimal totalAmount;

    @Setter
    private LocalDateTime purchaseDate;

    @Setter
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public Order() {}

    public Order(LocalDateTime purchaseDate, OrderStatus status) {
        this.purchaseDate = purchaseDate;
        this.status = status;
    }
}
