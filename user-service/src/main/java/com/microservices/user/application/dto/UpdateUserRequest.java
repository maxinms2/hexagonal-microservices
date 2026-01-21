package com.microservices.user.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * 📝 UPDATE USER REQUEST - DTO de Actualización
 * 
 * DTO para actualizar los datos de un usuario.
 * Los campos son opcionales (pueden ser null).
 * Solo se actualizan los campos que no son null.
 */
public record UpdateUserRequest(
        
    @Email(message = "El formato del email es inválido")
    String email,
    
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    String name
) {
    /**
     * Verifica si hay algo para actualizar
     */
    public boolean hasUpdates() {
        return email != null || name != null;
    }
}
