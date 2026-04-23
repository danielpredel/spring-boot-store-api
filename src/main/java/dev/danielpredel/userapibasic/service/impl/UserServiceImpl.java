package dev.danielpredel.userapibasic.service.impl;

import dev.danielpredel.userapibasic.model.User;
import dev.danielpredel.userapibasic.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserServiceImpl implements UserService {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private Long userCount = 1L;

    @Override
    public User save(User user) {
        Long id = userCount++;
        User newUser = new User(id, user.getName(), user.getEmail(), user.getPassword(), user.getEmail());
        users.put(id, newUser);
        return newUser;
    }

    @Override
    public List<User> findAll() {
        return users.values().stream().toList();
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }
}
