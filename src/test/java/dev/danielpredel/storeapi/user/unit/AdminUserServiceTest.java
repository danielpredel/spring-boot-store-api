package dev.danielpredel.storeapi.user.unit;

import dev.danielpredel.storeapi.common.enums.Role;
import dev.danielpredel.storeapi.common.exception.ResourceNotFoundException;
import dev.danielpredel.storeapi.user.dto.admin.AdminUserResponse;
import dev.danielpredel.storeapi.user.entity.User;
import dev.danielpredel.storeapi.user.mapper.UserMapper;
import dev.danielpredel.storeapi.user.repository.UserRepository;
import dev.danielpredel.storeapi.user.service.AdminUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AdminUserService adminUserService;

    @Test
    void shouldReturnPagedUsers() {
        Pageable pageable = PageRequest.of(0, 10);

        User user = new User();

        AdminUserResponse response =
                new AdminUserResponse(1L, "John", "john@test.com", Role.ADMIN, true);

        Page<User> userPage = new PageImpl<>(List.of(user));

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toAdminResponse(user)).thenReturn(response);

        Page<AdminUserResponse> result = adminUserService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("John", result.getContent().get(0).name());

        verify(userRepository).findAll(pageable);
        verify(userMapper).toAdminResponse(user);
    }

    @Test
    void shouldReturnUserById() {
        Long id = 1L;

        User user = new User();

        AdminUserResponse response =
                new AdminUserResponse(1L, "John", "john@test.com", Role.ADMIN, true);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toAdminResponse(user)).thenReturn(response);

        AdminUserResponse result = adminUserService.findById(id);

        assertNotNull(result);
        assertEquals("John", result.name());

        verify(userRepository).findById(id);
        verify(userMapper).toAdminResponse(user);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        Long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> adminUserService.findById(id));

        verify(userRepository).findById(id);
    }
}
