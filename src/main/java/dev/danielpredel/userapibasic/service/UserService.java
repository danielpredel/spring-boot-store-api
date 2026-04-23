package dev.danielpredel.userapibasic.service;

import dev.danielpredel.userapibasic.dto.UserRequestDTO;
import dev.danielpredel.userapibasic.dto.UserResponseDTO;

import java.util.List;

public interface UserService {
    UserResponseDTO save(UserRequestDTO dto);
    List<UserResponseDTO> findAll();
    UserResponseDTO findById(Long id);
    UserResponseDTO update(Long id, UserRequestDTO dto);
    void deleteById(Long id);
}
