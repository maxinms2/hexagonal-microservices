package com.microservices.user.domain.repository;

import com.microservices.user.domain.model.Email;
import com.microservices.user.domain.model.User;
import com.microservices.user.domain.model.UserId;

import java.util.List;
import java.util.Optional;

/**
 * 📦 USER REPOSITORY - Puerto de Salida (Output Port)
 * 
 * Esta es una INTERFACE que define las operaciones de persistencia.
 * 
 * ¿Por qué es una interface?
 * - El DOMINIO define QUÉ necesita (interface)
 * - La INFRAESTRUCTURA define CÓMO lo hace (implementación)
 * 
 * Esto permite:
 * 1. Cambiar de PostgreSQL a MongoDB sin tocar el dominio
 * 2. Hacer tests sin base de datos real
 * 3. Mantener el dominio independiente
 * 
 * Arquitectura Hexagonal:
 * Domain (esta interface) → Infrastructure (implementación)
 */
public interface UserRepository {
    
    /**
     * Guarda un usuario (crear o actualizar)
     * 
     * @param user Usuario a guardar
     * @return Usuario guardado con datos actualizados
     */
    User save(User user);
    
    /**
     * Busca un usuario por su ID
     * 
     * @param id ID del usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findById(UserId id);
    
    /**
     * Busca un usuario por su email
     * 
     * @param email Email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findByEmail(Email email);
    
    /**
     * Obtiene todos los usuarios activos
     * 
     * @return Lista de usuarios activos
     */
    List<User> findAllActive();
    
    /**
     * Obtiene todos los usuarios (activos e inactivos)
     * 
     * @return Lista de todos los usuarios
     */
    List<User> findAll();
    
    /**
     * Verifica si existe un usuario con el email dado
     * 
     * @param email Email a verificar
     * @return true si existe, false si no
     */
    boolean existsByEmail(Email email);
    
    /**
     * Elimina un usuario por su ID
     * 
     * @param id ID del usuario a eliminar
     */
    void deleteById(UserId id);
    
    /**
     * Cuenta el número total de usuarios
     * 
     * @return Número de usuarios
     */
    long count();
}
