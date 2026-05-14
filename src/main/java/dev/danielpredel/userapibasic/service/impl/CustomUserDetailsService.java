package dev.danielpredel.userapibasic.service.impl;

import dev.danielpredel.userapibasic.entity.User;
import dev.danielpredel.userapibasic.exception.ResourceNotFoundException;
import dev.danielpredel.userapibasic.repository.UserRepository;
import dev.danielpredel.userapibasic.security.CustomUserDetails;
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
        User user = userRepository.findByEmailAndActive(email, true)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        return new CustomUserDetails(user);
    }
}
