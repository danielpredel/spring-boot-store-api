package dev.danielpredel.userapibasic.service.impl;

import dev.danielpredel.userapibasic.dto.AdminUserResponse;
import dev.danielpredel.userapibasic.entity.User;
import dev.danielpredel.userapibasic.exception.ResourceNotFoundException;
import dev.danielpredel.userapibasic.mapper.UserMapper;
import dev.danielpredel.userapibasic.repository.UserRepository;
import dev.danielpredel.userapibasic.service.AdminUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdminUserServiceImpl implements AdminUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public AdminUserServiceImpl(UserRepository userRepository , UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Page<AdminUserResponse> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toAdminResponse);
    }

    @Override
    public AdminUserResponse findById(Long id) {
        User searchedUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        return userMapper.toAdminResponse(searchedUser);
    }
}
