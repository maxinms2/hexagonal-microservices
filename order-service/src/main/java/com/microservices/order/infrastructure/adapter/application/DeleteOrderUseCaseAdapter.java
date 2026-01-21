package com.microservices.order.infrastructure.adapter.application;

import com.microservices.order.application.service.OrderService;
import com.microservices.order.application.usecase.DeleteOrderUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 🗑️ DELETE ORDER USE CASE ADAPTER
 * 
 * Adaptador separado para el caso de uso de eliminación.
 * Se separa para evitar conflictos de firmas de métodos execute().
 */
@Service
@Transactional(readOnly = true)
public class DeleteOrderUseCaseAdapter implements DeleteOrderUseCase {

    private final OrderService orderService;

    public DeleteOrderUseCaseAdapter(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    @Transactional
    public void execute(String orderId) {
        orderService.delete(orderId);
    }
}
