package dev.danielpredel.storeapi.service.impl;

import dev.danielpredel.storeapi.dto.UserUpdateRequest;
import dev.danielpredel.storeapi.exception.EmailAlreadyExistsException;
import dev.danielpredel.storeapi.mapper.UserMapper;
import dev.danielpredel.storeapi.dto.UserRequest;
import dev.danielpredel.storeapi.dto.UserResponse;
import dev.danielpredel.storeapi.exception.ResourceNotFoundException;
import dev.danielpredel.storeapi.user.entity.User;
import dev.danielpredel.storeapi.user.repository.UserRepository;
import dev.danielpredel.storeapi.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository ,UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse save(UserRequest dto) {
        if(userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException("Email Already Exists");
        }

        User newUser = userMapper.toEntity(dto);
        newUser.setPassword(passwordEncoder.encode(dto.password()));
        User savedUser = userRepository.save(newUser);
        return userMapper.toResponse(savedUser);
    }

    @Override
    @PreAuthorize("#id == authentication.principal.id")
    public UserResponse findById(Long id) {
        User user = userRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        return userMapper.toResponse(user);
    }

    @Override
    @PreAuthorize("#id == authentication.principal.id")
    @Transactional
    public UserResponse update(Long id, UserUpdateRequest dto) {
        User user = userRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        user.setName(dto.name());
        user.setAddress(dto.address());

        return userMapper.toResponse(user);
    }

    @Override
    @PreAuthorize("#id == authentication.principal.id")
    @Transactional
    public void deleteById(Long id) {
        User user = userRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        user.setActive(false);
    }
}
