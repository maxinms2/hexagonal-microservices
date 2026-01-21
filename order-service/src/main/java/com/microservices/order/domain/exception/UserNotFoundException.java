package com.microservices.order.domain.exception;

/**
 * 👤 USER NOT FOUND EXCEPTION
 * 
 * Se lanza cuando el userId referenciado en una orden
 * no existe en el user-service.
 * 
 * Esta es una excepción de dominio porque valida
 * una regla de negocio: no se puede crear una orden
 * para un usuario que no existe en el sistema.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String userId) {
        super("Usuario no encontrado en el sistema: " + userId);
    }
}
