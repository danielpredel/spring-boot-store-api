package dev.danielpredel.storeapi.user.service;

import dev.danielpredel.storeapi.user.dto.admin.AdminUserResponse;
import dev.danielpredel.storeapi.user.entity.User;
import dev.danielpredel.storeapi.common.exception.ResourceNotFoundException;
import dev.danielpredel.storeapi.user.mapper.UserMapper;
import dev.danielpredel.storeapi.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdminUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public AdminUserService(UserRepository userRepository , UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public Page<AdminUserResponse> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toAdminResponse);
    }

    public AdminUserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        return userMapper.toAdminResponse(user);
    }
}
