package com.microservices.order.infrastructure.adapter.output.client;

import com.microservices.order.application.port.output.UserValidationPort;
import com.microservices.order.domain.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Adaptador de infraestructura que valida usuarios usando HTTP Interfaces.
 * Implementa el puerto de salida {@link UserValidationPort} para mantener la arquitectura hexagonal.
 */
public class UserValidationAdapter implements UserValidationPort {

    private static final Logger log = LoggerFactory.getLogger(UserValidationAdapter.class);

    private final UserServiceClient userServiceClient;

    public UserValidationAdapter(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    @Override
    public void validateUserExists(String userId) {
        try {
            var user = userServiceClient.getUserById(userId);
            log.info("Usuario validado en user-service: {} ({})", user.name(), user.email());
        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("Usuario no encontrado en user-service: {}", userId);
            throw new UserNotFoundException(userId);
        } catch (Exception ex) {
            log.error("Error al validar usuario en user-service", ex);
            throw new RuntimeException("Error comunicándose con user-service. Intenta más tarde.", ex);
        }
    }
}