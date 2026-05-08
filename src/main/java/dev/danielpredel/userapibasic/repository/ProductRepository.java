package dev.danielpredel.userapibasic.repository;

import dev.danielpredel.userapibasic.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
