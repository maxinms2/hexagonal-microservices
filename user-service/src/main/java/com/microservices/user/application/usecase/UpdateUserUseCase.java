package com.microservices.user.application.usecase;

import com.microservices.user.application.dto.UpdateUserRequest;
import com.microservices.user.application.dto.UserResponse;

/**
 * 📝 UPDATE USER USE CASE
 * 
 * Caso de uso para actualizar un usuario
 */
public interface UpdateUserUseCase {
    
    /**
     * Actualiza los datos de un usuario
     * 
     * @param userId ID del usuario a actualizar
     * @param request Nuevos datos
     * @return Usuario actualizado
     * @throws com.microservices.user.domain.exception.UserNotFoundException si no existe
     */
    UserResponse execute(String userId, UpdateUserRequest request);
}
