package dev.danielpredel.userapibasic.service;

import dev.danielpredel.userapibasic.dto.UserRequest;
import dev.danielpredel.userapibasic.dto.UserResponse;
import dev.danielpredel.userapibasic.dto.UserUpdateRequest;
import dev.danielpredel.userapibasic.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserService {
    UserResponse save(UserRequest dto);
    Page<UserResponse> findAll(Pageable pageable);
    UserResponse findById(CustomUserDetails user, Long id);
    UserResponse update(Long id, Long userId, UserUpdateRequest dto);
    void deleteById(Long id);
}
