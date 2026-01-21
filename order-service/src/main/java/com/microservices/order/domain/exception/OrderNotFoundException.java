package com.microservices.order.domain.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String orderId) {
        super("Orden no encontrada: " + orderId);
    }
}
