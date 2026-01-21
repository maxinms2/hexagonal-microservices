package com.microservices.order.infrastructure.adapter.output.client;

/**
 * 🔗 USER SERVICE CLIENT RESPONSE - DTO de Respuesta
 * 
 * Representa la información mínima que necesitamos del usuario
 * del user-service para validar que existe.
 * 
 * Este DTO mapea la respuesta JSON del user-service.
 */
public record UserResponse(
        String id,
        String email,
        String name
) { }
