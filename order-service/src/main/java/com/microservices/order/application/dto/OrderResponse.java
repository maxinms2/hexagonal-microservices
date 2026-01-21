package com.microservices.order.application.dto;

import com.microservices.order.domain.model.Order;
import com.microservices.order.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 📤 ORDER RESPONSE - DTO de Salida
 * 
 * Representa la información de una orden que se devuelve al cliente.
 * 
 * Ventajas:
 * - Oculta detalles de implementación del dominio
 * - Permite evolucionar el dominio sin romper contratos
 * - Formato consistente para todas las respuestas
 * 
 * El método from() es un patrón factory para convertir
 * entidades de dominio a DTOs de respuesta.
 */
public record OrderResponse(
        String id,
        UUID userId,
        BigDecimal totalAmount,
        OrderStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId().toString(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
