package com.microservices.order.domain.model;

/**
 * 📊 ORDER STATUS - Enum de Estado
 * 
 * Estados posibles de una orden en el sistema.
 * 
 * Flujo normal:
 * CREATED → PAID
 * 
 * Flujo de cancelación:
 * CREATED → CANCELLED
 * 
 * Nota: Una orden CANCELLED no puede cambiar de estado.
 */
public enum OrderStatus {
    /** Orden creada pero no pagada */
    CREATED,
    
    /** Orden pagada exitosamente */
    PAID,
    
    /** Orden cancelada */
    CANCELLED
}
