package dev.danielpredel.storeapi.service.impl;

import dev.danielpredel.storeapi.user.entity.User;
import dev.danielpredel.storeapi.exception.ResourceNotFoundException;
import dev.danielpredel.storeapi.user.repository.UserRepository;
import dev.danielpredel.storeapi.security.CustomUserDetails;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String email) throws ResourceNotFoundException {
        User user = userRepository.findByEmailAndActiveTrue(email)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        return new CustomUserDetails(user);
    }
}
