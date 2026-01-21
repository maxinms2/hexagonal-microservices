package com.microservices.user.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 📥 CREATE USER REQUEST - DTO de Entrada
 * 
 * Este es un Data Transfer Object (DTO) que representa
 * los datos necesarios para crear un usuario.
 * 
 * ¿Por qué usar DTOs?
 * 1. Desacopla la API de las entidades de dominio
 * 2. Permite validación de datos de entrada
 * 3. Controlas exactamente qué datos expones
 * 4. Puedes cambiar el dominio sin romper la API
 * 
 * Usamos un 'record' de Java 16+:
 * - Inmutable por defecto
 * - Constructor automático
 * - Getters automáticos
 * - toString(), equals(), hashCode() automáticos
 */
public record CreateUserRequest(
        
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email es inválido")
    String email,
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    String name
) {
    // El record ya genera:
    // - Constructor con validaciones
    // - email() getter
    // - name() getter
    // - toString(), equals(), hashCode()
}
