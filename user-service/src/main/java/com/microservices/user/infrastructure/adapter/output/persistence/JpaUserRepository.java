package com.microservices.user.infrastructure.adapter.output.persistence;

import com.microservices.user.infrastructure.adapter.output.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 🗄️ JPA USER REPOSITORY
 * 
 * Interface de Spring Data JPA que proporciona operaciones CRUD automáticas.
 * 
 * Spring Data JPA genera la implementación automáticamente.
 * Solo defines los métodos, Spring los implementa.
 * 
 * Esta NO es la interface del dominio (UserRepository).
 * Esta es la interface técnica de JPA.
 */
@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {
    
    /**
     * Busca un usuario por email
     * Query generada automáticamente: SELECT * FROM users WHERE email = ?
     */
    Optional<UserEntity> findByEmail(String email);
    
    /**
     * Verifica si existe un usuario con el email
     * Query: SELECT COUNT(*) FROM users WHERE email = ?
     */
    boolean existsByEmail(String email);
    
    /**
     * Obtiene todos los usuarios activos
     * Query: SELECT * FROM users WHERE active = true
     */
    List<UserEntity> findByActiveTrue();
}
