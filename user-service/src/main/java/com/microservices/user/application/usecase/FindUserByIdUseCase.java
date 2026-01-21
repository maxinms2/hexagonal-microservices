package com.microservices.user.application.usecase;

import com.microservices.user.application.dto.UserResponse;

/**
 * 🔍 FIND USER BY ID USE CASE
 * 
 * Caso de uso para buscar un usuario por su ID
 */
public interface FindUserByIdUseCase {
    
    /**
     * Busca un usuario por su ID
     * 
     * @param userId ID del usuario
     * @return Usuario encontrado
     * @throws com.microservices.user.domain.exception.UserNotFoundException si no existe
     */
    UserResponse execute(String userId);
}
