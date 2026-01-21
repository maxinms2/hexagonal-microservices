package com.microservices.user.domain.model;

import java.util.UUID;

/**
 * 🆔 USER ID - Value Object
 * 
 * Representa el identificador único de un usuario.
 * 
 * ¿Por qué usar un Value Object en lugar de String o UUID?
 * 1. Type Safety: No puedes mezclar un UserId con un OrderId
 * 2. Encapsulación: La lógica de generación está aquí
 * 3. Claridad: El código es más expresivo
 * 4. Validación: Puedes validar el formato
 * 
 * Un Value Object:
 * - Es inmutable (final)
 * - Se compara por valor, no por referencia
 * - No tiene identidad propia
 */
public record UserId(UUID value) {
    
    /**
     * Constructor compacto con validación
     */
    public UserId {
        if (value == null) {
            throw new IllegalArgumentException("UserId no puede ser null");
        }
    }
    
    /**
     * Crea un UserId a partir de un String
     */
    public static UserId of(String value) {
        try {
            return new UserId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("UserId inválido: " + value);
        }
    }
    
    /**
     * Genera un nuevo UserId aleatorio
     */
    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }
    
    /**
     * Convierte a String para persistencia
     */
    @Override
    public String toString() {
        return value.toString();
    }
}
