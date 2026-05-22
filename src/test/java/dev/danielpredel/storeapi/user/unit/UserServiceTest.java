package dev.danielpredel.storeapi.user.unit;

import dev.danielpredel.storeapi.common.exception.EmailAlreadyExistsException;
import dev.danielpredel.storeapi.common.exception.ResourceNotFoundException;
import dev.danielpredel.storeapi.user.dto.UserResponse;
import dev.danielpredel.storeapi.user.dto.UserUpdateRequest;
import dev.danielpredel.storeapi.user.dto.auth.RegisterRequest;
import dev.danielpredel.storeapi.user.dto.auth.RegisterResponse;
import dev.danielpredel.storeapi.user.entity.User;
import dev.danielpredel.storeapi.user.mapper.UserMapper;
import dev.danielpredel.storeapi.user.repository.UserRepository;
import dev.danielpredel.storeapi.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUserSuccessfully() {
        RegisterRequest request = mock(RegisterRequest.class);
        User user = new User();
        User savedUser = new User();
        RegisterResponse response = mock(RegisterResponse.class);

        when(request.email()).thenReturn("test@mail.com");
        when(request.password()).thenReturn("123");

        when(userRepository.existsByEmail("test@mail.com")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode("123")).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toRegisterResponse(savedUser)).thenReturn(response);

        RegisterResponse result = userService.save(request);

        assertNotNull(result);
        assertEquals("encodedPassword", user.getPassword());

        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        RegisterRequest request = mock(RegisterRequest.class);

        when(request.email()).thenReturn("test@mail.com");
        when(userRepository.existsByEmail("test@mail.com")).thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.save(request)
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldReturnUserById() {
        User user = new User();
        UserResponse response = mock(UserResponse.class);

        when(userRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(user));

        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.findById(1L);

        assertNotNull(result);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundById() {
        when(userRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.findById(1L)
        );
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        User user = new User();
        UserUpdateRequest request = mock(UserUpdateRequest.class);
        UserResponse response = mock(UserResponse.class);

        when(request.name()).thenReturn("John");
        when(request.address()).thenReturn("Street 123");

        when(userRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(user));

        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.update(1L, request);

        assertNotNull(result);
        assertEquals("John", user.getName());
        assertEquals("Street 123", user.getAddress());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingUser() {
        UserUpdateRequest request = mock(UserUpdateRequest.class);

        when(userRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.update(1L, request)
        );
    }

    @Test
    void shouldDisableUserSuccessfully() {
        User user = new User();
        user.setActive(true);

        when(userRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(user));

        userService.deleteById(1L);

        assertFalse(user.isActive());
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingUser() {
        when(userRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.deleteById(1L)
        );
    }
}
