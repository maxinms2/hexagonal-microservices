package com.microservices.user.infrastructure.adapter.output.persistence;

import com.microservices.user.domain.model.Email;
import com.microservices.user.domain.model.User;
import com.microservices.user.domain.model.UserId;
import com.microservices.user.domain.repository.UserRepository;
import com.microservices.user.infrastructure.adapter.output.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 🔌 POSTGRES USER REPOSITORY ADAPTER - Adaptador de Salida
 * 
 * Esta clase implementa el puerto de salida (UserRepository del dominio)
 * usando JPA y PostgreSQL/H2.
 * 
 * Responsabilidades:
 * 1. Convertir entre User (dominio) y UserEntity (JPA)
 * 2. Delegar operaciones a JpaUserRepository
 * 3. Manejar la persistencia
 * 
 * En Arquitectura Hexagonal:
 * - UserRepository (interface) = Puerto de Salida
 * - Esta clase = Adaptador de Salida
 * - JpaUserRepository = Tecnología específica (JPA)
 * 
 * El dominio NO conoce esta clase, solo la interface.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class PostgresUserRepositoryAdapter implements UserRepository {
    
    private final JpaUserRepository jpaRepository;
    
    @Override
    public User save(User user) {
        log.debug("💾 Guardando usuario: {}", user.getId());
        
        // Convertir de dominio a entidad JPA
        UserEntity entity = toEntity(user);
        
        // Guardar en la base de datos
        UserEntity savedEntity = jpaRepository.save(entity);
        
        // Convertir de entidad JPA a dominio
        return toDomain(savedEntity);
    }
    
    @Override
    public Optional<User> findById(UserId id) {
        log.debug("🔍 Buscando usuario por ID: {}", id);
        
        return jpaRepository.findById(id.value())
                .map(this::toDomain);
    }
    
    @Override
    public Optional<User> findByEmail(Email email) {
        log.debug("🔍 Buscando usuario por email: {}", email.value());
        
        return jpaRepository.findByEmail(email.value())
                .map(this::toDomain);
    }
    
    @Override
    public List<User> findAllActive() {
        log.debug("📋 Obteniendo todos los usuarios activos");
        
        return jpaRepository.findByActiveTrue()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<User> findAll() {
        log.debug("📋 Obteniendo todos los usuarios");
        
        return jpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsByEmail(Email email) {
        log.debug("❓ Verificando existencia de email: {}", email.value());
        
        return jpaRepository.existsByEmail(email.value());
    }
    
    @Override
    public void deleteById(UserId id) {
        log.debug("🗑️ Eliminando usuario: {}", id);
        
        jpaRepository.deleteById(id.value());
    }
    
    @Override
    public long count() {
        return jpaRepository.count();
    }
    
    // ============================================
    // MAPPERS: Conversión entre Dominio y JPA
    // ============================================
    
    /**
     * Convierte User (dominio) → UserEntity (JPA)
     */
    private UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId().value());
        entity.setEmail(user.getEmail().value());
        entity.setName(user.getName());
        entity.setActive(user.isActive());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        return entity;
    }
    
    /**
     * Convierte UserEntity (JPA) → User (dominio)
     */
    private User toDomain(UserEntity entity) {
        return new User(
                new UserId(entity.getId()),
                new Email(entity.getEmail()),
                entity.getName(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.isActive()
        );
    }
}
