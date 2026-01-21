package com.microservices.order.application.usecase;

/**
 * 🗑️ DELETE ORDER USE CASE - Puerto de Entrada
 * 
 * Define el contrato para eliminar una orden del sistema.
 * 
 * Nota: En producción se recomienda usar "soft delete" en lugar de
 * eliminación física para mantener historial y auditoría.
 */
public interface DeleteOrderUseCase {
    /**
     * Elimina una orden del sistema
     * 
     * @param orderId ID de la orden a eliminar
     * @throws com.microservices.order.domain.exception.OrderNotFoundException si no existe
     */
    void execute(String orderId);
}
