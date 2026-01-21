package com.microservices.user.application.usecase;

import com.microservices.user.application.dto.CreateUserRequest;
import com.microservices.user.application.dto.UserResponse;

/**
 * 🎯 CREATE USER USE CASE - Puerto de Entrada (Input Port)
 * 
 * Define el contrato para crear un usuario.
 * 
 * ¿Por qué una interface?
 * - Separa WHAT (qué hace) de HOW (cómo lo hace)
 * - Facilita testing (puedes hacer mocks)
 * - Permite múltiples implementaciones
 * - Sigue el principio de segregación de interfaces
 * 
 * En Arquitectura Hexagonal:
 * - Esta interface es el "Puerto de Entrada"
 * - El Controller es el "Adaptador de Entrada"
 * - El Service es la "Implementación"
 */
public interface CreateUserUseCase {
    
    /**
     * Crea un nuevo usuario en el sistema
     * 
     * @param request Datos del usuario a crear
     * @return Usuario creado
     * @throws com.microservices.user.domain.exception.EmailAlreadyExistsException si el email ya existe
     * @throws IllegalArgumentException si los datos son inválidos
     */
    UserResponse execute(CreateUserRequest request);
}
