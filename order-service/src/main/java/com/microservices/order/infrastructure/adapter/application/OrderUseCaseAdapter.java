package com.microservices.order.infrastructure.adapter.application;

import com.microservices.order.application.dto.CreateOrderRequest;
import com.microservices.order.application.dto.OrderResponse;
import com.microservices.order.application.dto.UpdateOrderStatusRequest;
import com.microservices.order.application.service.OrderService;
import com.microservices.order.application.usecase.CreateOrderUseCase;
import com.microservices.order.application.usecase.FindAllOrdersUseCase;
import com.microservices.order.application.usecase.FindOrderByIdUseCase;
import com.microservices.order.application.usecase.UpdateOrderStatusUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 🔌 ORDER USE CASE ADAPTER
 * 
 * Adaptador de infraestructura que expone los casos de uso de órdenes
 * y aplica las reglas transaccionales de Spring.
 * 
 * Este adaptador:
 * - Implementa múltiples interfaces de casos de uso
 * - Delega la lógica al OrderService
 * - Gestiona transacciones con @Transactional
 * - Es un @Service de Spring (gestionado por el contenedor)
 */
@Service
@Transactional(readOnly = true)
public class OrderUseCaseAdapter implements
        CreateOrderUseCase,
        FindOrderByIdUseCase,
        FindAllOrdersUseCase,
    UpdateOrderStatusUseCase {

    private final OrderService orderService;

    public OrderUseCaseAdapter(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    @Transactional
    public OrderResponse execute(CreateOrderRequest request) {
        return orderService.create(request);
    }

    @Override
    public OrderResponse execute(String orderId) {
        return orderService.findById(orderId);
    }

    @Override
    public List<OrderResponse> execute() {
        return orderService.findAll();
    }

    @Override
    @Transactional
    public OrderResponse execute(String orderId, UpdateOrderStatusRequest request) {
        return orderService.updateStatus(orderId, request);
    }
}
