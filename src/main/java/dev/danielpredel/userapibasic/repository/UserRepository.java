package dev.danielpredel.userapibasic.repository;

import dev.danielpredel.userapibasic.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
    Optional<UserEntity> findByEmail(String email);
}
