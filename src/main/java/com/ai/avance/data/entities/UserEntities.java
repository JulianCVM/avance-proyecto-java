package com.ai.avance.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Clases de entidad para usuarios.
 */
public class UserEntities {
    
    /**
     * Entidad que representa un usuario del sistema.
     */
    @Entity
    @Table(name = "users")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        
        @Column(nullable = false, unique = true, length = 50)
        private String username;
        
        @Column(nullable = false, unique = true)
        private String email;
        
        @Column(nullable = false)
        private String password;
        
        @Column(name = "first_name")
        private String firstName;
        
        @Column(name = "last_name")
        private String lastName;
        
        @Column(nullable = false)
        private boolean active;
        
        @Column(name = "created_at")
        private LocalDateTime createdAt;
        
        @Column(name = "last_login")
        private LocalDateTime lastLogin;
        
        @ElementCollection(fetch = FetchType.EAGER)
        @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
        @Column(name = "role")
        @Builder.Default
        private Set<String> roles = new HashSet<>();
        
        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
        @Builder.Default
        private List<AIEntities.AgentEntity> agents = new ArrayList<>();
        
        @PrePersist
        protected void onCreate() {
            createdAt = LocalDateTime.now();
        }
    }
    
    /**
     * Entidad que representa un token de restablecimiento de contraseña.
     */
    @Entity
    @Table(name = "password_reset_tokens")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PasswordResetTokenEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        
        @Column(nullable = false, unique = true)
        private String token;
        
        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false)
        private UserEntity user;
        
        @Column(name = "expiry_date", nullable = false)
        private LocalDateTime expiryDate;
        
        @Column(name = "is_used")
        private boolean used;
    }
}
