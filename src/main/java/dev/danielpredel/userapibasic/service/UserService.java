package dev.danielpredel.userapibasic.service;

import dev.danielpredel.userapibasic.dto.UserRequest;
import dev.danielpredel.userapibasic.dto.UserResponse;
import dev.danielpredel.userapibasic.dto.UserUpdateRequest;
import dev.danielpredel.userapibasic.security.CustomUserDetails;


public interface UserService {
    UserResponse save(UserRequest dto);
    UserResponse findById(CustomUserDetails user, Long id);
    UserResponse update(Long id, Long userId, UserUpdateRequest dto);
    void deleteById(Long id, Long userId);
}
