package dev.danielpredel.userapibasic.service;

import dev.danielpredel.userapibasic.dto.UserRequest;
import dev.danielpredel.userapibasic.dto.UserResponse;
import dev.danielpredel.userapibasic.dto.UserUpdateRequest;


public interface UserService {
    UserResponse save(UserRequest dto);
    UserResponse findById(Long id);
    UserResponse update(Long id, UserUpdateRequest dto);
    void deleteById(Long id, Long userId);
}
