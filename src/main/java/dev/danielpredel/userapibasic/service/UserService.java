package dev.danielpredel.userapibasic.service;

import dev.danielpredel.userapibasic.dto.UserRequest;
import dev.danielpredel.userapibasic.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse save(UserRequest dto);
    List<UserResponse> findAll();
    UserResponse findById(Long id);
    UserResponse findByEmail(String email);
    UserResponse update(Long id, UserRequest dto);
    void deleteById(Long id);
}
