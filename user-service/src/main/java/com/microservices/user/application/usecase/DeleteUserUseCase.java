package com.microservices.user.application.usecase;

/**
 * 🗑️ DELETE USER USE CASE
 * 
 * Caso de uso para eliminar (desactivar) un usuario
 */
public interface DeleteUserUseCase {
    
    /**
     * Desactiva un usuario
     * 
     * @param userId ID del usuario a desactivar
     * @throws com.microservices.user.domain.exception.UserNotFoundException si no existe
     */
    void execute(String userId);
}
