package com.microservices.order.application.usecase;

import com.microservices.order.application.dto.OrderResponse;
import com.microservices.order.application.dto.UpdateOrderStatusRequest;

/**
 * 🔄 UPDATE ORDER STATUS USE CASE - Puerto de Entrada
 * 
 * Define el contrato para cambiar el estado de una orden.
 */
public interface UpdateOrderStatusUseCase {
    /**
     * Actualiza el estado de una orden
     * 
     * @param orderId ID de la orden
     * @param request Nuevo estado
     * @return Orden actualizada
     * @throws com.microservices.order.domain.exception.OrderNotFoundException si no existe
     * @throws com.microservices.order.domain.exception.InvalidOrderStateException si el cambio no es válido
     */
    OrderResponse execute(String orderId, UpdateOrderStatusRequest request);
}
