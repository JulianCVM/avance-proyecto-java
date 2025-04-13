package com.ai.avance.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clases de entidad para usuarios.
 */
public class UserEntities {
    
    /**
     * Entidad que representa un usuario del sistema.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserEntity {
        private Long id;
        private String username;
        private String email;
        private String password;
        private boolean active;
    }
}
