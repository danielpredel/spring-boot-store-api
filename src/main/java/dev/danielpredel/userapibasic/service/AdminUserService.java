package dev.danielpredel.userapibasic.service;

import dev.danielpredel.userapibasic.dto.AdminUserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface AdminUserService {
    Page<AdminUserResponse> findAll(Pageable pageable);
    AdminUserResponse findById(Long id);
}
