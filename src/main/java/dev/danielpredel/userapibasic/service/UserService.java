package dev.danielpredel.userapibasic.service;

import dev.danielpredel.userapibasic.dto.UserRequest;
import dev.danielpredel.userapibasic.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserService {
    UserResponse save(UserRequest dto);
    Page<UserResponse> findAll(Pageable pageable);
    UserResponse findById(Long id);
    UserResponse findByEmail(String email);
    UserResponse update(Long id, UserRequest dto);
    void deleteById(Long id);
}
