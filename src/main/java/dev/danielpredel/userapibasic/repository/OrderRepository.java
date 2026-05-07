package dev.danielpredel.userapibasic.repository;

import dev.danielpredel.userapibasic.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
