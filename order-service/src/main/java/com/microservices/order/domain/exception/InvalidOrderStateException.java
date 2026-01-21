package com.microservices.order.domain.exception;

import com.microservices.order.domain.model.OrderStatus;

public class InvalidOrderStateException extends RuntimeException {
    public InvalidOrderStateException(OrderStatus current, OrderStatus target) {
        super("No se puede cambiar el estado de " + current + " a " + target);
    }
}
