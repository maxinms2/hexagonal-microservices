package com.microservices.order.application.usecase;

import com.microservices.order.application.dto.CreateOrderRequest;
import com.microservices.order.application.dto.OrderResponse;

/**
 * 🎯 CREATE ORDER USE CASE - Puerto de Entrada (Input Port)
 * 
 * Define el contrato para crear una orden.
 * 
 * ¿Por qué una interface?
 * - Separa WHAT (qué hace) de HOW (cómo lo hace)
 * - Facilita testing (puedes hacer mocks)
 * - Permite múltiples implementaciones
 * - Sigue el principio de segregación de interfaces
 * 
 * En Arquitectura Hexagonal:
 * - Esta interface es el "Puerto de Entrada"
 * - El Controller es el "Adaptador de Entrada"
 * - El Service es la "Implementación"
 */
public interface CreateOrderUseCase {
    /**
     * Crea una nueva orden en el sistema
     * 
     * @param request Datos de la orden a crear
     * @return Orden creada
     * @throws IllegalArgumentException si los datos son inválidos
     */
    OrderResponse execute(CreateOrderRequest request);
}
