package dev.danielpredel.storeapi.service;

import dev.danielpredel.storeapi.user.dto.admin.AdminUserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface AdminUserService {
    Page<AdminUserResponse> findAll(Pageable pageable);
    AdminUserResponse findById(Long id);
}
