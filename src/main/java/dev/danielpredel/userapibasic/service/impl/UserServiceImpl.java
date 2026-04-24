package dev.danielpredel.userapibasic.service.impl;

import dev.danielpredel.userapibasic.exception.EmailAlreadyExistsException;
import dev.danielpredel.userapibasic.mapper.UserMapper;
import dev.danielpredel.userapibasic.dto.UserRequest;
import dev.danielpredel.userapibasic.dto.UserResponse;
import dev.danielpredel.userapibasic.exception.ResourceNotFoundException;
import dev.danielpredel.userapibasic.model.User;
import dev.danielpredel.userapibasic.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private Long userCount = 1L;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse save(UserRequest dto) {
        if(existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException("Email Already Exists");
        }

        Long id = userCount++;
        User newUser = userMapper.toUser(id, dto);
        users.put(id, newUser);
        return userMapper.toUserResponse(newUser);
    }

    @Override
    public List<UserResponse> findAll() {
        return userMapper.toUserResponseList(users.values().stream().toList());
    }

    @Override
    public UserResponse findById(Long id) {
        User user = Optional.ofNullable(users.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse update(Long id, UserRequest dto) {
        if(!users.containsKey(id)) {
            throw new ResourceNotFoundException("User Not Found");
        }

        if(existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new EmailAlreadyExistsException("Email Already Exists");
        }

        User user = userMapper.toUser(id, dto);
        users.put(id, user);
        return userMapper.toUserResponse(user);
    }

    @Override
    public void deleteById(Long id) {
        if(!users.containsKey(id)) {
            throw new ResourceNotFoundException("User Not Found");
        }

        users.remove(id);
    }

    private boolean existsByEmail(String email) {
        return users.values().stream()
                .anyMatch(u -> u.getEmail().equals(email));
    }

    private boolean existsByEmailAndIdNot(String email, Long id) {
        return users.values().stream()
                .anyMatch(u -> u.getEmail().equals(email) && !u.getId().equals(id));
    }
}
