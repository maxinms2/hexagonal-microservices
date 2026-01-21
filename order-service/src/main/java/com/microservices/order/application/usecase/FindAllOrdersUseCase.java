package com.microservices.order.application.usecase;

import com.microservices.order.application.dto.OrderResponse;

import java.util.List;

/**
 * 📋 FIND ALL ORDERS USE CASE - Puerto de Entrada
 * 
 * Define el contrato para obtener todas las órdenes del sistema.
 */
public interface FindAllOrdersUseCase {
    /**
     * Obtiene todas las órdenes del sistema
     * 
     * @return Lista de órdenes
     */
    List<OrderResponse> execute();
}
