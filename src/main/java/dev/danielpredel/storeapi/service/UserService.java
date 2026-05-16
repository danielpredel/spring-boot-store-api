package dev.danielpredel.storeapi.service;

import dev.danielpredel.storeapi.dto.UserRequest;
import dev.danielpredel.storeapi.user.dto.UserResponse;
import dev.danielpredel.storeapi.user.dto.UserUpdateRequest;


public interface UserService {
    UserResponse save(UserRequest dto);
    UserResponse findById(Long id);
    UserResponse update(Long id, UserUpdateRequest dto);
    void deleteById(Long id);
}
