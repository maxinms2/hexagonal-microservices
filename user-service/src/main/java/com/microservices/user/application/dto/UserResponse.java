package com.microservices.user.application.dto;

import com.microservices.user.domain.model.User;

import java.time.LocalDateTime;

/**
 * 📤 USER RESPONSE - DTO de Salida
 * 
 * DTO que representa la respuesta al cliente.
 * 
 * ¿Por qué no devolver la entidad User directamente?
 * 1. Seguridad: No exponemos datos sensibles
 * 2. Control: Decidimos qué datos mostrar
 * 3. Estabilidad: Cambios en User no rompen la API
 * 4. Formato: Podemos formatear datos para el cliente
 */
public record UserResponse(
    String id,
    String email,
    String name,
    boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    
    /**
     * Factory Method: Crea un UserResponse desde un User
     * 
     * Este es el patrón de mapeo más simple.
     * Para proyectos más grandes, usa MapStruct.
     */
    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId().toString(),
            user.getEmail().value(),
            user.getName(),
            user.isActive(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
