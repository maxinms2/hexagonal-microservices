package com.microservices.order.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * 📥 CREATE ORDER REQUEST - DTO de Entrada
 * 
 * Este es un Data Transfer Object (DTO) que representa
 * los datos necesarios para crear una orden.
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
public record CreateOrderRequest(
        @NotBlank(message = "El userId es obligatorio")
        String userId,

        @NotNull(message = "El total es obligatorio")
        @Positive(message = "El total debe ser mayor que cero")
        BigDecimal totalAmount
) { }
