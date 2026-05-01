package dev.danielpredel.userapibasic.service.impl;

import dev.danielpredel.userapibasic.exception.EmailAlreadyExistsException;
import dev.danielpredel.userapibasic.mapper.UserMapper;
import dev.danielpredel.userapibasic.dto.UserRequest;
import dev.danielpredel.userapibasic.dto.UserResponse;
import dev.danielpredel.userapibasic.exception.ResourceNotFoundException;
import dev.danielpredel.userapibasic.entity.UserEntity;
import dev.danielpredel.userapibasic.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final Map<Long, UserEntity> usersById = new ConcurrentHashMap<>();
    private final Map<String, UserEntity> usersByEmail = new ConcurrentHashMap<>();
    private final AtomicLong userCount = new AtomicLong(1);

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse save(UserRequest dto) {
        if(existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException("Email Already Exists");
        }

        Long id = userCount.getAndIncrement();
        UserEntity newUser = userMapper.toUser(id, dto);

        usersById.put(id, newUser);
        usersByEmail.put(newUser.getEmail(), newUser);

        return userMapper.toUserResponse(newUser);
    }

    @Override
    public List<UserResponse> findAll() {
        return userMapper.toUserResponseList(usersById.values().stream().toList());
    }

    @Override
    public UserResponse findById(Long id) {
        UserEntity user = Optional.ofNullable(usersById.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse update(Long id, UserRequest dto) {
        if(!usersById.containsKey(id)) {
            throw new ResourceNotFoundException("User Not Found");
        }

        if(existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new EmailAlreadyExistsException("Email Already Exists");
        }

        UserEntity user = userMapper.toUser(id, dto);

        usersById.put(id, user);
        usersByEmail.put(user.getEmail(), user);

        return userMapper.toUserResponse(user);
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
        return usersByEmail.containsKey(email);
    }

    private boolean existsByEmailAndIdNot(String email, Long id) {
        UserEntity u = usersByEmail.get(email);
        return u != null && !u.getId().equals(id);
    }
}
