package com.ai.avance.data.repositories;

import com.ai.avance.data.entities.UserEntities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para acceder a la entidad UserEntity.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    
    /**
     * Busca un usuario por nombre de usuario.
     */
    Optional<UserEntity> findByUsername(String username);
    
    /**
     * Busca un usuario por correo electrónico.
     */
    Optional<UserEntity> findByEmail(String email);
    
    /**
     * Verifica si existe un usuario con el nombre de usuario dado.
     */
    Boolean existsByUsername(String username);
    
    /**
     * Verifica si existe un usuario con el correo electrónico dado.
     */
    Boolean existsByEmail(String email);
} 