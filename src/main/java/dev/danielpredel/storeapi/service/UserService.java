package dev.danielpredel.storeapi.service;

import dev.danielpredel.storeapi.user.dto.auth.RegisterRequest;
import dev.danielpredel.storeapi.user.dto.UserResponse;
import dev.danielpredel.storeapi.user.dto.UserUpdateRequest;


public interface UserService {
    UserResponse save(RegisterRequest dto);
    UserResponse findById(Long id);
    UserResponse update(Long id, UserUpdateRequest dto);
    void deleteById(Long id);
}
