package dev.danielpredel.userapibasic.service.impl;

import dev.danielpredel.userapibasic.dto.UserUpdateRequest;
import dev.danielpredel.userapibasic.exception.EmailAlreadyExistsException;
import dev.danielpredel.userapibasic.mapper.UserMapper;
import dev.danielpredel.userapibasic.dto.UserRequest;
import dev.danielpredel.userapibasic.dto.UserResponse;
import dev.danielpredel.userapibasic.exception.ResourceNotFoundException;
import dev.danielpredel.userapibasic.entity.User;
import dev.danielpredel.userapibasic.repository.UserRepository;
import dev.danielpredel.userapibasic.service.UserService;
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
        User user = userRepository.findByIdAndActive(id, true)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse update(Long id, Long userId, UserUpdateRequest dto) {
        if (!userId.equals(id)) {
            throw new ResourceNotFoundException("User Not Found");
        }

        User user = userRepository.findByIdAndActive(id, true)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        user.setName(dto.name());
        user.setAddress(dto.address());

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void deleteById(Long id, Long userId) {
        if(!userId.equals(id) || !userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User Not Found");
        }

        User user = userRepository.findByIdAndActive(id, true)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        user.setActive(false);
    }
}
