package com.microservices.order.application.dto;

import com.microservices.order.domain.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

/**
 * 🔄 UPDATE ORDER STATUS REQUEST - DTO de Entrada
 * 
 * Permite cambiar el estado de una orden existente.
 * 
 * Estados válidos:
 * - CREATED: Orden creada (inicial)
 * - PAID: Orden pagada
 * - CANCELLED: Orden cancelada
 * 
 * Regla de negocio:
 * Una orden CANCELLED no puede cambiar a otro estado.
 */
public record UpdateOrderStatusRequest(
        @NotNull(message = "El estado es obligatorio")
        OrderStatus status
) { }
