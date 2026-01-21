package com.microservices.order.domain.model;

import java.util.UUID;

/**
 * 🆔 ORDER ID - Value Object
 * 
 * Representa el identificador único de una orden.
 * 
 * ¿Por qué usar un Value Object en lugar de String o UUID?
 * 1. Type Safety: No puedes mezclar un OrderId con un UserId
 * 2. Encapsulación: La lógica de generación está aquí
 * 3. Claridad: El código es más expresivo
 * 4. Validación: Puedes validar el formato
 */
public record OrderId(UUID value) {

    public OrderId {
        if (value == null) {
            throw new IllegalArgumentException("OrderId no puede ser null");
        }
    }

    public static OrderId of(String value) {
        try {
            return new OrderId(UUID.fromString(value));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("OrderId inválido: " + value, ex);
        }
    }

    public static OrderId generate() {
        return new OrderId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
