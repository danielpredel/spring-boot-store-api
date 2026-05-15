package dev.danielpredel.userapibasic.repository;

import dev.danielpredel.userapibasic.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndActiveTrue(String email);
    Optional<User> findByIdAndActiveTrue(Long id);
}
