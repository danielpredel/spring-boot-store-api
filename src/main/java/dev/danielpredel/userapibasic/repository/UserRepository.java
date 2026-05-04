package dev.danielpredel.userapibasic.repository;

import dev.danielpredel.userapibasic.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);
}
