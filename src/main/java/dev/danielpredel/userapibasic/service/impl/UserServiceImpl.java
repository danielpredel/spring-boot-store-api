package dev.danielpredel.userapibasic.service.impl;

import dev.danielpredel.userapibasic.exception.EmailAlreadyExistsException;
import dev.danielpredel.userapibasic.mapper.UserMapper;
import dev.danielpredel.userapibasic.dto.UserRequest;
import dev.danielpredel.userapibasic.dto.UserResponse;
import dev.danielpredel.userapibasic.exception.ResourceNotFoundException;
import dev.danielpredel.userapibasic.entity.UserEntity;
import dev.danielpredel.userapibasic.repository.UserRepository;
import dev.danielpredel.userapibasic.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final Map<Long, UserEntity> usersById = new ConcurrentHashMap<>();
    private final Map<String, UserEntity> usersByEmail = new ConcurrentHashMap<>();

    public UserServiceImpl(UserRepository userRepository ,UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse save(UserRequest dto) {
        if(existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException("Email Already Exists");
        }

        UserEntity newUser = userMapper.toEntity(dto);
        UserEntity savedUser = userRepository.save(newUser);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public List<UserResponse> findAll() {
        return userMapper.toResponseList(userRepository.findAll());
    }

    @Override
    public UserResponse findById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse update(Long id, UserRequest dto) {
        if(!usersById.containsKey(id)) {
            throw new ResourceNotFoundException("User Not Found");
        }

        if(existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new EmailAlreadyExistsException("Email Already Exists");
        }

        UserEntity user = userMapper.toEntity(dto);

        usersById.put(id, user);
        usersByEmail.put(user.getEmail(), user);

        return userMapper.toResponse(user);
    }

    @Override
    public void deleteById(Long id) {
        if(!usersById.containsKey(id)) {
            throw new ResourceNotFoundException("User Not Found");
        }

        UserEntity deletedUser = usersById.remove(id);
        usersByEmail.remove(deletedUser.getEmail());
    }

    private boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private boolean existsByEmailAndIdNot(String email, Long id) {
        UserEntity u = usersByEmail.get(email);
        return u != null && !u.getId().equals(id);
    }
}
