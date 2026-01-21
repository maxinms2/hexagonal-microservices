package com.microservices.order.application.port.output;

/**
 * Puerto de salida para validar usuarios en sistemas externos.
 * La aplicación depende de esta interfaz y no de detalles de infraestructura.
 */
public interface UserValidationPort {

    /**
     * Valida que el usuario exista en el servicio externo.
     * 
     * @param userId identificador del usuario a validar
     */
    void validateUserExists(String userId);
}