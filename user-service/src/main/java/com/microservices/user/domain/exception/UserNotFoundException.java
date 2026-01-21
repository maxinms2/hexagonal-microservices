package com.microservices.user.domain.exception;

/**
 * 🚫 USER NOT FOUND EXCEPTION
 * 
 * Excepción de dominio que se lanza cuando no se encuentra un usuario.
 * 
 * ¿Por qué crear excepciones personalizadas?
 * 1. Claridad: El nombre describe exactamente qué pasó
 * 2. Manejo específico: Puedes capturarla y manejarla diferente
 * 3. Dominio: Pertenece al lenguaje del negocio
 */
public class UserNotFoundException extends RuntimeException {
    
    public UserNotFoundException(String userId) {
        super("Usuario no encontrado con ID: " + userId);
    }
    
    public UserNotFoundException(String field, String value) {
        super(String.format("Usuario no encontrado con %s: %s", field, value));
    }
}
