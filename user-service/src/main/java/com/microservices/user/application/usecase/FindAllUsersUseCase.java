package com.microservices.user.application.usecase;

import com.microservices.user.application.dto.UserResponse;

import java.util.List;

/**
 * 📋 FIND ALL USERS USE CASE
 * 
 * Caso de uso para obtener todos los usuarios
 */
public interface FindAllUsersUseCase {
    
    /**
     * Obtiene todos los usuarios activos
     * 
     * @return Lista de usuarios
     */
    List<UserResponse> execute();
}
