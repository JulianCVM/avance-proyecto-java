package com.ai.avance.services;

import com.ai.avance.data.entities.UserEntities.UserEntity;
import com.ai.avance.data.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar usuarios.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Encuentra un usuario por su ID.
     */
    public Optional<UserEntity> findById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Encuentra un usuario por su nombre de usuario.
     */
    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
    
    /**
     * Encuentra un usuario por su correo electrónico.
     */
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    
    /**
     * Guarda un usuario.
     */
    @Transactional
    public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }
    
    /**
     * Devuelve todos los usuarios.
     */
    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }
    
    /**
     * Actualiza un usuario existente.
     */
    @Transactional
    public UserEntity update(UserEntity user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("El usuario debe tener un ID para ser actualizado");
        }
        return userRepository.save(user);
    }
    
    /**
     * Elimina un usuario por su ID.
     */
    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
    
    /**
     * Crear un nuevo usuario.
     */
    @Transactional
    public UserEntity createUser(UserEntity user) {
        // Verificar que el nombre de usuario y email no existan
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        // Encriptar contraseña
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    /**
     * Actualizar la última fecha de inicio de sesión de un usuario.
     */
    @Transactional
    public void updateLastLogin(UserEntity user) {
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }
}
