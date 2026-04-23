package dev.danielpredel.userapibasic.service;

import dev.danielpredel.userapibasic.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User save(User user);
    List<User> findAll();
    Optional<User> findById(Long id);
}
