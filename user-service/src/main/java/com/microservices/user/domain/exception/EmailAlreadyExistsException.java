package com.microservices.user.domain.exception;

/**
 * 🚫 EMAIL ALREADY EXISTS EXCEPTION
 * 
 * Excepción que se lanza cuando se intenta crear un usuario
 * con un email que ya existe en el sistema.
 * 
 * Esta es una regla de negocio: No pueden existir dos usuarios
 * con el mismo email.
 */
public class EmailAlreadyExistsException extends RuntimeException {
    
    public EmailAlreadyExistsException(String email) {
        super("Ya existe un usuario con el email: " + email);
    }
}
