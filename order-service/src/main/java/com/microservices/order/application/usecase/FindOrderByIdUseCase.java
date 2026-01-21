package com.microservices.order.application.usecase;

import com.microservices.order.application.dto.OrderResponse;

/**
 * 🔍 FIND ORDER BY ID USE CASE - Puerto de Entrada
 * 
 * Define el contrato para buscar una orden por su ID.
 */
public interface FindOrderByIdUseCase {
    /**
     * Busca una orden por su identificador único
     * 
     * @param orderId ID de la orden a buscar
     * @return Orden encontrada
     * @throws com.microservices.order.domain.exception.OrderNotFoundException si no existe
     */
    OrderResponse execute(String orderId);
}
